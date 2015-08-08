package org.analogweb.core;

import static org.analogweb.core.DefaultApplicationProperties.defaultProperties;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.analogweb.Application;
import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProperties;
import org.analogweb.Disposable;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.ResponseContext.Response;
import org.analogweb.Server;
import org.analogweb.WebApplicationException;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.util.Assertion;
import org.analogweb.util.ClassCollector;
import org.analogweb.util.CollectionUtils;
import org.analogweb.util.FileClassCollector;
import org.analogweb.util.IOUtils;
import org.analogweb.util.JarClassCollector;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

public class DefaultServer implements Server {

    private static final Log log = Logs.getLog(DefaultServer.class);
    private static final int CR = 13;
    private static final int LF = 10;
    private static final byte[] CRLF = new byte[] { CR, LF };
    private final URI serverURI;
    private final Application app;
    private final ApplicationContext resolver;
    private final ApplicationProperties props;

    public DefaultServer(String uri) {
        this(URI.create(uri));
    }

    public DefaultServer(URI uri) {
        this(uri, new WebApplication());
    }

    public DefaultServer(URI uri, Application app) {
        this(uri, app, (ApplicationContext) null);
    }

    public DefaultServer(URI uri, Application app, ApplicationContext contextResolver) {
        this(uri, app, contextResolver, defaultProperties());
    }

    public DefaultServer(URI uri, Application app, ApplicationProperties props) {
        this(uri, app, null, props);
    }

    public DefaultServer(URI uri, Application app, ApplicationContext contextResolver,
            ApplicationProperties props) {
        Assertion.notNull(app, Application.class.getName());
        this.serverURI = uri;
        this.app = app;
        this.resolver = contextResolver;
        this.props = props;
    }

    @Override
    public void run() {
        ServerSocketChannel cnl = null;
        try {
            this.app.run(resolver, props, getClassCollectors(), Thread.currentThread()
                    .getContextClassLoader());
            cnl = SelectorProvider.provider().openServerSocketChannel();
            cnl.configureBlocking(false);
            cnl.socket().bind(new InetSocketAddress(this.serverURI.getPort()));
            final Selector sl = Selector.open();
            cnl.register(sl, SelectionKey.OP_ACCEPT);
            runInternal(sl);
        } catch (IOException e) {
            throw new ApplicationRuntimeException(e) {

                private static final long serialVersionUID = 1L;
            };
        } finally {
            IOUtils.closeQuietly(cnl);
        }
    }

    private Map<SelectableChannel, Handler> requests = Maps.newEmptyHashMap();

