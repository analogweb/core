package org.analogweb.core;

import static org.analogweb.core.DefaultApplicationProperties.defaultProperties;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import org.analogweb.ResponseContext;
import org.analogweb.Server;
import org.analogweb.core.response.HttpStatus;
import org.analogweb.util.Assertion;
import org.analogweb.util.ClassCollector;
import org.analogweb.util.CollectionUtils;
import org.analogweb.util.FileClassCollector;
import org.analogweb.util.IOUtils;
import org.analogweb.util.JarClassCollector;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;

public class DefaultServer implements Server {

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
    
    private Map<SelectableChannel, Handler> requests = Maps.newConcurrentHashMap();

    private void runInternal(Selector sl) throws IOException {
        while (true) {
            while(sl.select() > 0){
                Iterator<SelectionKey> keys = sl.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssock = (ServerSocketChannel) key.channel();
                        SocketChannel channel = ssock.accept();
                        channel.configureBlocking(false);
                        channel.register(sl, SelectionKey.OP_READ);
                    } else {
                        if (key.isReadable()) {
                            SelectableChannel channel = key.channel();
                            Handler handler;
                            if(requests.containsKey(channel)){
                                handler = requests.get(channel);
                            } else {
                                handler = new Handler();
                                requests.put(channel, handler);
                            }
                            handler.read(key);
                        }
                        if (key.isWritable()) {
                            SelectableChannel channel = key.channel();
                            try{
                                requests.get(channel).write(key);
                            } finally {
                                requests.remove(channel);
                            }
                        }
                    }
                }
            }
            Iterator<SelectionKey> keys = sl.keys().iterator();
            while(keys.hasNext()){
                keys.next().channel().close();
            }
        }
    }
    
    private class Handler {
        
        private RequestPath path;
        private Map<String, List<String>> headerMap;
        private RequestBody body;
        
        public void read(SelectionKey key) throws IOException {
            SocketChannel sock = (SocketChannel) key.channel();
            sock.configureBlocking(false);
            ByteBuffer buf = ByteBuffer.allocate(8192);
            int read = sock.read(buf);
            if (read == 1) {
                // spinning.
                return;
            }
            if (read > 0) {
                buf.flip();
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
                        sendStatus(sock, HttpStatus.BAD_REQUEST, StringUtils.EMPTY);
                        return;
                    }
                    StringTokenizer st = new StringTokenizer(aLine);
                    if (st.countTokens() < 2) {
                        sendStatus(sock, HttpStatus.BAD_REQUEST, StringUtils.EMPTY);
                        return;
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
                }
                body = resolveRequestBody(sock, buf, eoh, read, path.getRequestMethod(),
                        headerMap.get("Content-Length"));
                if (body.resolved()) {
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                } else {
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }
        
        private RequestBody resolveRequestBody(SocketChannel sock, ByteBuffer buf, int eoh, int read,
                String method, List<String> contentLengthHeader) throws IOException {
            if(this.body != null){
                body.update(buf.array());
                buf.clear();
                return body;
            }
            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
                int contentLength = CollectionUtils.isEmpty(contentLengthHeader) ? -1 : Integer
                        .valueOf(contentLengthHeader.get(0));
                if (contentLength < 0) {
                    sendStatus(sock, HttpStatus.BAD_REQUEST, StringUtils.EMPTY);
                    return null;
                }
                int firstBodySize = buf.limit() - eoh;
                byte[] ba = new byte[firstBodySize];
                buf = (ByteBuffer) buf.position(eoh);
                buf.get(ba);
                contentLength -= firstBodySize;
                buf.clear();        
                return new RequestBody(contentLength, ba);
            } else {
                return new RequestBody();
            }
        }

        public void write(SelectionKey key) throws IOException {
            SocketChannel sock = (SocketChannel) key.channel();
            sock.configureBlocking(false);
            RequestContextImpl request = null;
            try {
                request = new RequestContextImpl(path, Locale.getDefault(), headerMap, body);
                if (request != null) {
                    ResponseContext response = createResponseContext(request, sock);
                    int proceed = app.processRequest(request.getRequestPath(),
                            request, response);
                    if (proceed == Application.NOT_FOUND) {
                        sendStatus(sock, HttpStatus.NOT_FOUND, StringUtils.EMPTY);
                    } else {
                        response.commmit(request);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendStatus(sock, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            } finally {
                if (request != null) {
                    request.dispose();
                }
                sock.close();
            }
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

        private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private InputStream in;
        private int remain;
        private boolean resolved;

        RequestBody() {
            this.in = new ByteArrayInputStream(new byte[0]);
            this.resolved = true;
        }

        RequestBody(int remain,byte[] first) throws IOException {
            this.remain = remain;
            update(first);
        }

        public boolean resolved(){
            return this.resolved;
        }

        @Override
        public void dispose() {
            IOUtils.closeQuietly(in);
            this.bytes = null;
        }
        
        public void update(byte[] buffer) throws IOException {
            this.remain -= buffer.length;
            this.bytes.write(buffer);
            if(this.remain < 1){
                this.resolved = true;
            }
        }

        public InputStream open() throws IOException {
            return new ByteArrayInputStream(this.bytes.toByteArray());
        }
    }

    private ResponseContext createResponseContext(final RequestContext request,
            final SocketChannel sock) {
        return new AbstractResponseContext() {

            @Override
            public void commmit(RequestContext context) {
                // write headers
                CharBuffer buffer = CharBuffer.allocate(8192);
                buffer.append("HTTP/1.1").append(' ').append(String.valueOf(getStatus()))
                        .append(' ').append(HttpStatus.valueOf(getStatus()).name()).append("\r\n");
                Headers h = getResponseHeaders();
                if (h.contains("Date") == false) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    buffer.append("Date: ").append(dateFormat.format(new Date())).append("\r\n");
                }
                long length = getContentLength();
                OutputStream out = null;
                if (request.getRequestMethod().equals("HEAD") == false) {
                    if (h.contains("Content-Length") == false && length != -1) {
                        buffer.append("Content-Length: ").append(String.valueOf(length))
                                .append("\r\n");
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
                    ResponseEntity entity = getResponseWriter().getEntity();
                    if (entity != null) {
                        entity.writeInto(out);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // write contents.s
            }

            private FixedLengthOutputStream toFixedLengthOutputStream(long length,
                    SocketChannel sock) {
                return new FixedLengthOutputStream(toOutputStream(sock), length);
            }

            private ChunkedOutputStream toChunkedOutputStream(SocketChannel sock) {
                return new ChunkedOutputStream(8192, toOutputStream(sock));
            }

            private OutputStream toOutputStream(final SocketChannel sock) {
                return new OutputStream() {

                    private ByteBuffer bu = ByteBuffer.allocate(8192);

                    @Override
                    public void write(int b) throws IOException {
                        bu.clear();
                        bu.put((byte) b);
                        bu.flip();
                        sock.write(bu);
                    }

                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        if (bu.limit() < len) {
                            bu = ByteBuffer.wrap(b, off, len);
                        }
                        bu.clear();
                        bu.put(b, off, len);
                        bu.flip();
                        sock.write(bu);
                    }
                };
            }
        };
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