    private void runInternal(Selector sl) throws IOException {
        while (true) {
            while (sl.select() > 0) {
                Iterator<SelectionKey> keys = sl.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isAcceptable()) {
                        log.trace("Accepting socket.");
                        ServerSocketChannel ssock = (ServerSocketChannel) key.channel();
                        SocketChannel channel = ssock.accept();
                        channel.configureBlocking(false);
                        channel.register(sl, SelectionKey.OP_READ);
                    } else {
                        if (key.isReadable()) {
                            log.trace("Reading socket.");
                            SelectableChannel channel = key.channel();
                            Handler handler;
                            if (requests.containsKey(channel)) {
                                log.trace(String.format("Handler[key=%s] resolved.", channel));
                                handler = requests.get(channel);
                            } else {
                                log.trace(String.format("New Handler[key=%s] created.", channel));
                                handler = new Handler();
                                requests.put(channel, handler);
                            }
                            log.trace(String.format("Invoking Handler[key=%s]#read.", channel));
                            try {
                                handler.read(key);
                            } catch (RequestCancelledException e) {
                                sendStatus((SocketChannel) key.channel(), e.getStatus(),
                                        e.getBody());
                                requests.remove(channel);
                                log.trace(String.format("Handler[key=%s] removed.", channel));
                                log.trace(String.format("Currently %s requests remained.",
                                        requests.size()));
                            }
                        }
                        if (key.isValid() && key.isWritable()) {
                            SelectableChannel channel = key.channel();
                            boolean completed = false;
                            try {
                                log.trace(String.format("Invoking Handler[key=%s]#write.", channel));
                                completed = requests.get(channel).write(key);
                            } finally {
                                if (completed) {
                                    requests.remove(channel);
                                    log.trace(String.format("Handler[key=%s] removed.", channel));
                                    log.trace(String.format("Currently %s requests remained.",
                                            requests.size()));
                                }
                            }
                        }
                    }
                }
            }
            Iterator<SelectionKey> keys = sl.keys().iterator();
            while (keys.hasNext()) {
                keys.next().channel().close();
            }
        }
    }

    final class Handler {

        private RequestPath path;
        private Map<String, List<String>> headerMap;
        private RequestBody body;
        private boolean executed;

        public void read(SelectionKey key) throws IOException {
            SocketChannel sock = (SocketChannel) key.channel();
            sock.configureBlocking(false);
            ByteBuffer buf = ByteBuffer.allocate(8192);
            int read = sock.read(buf);
            log.trace(String.format("%s bytes readed.", read));
            if (read == 1) {
                // spinning.
                log.trace("spinning.");
                return;
            }
            buf.flip();
            if (read > 0) {
                // Read first.
                int eoh = 0;
                if (headerMap == null || path == null) {
                    eoh = endOfHeader(buf, read);
                    while (eoh == 0) {
                        buf.limit(8192);
                        read = sock.read(buf);
                        buf.flip();
                        eoh = endOfHeader(buf, read);
                    }
                    BufferedReader r = new BufferedReader(new InputStreamReader(
                            new ByteArrayInputStream(
                                    ((ByteBuffer) buf.duplicate().limit(eoh)).array())));
                    String aLine = r.readLine();
                    if (aLine == null) {
                        throw new RequestCancelledException(HttpStatus.BAD_REQUEST,
                                StringUtils.EMPTY);
                    }
                    StringTokenizer st = new StringTokenizer(aLine);
                    if (st.countTokens() < 2) {
                        throw new RequestCancelledException(HttpStatus.BAD_REQUEST,
                                StringUtils.EMPTY);
                    }
                    final String method = st.nextToken();
                    final URI uri = URI.create(st.nextToken());
                    path = new DefaultRequestPath(URI.create("/"), uri, method);
                    headerMap = Maps.newEmptyHashMap();
                    while ((aLine = r.readLine()) != null && aLine.trim().length() != 0) {
                        int i = aLine.indexOf(':');
                        if (i > 0 && i < aLine.length()) {
                            String k = aLine.substring(0, i).trim();
                            String value = aLine.substring(i + 1).trim();
                            headerMap.put(k, Arrays.asList(value));
                        }
                    }
                    log.trace(String.format("Request header resolved. %s", headerMap));
                }
                body = resolveRequestBody(sock, buf, eoh, read, path.getRequestMethod(),
                        headerMap.get("Content-Length"));
                if (body.resolved() && executed == false) {
                    log.trace(String.format("Invoke application."));
                    RequestContextImpl request = new RequestContextImpl(path, Locale.getDefault(),
                            headerMap, body);
                    ResponseContextImpl response = new ResponseContextImpl(request, sock);
                    try {
                        Response proceed = app.processRequest(request.getRequestPath(), request,
                                response);
                        if (proceed == Application.NOT_FOUND) {
                            throw new RequestCancelledException(HttpStatus.NOT_FOUND,
                                    StringUtils.EMPTY);
                        } else {
                            proceed.commit(request, response);
                        }
                    } catch (WebApplicationException e) {
                        e.printStackTrace();
                        throw new RequestCancelledException(HttpStatus.INTERNAL_SERVER_ERROR,
                                e.getMessage());
                    } finally {
                        if (response.getBody() != null) {
                            key.attach(response);
                            this.executed = true;
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                    }
                } else {
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }

        private RequestBody resolveRequestBody(SocketChannel sock, ByteBuffer buf, int eoh,
                int read, String method, List<String> contentLengthHeader) throws IOException {
            if (this.body != null) {
                log.trace(String.format("Append Request Body %s", body));
                byte[] dst = new byte[read];
                buf.get(dst);
                body.update(dst);
                buf.clear();
                return body;
            }
            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
                log.trace("Create Request Body");
                int contentLength = CollectionUtils.isEmpty(contentLengthHeader) ? -1 : Integer
                        .valueOf(contentLengthHeader.get(0));
                if (contentLength < 0) {
                    throw new RequestCancelledException(HttpStatus.BAD_REQUEST, StringUtils.EMPTY);
                }
                int firstBodySize = buf.limit() - eoh;
                byte[] ba = new byte[firstBodySize];
                buf = (ByteBuffer) buf.position(eoh);
                buf.get(ba);
                buf.clear();
                log.trace(String.format("Content Length = %s", contentLength));
                return new RequestBody(contentLength, ba, props);
            } else {
                return new RequestBody();
            }
        }

        public boolean write(SelectionKey key) throws IOException {
            SocketChannel sock = (SocketChannel) key.channel();
            sock.configureBlocking(false);
            ResponseContextImpl ri = (ResponseContextImpl)key.attachment();
            ByteBuffer buffer = ri.getBody().getByteBuffer();
            if (buffer != null) {
                buffer.flip();
                try {
                    sock.write(buffer);
                } catch (Exception e) {
                } finally {
                    if (buffer.hasRemaining() == false && ri.completed()) {
                        key.channel().register(key.selector(), SelectionKey.OP_READ);
                        sock.close();
                    } else {
                        buffer.compact();
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                }
                return buffer.hasRemaining() == false && ri.completed();
            }
            return false;
        }
    }

    private int endOfHeader(final ByteBuffer buf, int length) {
        int splitbyte = 0;
        while (splitbyte + 3 < length) {
            if (buf.get(splitbyte) == '\r' && buf.get(splitbyte + 1) == '\n'
                    && buf.get(splitbyte + 2) == '\r' && buf.get(splitbyte + 3) == '\n') {
                return splitbyte + 4;
            }
            splitbyte++;
        }
        return 0;
    }

    private void sendStatus(final SocketChannel sock, HttpStatus status, String body) {
        CharBuffer buffer = CharBuffer.allocate(8192);
        buffer.append("HTTP/1.1").append(' ').append(String.valueOf(status.getStatusCode()))
                .append(' ').append(status.name()).append('\r').append('\n');
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'",
                Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        buffer.append("Date: ").append(dateFormat.format(new Date())).append('\r').append('\n');
        if (StringUtils.isNotEmpty(body)) {
            buffer.append("Content-Length: ").append(String.valueOf(body.length())).append('\r')
                    .append('\n');
        }
        buffer.append("Connection: close");
        buffer.append("\r\n");
        buffer.append("\r\n");
        buffer.flip();
        Charset iso = Charset.forName("ISO-8859-1");
        try {
            sock.write(iso.encode(buffer));
            if (StringUtils.isNotEmpty(body)) {
                sock.write(ByteBuffer.wrap(body.getBytes()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(sock);
        }
    }

    private static final class RequestCancelledException extends RuntimeException {

        private static final long serialVersionUID = -5041505179564601790L;
        private HttpStatus status;
        private String body;

        RequestCancelledException(HttpStatus status, String body) {
            this.status = status;
            this.body = body;
        }

        public HttpStatus getStatus() {
            return this.status;
        }

        public String getBody() {
            return this.body;
        }
    }

    private static final class RequestContextImpl extends AbstractRequestContext implements
            Disposable {

        private Map<String, List<String>> headerMap;
        private RequestBody body;

        RequestContextImpl(RequestPath requestPath, Locale defaultLocale,
                Map<String, List<String>> headerMap, RequestBody body) {
            super(requestPath, defaultLocale);
            this.body = body;
            this.headerMap = headerMap;
        }

        @Override
        public String getRequestMethod() {
            return getRequestPath().getRequestMethods().get(0);
        }

        @Override
        public Headers getRequestHeaders() {
            return new MapHeaders(headerMap);
        }

        @Override
        public InputStream getRequestBody() throws IOException {
            return body.open();
        }

        @Override
        public void dispose() {
            this.headerMap.clear();
            this.body.dispose();
        }
    }

    private static final class RequestBody implements Disposable {

        private static final ByteArrayInputStream EMPTY = new ByteArrayInputStream(new byte[0]);
        private OutputStream out;
        private InputStream in;
        private int remain;
        private boolean resolved;
        private File file;
        private RandomAccessFile ra;

        RequestBody() {
            this.in = new ByteArrayInputStream(new byte[0]);
            this.resolved = true;
        }

        RequestBody(int remain, byte[] first, ApplicationProperties props) throws IOException {
            this.remain = remain;
            if (remain > 1024 * 1024) {
                // Over 1M bytes.
                this.file = File.createTempFile("Analogweb", "Request", props.getTempDir());
                this.ra = new RandomAccessFile(file, "rw");
                this.out = new FileOutputStream(ra.getFD());
            } else {
                // In memory mode.
                this.out = new ByteArrayOutputStream();
            }
            update(first);
        }

        public boolean resolved() {
            return this.resolved;
        }

        @Override
        public void dispose() {
            IOUtils.closeQuietly(this.ra);
            IOUtils.closeQuietly(in);
            this.in = null;
            IOUtils.closeQuietly(out);
            this.out = null;
            if (this.file != null) {
                this.file.deleteOnExit();
            }
        }

        public void update(byte[] buffer) throws IOException {
            if (this.out == null) {
                log.trace("This request buffer not writable.");
                return;
            }
            this.remain -= buffer.length;
            this.out.write(buffer);
            log.trace(String.format("%s bytes written.", buffer.length));
            log.trace(String.format("Content remaining %s bytes.", remain));
            if (this.remain < 1) {
                this.resolved = true;
            }
        }

        public InputStream open() throws IOException {
            if (this.ra != null) {
                IOUtils.closeQuietly(this.ra);
                this.ra = new RandomAccessFile(file, "r");
                return new FileInputStream(this.ra.getFD());
            } else {
                if (this.out instanceof ByteArrayOutputStream) {
                    return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
                } else {
                    return EMPTY;
                }
            }
        }
    }

    static final class ResponseContextImpl extends AbstractResponseContext {

        private RequestContext request;
        private SocketChannel sock;
        private ResponseBody body;

        ResponseContextImpl(final RequestContext request, final SocketChannel sock) {
            this.request = request;
            this.sock = sock;
        }

        public ResponseBody getBody() {
            return this.body;
        }

        @Override
        public void commmit(RequestContext context, Response response) {
            // write headers
            CharBuffer buffer = CharBuffer.allocate(8192);
            buffer.append("HTTP/1.1").append(' ').append(String.valueOf(getStatus())).append(' ')
                    .append(HttpStatus.valueOf(getStatus()).name()).append("\r\n");
            Headers h = getResponseHeaders();
            if (h.contains("Date") == false) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'",
                        Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                buffer.append("Date: ").append(dateFormat.format(new Date())).append("\r\n");
            }
            long length = response.getContentLength();
            OutputStream out = null;
            if (request.getRequestMethod().equals("HEAD") == false) {
                if (h.contains("Content-Length") == false && length != -1) {
                    buffer.append("Content-Length: ").append(String.valueOf(length)).append("\r\n");
                    out = toFixedLengthOutputStream(length, sock);
                }
                if (length == -1) {
                    buffer.append("Transfer-Encoding: chunked").append("\r\n");
                    out = toChunkedOutputStream(sock);
                }
            }
            for (String headerName : h.getNames()) {
                Iterator<String> values = h.getValues(headerName).iterator();
                buffer.append(headerName).append(": ").append(values.next());
                while (values.hasNext()) {
                    buffer.append(',').append(values.next());
                }
                buffer.append("\r\n");
            }
            buffer.append("\r\n");
            buffer.flip();
            Charset iso = Charset.forName("ISO-8859-1");
            try {
                sock.write(iso.encode(buffer));
                ResponseEntity entity = response.getEntity();
                if (entity != null) {
                    entity.writeInto(out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // write contents.s
        }

        private FixedLengthOutputStream toFixedLengthOutputStream(long length, SocketChannel sock) {
            return new FixedLengthOutputStream(toOutputStream(), length);
        }

        private ChunkedOutputStream toChunkedOutputStream(SocketChannel sock) {
            return new ChunkedOutputStream(8192, toOutputStream());
        }

        private OutputStream toOutputStream() {
            this.body = new ResponseBody(ByteBuffer.allocate(64 * 1024));
            return new OutputStream() {

                @Override
                public void write(int b) throws IOException {
                    body.put(new byte[] { (byte) b });
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    body.put(b);
                }
            };
        }
    }

    static final class ResponseBody {

        private ByteBuffer backend;

        private ResponseBody(ByteBuffer bb) {
            this.backend = bb;
        }

        public static ResponseBody allocate(int capacity) {
            return new ResponseBody(ByteBuffer.allocate(capacity));
        }

        public void put(byte[] src) {
            ensureCapacity(src.length);
            backend.put(src);
        }

        private void ensureCapacity(int size) {
            int remaining = backend.remaining();
            if (size > remaining) {
                log.debug("allocating new DynamicByteBuffer, old capacity {}: ", backend.capacity());
                int missing = size - remaining;
                int newSize = (int) ((backend.capacity() + missing) * 1.5);
                reallocate(newSize);
            }
        }

        // Preserves position.
        private void reallocate(int newCapacity) {
            int oldPosition = backend.position();
            byte[] newBuffer = new byte[newCapacity];
            System.arraycopy(backend.array(), 0, newBuffer, 0, backend.position());
            backend = ByteBuffer.wrap(newBuffer);
            backend.position(oldPosition);
            log.debug("allocated new DynamicByteBufer, new capacity: {}", backend.capacity());
        }

        public ByteBuffer getByteBuffer() {
            return backend;
        }

        public void flip() {
            backend.flip();
        }

        public int limit() {
            return backend.limit();
        }

        public int position() {
            return backend.position();
        }

        public byte[] array() {
            return backend.array();
        }

        public int capacity() {
            return backend.capacity();
        }

        public boolean hasRemaining() {
            return backend.hasRemaining();
        }

        public ResponseBody compact() {
            backend.compact();
            return this;
        }

        public ResponseBody clear() {
            backend.clear();
            return this;
        }
    }

    static final class FixedLengthOutputStream extends OutputStream {

        private final OutputStream out;
        private final long contentLength;
        private long total = 0;
        private boolean closed = false;

        FixedLengthOutputStream(final OutputStream out, final long contentLength) {
            super();
            this.out = out;
            this.contentLength = contentLength;
        }

        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                this.out.flush();
            }
        }

        @Override
        public void flush() throws IOException {
            this.out.flush();
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (this.closed) {
                throw new IOException("Attempted write to closed stream.");
            }
            if (this.total < this.contentLength) {
                final long max = this.contentLength - this.total;
                int chunk = len;
                if (chunk > max) {
                    chunk = (int) max;
                }
                this.out.write(b, off, chunk);
                this.total += chunk;
            }
        }

        @Override
        public void write(final byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(final int b) throws IOException {
            if (this.closed) {
                throw new IOException("Attempted write to closed stream.");
            }
            if (this.total < this.contentLength) {
                this.out.write(b);
                this.total++;
            }
        }
    }

    static final class ChunkedOutputStream extends OutputStream {

        private final OutputStream out;
        private final byte[] cache;
        private int cachePosition = 0;
        private boolean wroteLastChunk = false;
        private boolean closed = false;

        ChunkedOutputStream(final int bufferSize, final OutputStream out) {
            super();
            this.cache = new byte[bufferSize];
            this.out = out;
        }

        protected void flushCache() throws IOException {
            if (this.cachePosition > 0) {
                this.out.write(Integer.toHexString(this.cachePosition).getBytes());
                this.out.write(CRLF);
                this.out.write(this.cache, 0, this.cachePosition);
                this.out.write("".getBytes());
                this.cachePosition = 0;
            }
        }

        protected void flushCacheWithAppend(final byte bufferToAppend[], final int off,
                final int len) throws IOException {
            this.out.write(Integer.toHexString(this.cachePosition + len).getBytes());
            this.out.write(CRLF);
            this.out.write(this.cache, 0, this.cachePosition);
            this.out.write(bufferToAppend, off, len);
            this.out.write("".getBytes());
            this.cachePosition = 0;
        }

        protected void writeClosingChunk() throws IOException {
            this.out.write("0".getBytes());
            this.out.write("".getBytes());
        }

        public void finish() throws IOException {
            if (!this.wroteLastChunk) {
                flushCache();
                writeClosingChunk();
                this.wroteLastChunk = true;
            }
        }

        @Override
        public void write(final int b) throws IOException {
            if (this.closed) {
                throw new IOException("Attempted write to closed stream.");
            }
            this.cache[this.cachePosition] = (byte) b;
            this.cachePosition++;
            if (this.cachePosition == this.cache.length) {
                flushCache();
            }
        }

        @Override
        public void write(final byte b[]) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(final byte src[], final int off, final int len) throws IOException {
            if (this.closed) {
                throw new IOException("Attempted write to closed stream.");
            }
            if (len >= this.cache.length - this.cachePosition) {
                flushCacheWithAppend(src, off, len);
            } else {
                System.arraycopy(src, off, cache, this.cachePosition, len);
                this.cachePosition += len;
            }
        }

        @Override
        public void flush() throws IOException {
            flushCache();
            this.out.flush();
        }

        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                finish();
                this.out.flush();
            }
        }
    }

    @Override
    public void shutdown(int mode) {
        app.dispose();
    }

    protected List<ClassCollector> getClassCollectors() {
        List<ClassCollector> list = new ArrayList<ClassCollector>();
        list.add(new JarClassCollector());
        list.add(new FileClassCollector());
        return Collections.unmodifiableList(list);
    }
}
