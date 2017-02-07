package org.analogweb.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.*;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.ClassUtils;
import org.analogweb.util.IOUtils;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * @author snowgooseyk
 */
public class MultipartParameterResolver extends ParameterValueResolver implements
        SpecificMediaTypeRequestValueResolver {

    private static final Log log = Logs.getLog(MultipartParameterResolver.class);
    private final String defaultEncoding = "UTF-8";
    private static final String MULTIPARTPARAMETERS_ON_CURRENT_REQUEST = MultipartParameterResolver.class
            .getCanonicalName() + "_MULTIPARTPARAMETERS_ON_CURRENT_REQUEST";

    @Override
    public boolean supports(MediaType mediaType) {
        return MediaTypes.MULTIPART_FORM_DATA_TYPE.isCompatible(mediaType);
    }

    @Override
    public Object resolveValue(RequestContext request, InvocationMetadata metadata, String name,
            Class<?> requiredType, Annotation[] annotations) {
        try {
            MultipartParameters<Multipart> multiparts = request
                    .getAttribute(MULTIPARTPARAMETERS_ON_CURRENT_REQUEST);
            if (multiparts == null) {
                multiparts = extractMultipart(request.getContentType().toString(),
                        request.getRequestBody());
                request.setAttribute(MULTIPARTPARAMETERS_ON_CURRENT_REQUEST, multiparts);
            }
            return resolveParameterizedValue(request, metadata, name, requiredType, annotations,
                    multiparts);
        } catch (IOException e) {
            throw new ApplicationRuntimeException(e) {

                private static final long serialVersionUID = 2628554296029294446L;
            };
        }
    }

    private MultipartParameters<Multipart> extractMultipart(String contentType, ReadableBuffer in)
            throws IOException {
        byte[] boundary = extractBoundary(contentType);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Multipart Boundary : %s", new String(boundary)));
        }
        List<Multipart> params = new LinkedList<Multipart>();
        ReadableByteChannel source = in.asChannel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        source.read(buffer);
        buffer.flip();
        int beginOfBoundary = nextPosition(0, boundary, buffer);
        while (true) {
            int endOfBoundary = beginOfBoundary + boundary.length;
            int endOfHeader = nextPosition(endOfBoundary, new byte[] { 13, 10, 13, 10 }, buffer);
            if (endOfHeader == -1) {
                break;
            }
            byte[] dst = new byte[endOfHeader - endOfBoundary - 2];
            buffer.position(endOfBoundary + 2);// CRLF
            buffer.get(dst);
            Map<String, String> header = parseHeader(new String(dst));
            if (log.isTraceEnabled()) {
                log.trace(String.format("Header of part : %s", header));
            }
            buffer.position(buffer.position() + 4);// CRLF + CRLF
            int nextBeginOfBoundary;
            File file = File.createTempFile(MultipartParameterResolver.class.getCanonicalName(),
                    "parameter");
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileOutputStream fo = new FileOutputStream(raf.getFD());
            while ((nextBeginOfBoundary = nextPosition(endOfHeader, boundary, buffer)) < 0) {
                dst = new byte[buffer.remaining()];
                buffer.get(dst);
                fo.write(dst);
                buffer.clear();
                source.read(buffer);
                buffer.flip();
                endOfHeader = 0;
            }
            int remaining = (nextBeginOfBoundary - 8) - endOfHeader;
            dst = new byte[remaining];
            buffer.get(dst);
            fo.write(dst);
            fo.flush();
            fo.close();
            raf.close();
            params.add(new MultipartImpl(header, file));
            if (log.isTraceEnabled()) {
                log.trace(String.format("Body of part : %s", new String(dst)));
            }
            beginOfBoundary = nextBeginOfBoundary;
        }
        return new MultipartsImpl(params, Charset.forName(defaultEncoding));
    }

    private int nextPosition(int start, byte[] boundary, ByteBuffer buffer) {
        int matches = 0;
        int matchedPosition = -1;
        for (int i = start; i < buffer.limit(); i++) {
            if (buffer.get(i) == boundary[matches]) {
                if (matches == 0)
                    matchedPosition = i;
                matches++;
                if (matches == boundary.length) {
                    return matchedPosition;
                }
            } else {
                i -= matches;
                matches = 0;
                matchedPosition = -1;
            }
        }
        return -1;
    }

    private byte[] extractBoundary(String contentType) {
        String prefix = "boundary=";
        String boundary = contentType.substring(contentType.indexOf(prefix) + prefix.length(),
                contentType.length());
        if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
            return boundary.substring(1, boundary.length() - 1).getBytes();
        } else {
            return contentType.substring(contentType.indexOf(prefix) + prefix.length(),
                    contentType.length()).getBytes();
        }
    }

    private Map<String, String> parseHeader(String buffer) throws IOException {
        BufferedReader r = new BufferedReader(new StringReader(buffer));
        String aLine = null;
        Map<String, String> m = Maps.newEmptyHashMap();
        while ((aLine = r.readLine()) != null && aLine.trim().length() != 0) {
            int i = aLine.indexOf(':');
            if (i > 0 && i < aLine.length()) {
                String key = aLine.substring(0, i).trim();
                String value = aLine.substring(i + 1).trim();
                m.put(key, value);
            }
        }
        return m;
    }

    protected Object resolveParameterizedValue(RequestContext request, InvocationMetadata metadata,
            String name, Class<?> requiredType, Annotation[] annotations,
            MultipartParameters<Multipart> parameters) {
        if (isEqualsType(Iterable.class, requiredType)) {
            return parameters;
        }
        final Multipart[] value = parameters.getMultiparts(name);
        if (ArrayUtils.isNotEmpty(value)) {
            if (isEqualsType(ClassUtils.forNameQuietly("[L" + File.class.getName() + ";"),
                    requiredType)) {
                final List<File> files = new ArrayList<File>();
                for (final Multipart mp : value) {
                    File f;
                    if (mp instanceof MultipartImpl
                            && (f = ((MultipartImpl) mp).getAsFile()) != null) {
                        files.add(f);
                    }
                }
                return files.toArray(new File[files.size()]);
            } else if (isEqualsType(
                    ClassUtils.forNameQuietly("[L" + Multipart.class.getName() + ";"), requiredType)) {
                return value;
            } else if (isEqualsType(ClassUtils.forNameQuietly("[L" + String.class.getName() + ";"),
                    requiredType)) {
                List<String> values = new ArrayList<String>();
                for (final Multipart mp : value) {
                    values.add(resolveParameterizedValueAsString(mp));
                }
                return values.toArray(new String[values.size()]);
            }
            final Multipart mp = value[0];
            if (isEqualsType(InputStream.class, requiredType)) {
                return mp.getInputStream();
            } else if (isEqualsType(File.class, requiredType)) {
                if (mp instanceof MultipartImpl) {
                    return ((MultipartImpl) mp).getAsFile();
                }
            } else if (isEqualsType(byte[].class, requiredType)) {
                return mp.getBytes();
            } else if (isEqualsType(String.class, requiredType)) {
                return resolveParameterizedValueAsString(mp);
            } else if (isEqualsType(Multipart.class, requiredType)) {
                return mp;
            } else {
                throw new UnresolvableValueException(this, requiredType, name);
            }
        }
        return super.resolveValue(request, metadata, name, requiredType, annotations);
    }

    private String resolveParameterizedValueAsString(Multipart mp) {
        String charset = MediaTypes.valueOf(mp.getContentType()).getParameters().get("charset");
        if (StringUtils.isNotEmpty(charset)) {
            return new String(mp.getBytes(), Charset.forName(charset));
        } else {
            return new String(mp.getBytes(), Charset.forName("ISO-8859-1"));
        }
    }

    private boolean isEqualsType(Class<?> clazz, Class<?> other) {
        if (clazz == null || other == null) {
            return false;
        }
        return (clazz == other) || clazz.getCanonicalName().equals(other.getCanonicalName());
    }

    static final class MultipartImpl implements Multipart {

        private Map<String, String> header;
        private Map<String, String> contentDisposition;
        private File tempFile;

        MultipartImpl(Map<String, String> header, File temp) {
            this.header = header;
            this.tempFile = temp;
        }

        @Override
        public String getName() {
            return getContentDisposition().get("name");
        }

        public void dispose() {
            this.tempFile.deleteOnExit();
        }

        @Override
        public String getResourceName() {
            return getContentDisposition().get("filename");
        }

        private Map<String, String> getContentDisposition() {
            if (contentDisposition == null) {
                contentDisposition = Maps.newEmptyHashMap();
                String value = this.header.get("Content-Disposition");
                for (String param : StringUtils.split(value, ';')) {
                    List<String> sp = StringUtils.split(param.trim(), '=');
                    if (sp.size() > 1) {
                        String p = sp.get(1);
                        if (p.endsWith("\"") && p.startsWith("\"")) {
                            contentDisposition.put(sp.get(0), p.substring(1, p.length() - 1));
                        } else {
                            contentDisposition.put(sp.get(0), p);
                        }
                    } else {
                        contentDisposition.put(sp.get(0), "");
                    }
                }
            }
            return contentDisposition;
        }

        @Override
        public InputStream getInputStream() {
            try {
                return new FileInputStream(tempFile);
            } catch (IOException e) {
                throw new ApplicationRuntimeException(e) {

                    private static final long serialVersionUID = 1L;
                };
            }
        }

        public File getAsFile() {
            return this.tempFile;
        }

        @Override
        public byte[] getBytes() {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            IOUtils.copyQuietly(getInputStream(), bytes);
            return bytes.toByteArray();
        }

        @Override
        public String getContentType() {
            return header.get("Content-Type");
        }
    }

    static final class MultipartsImpl implements MultipartParameters<Multipart> {

        private List<Multipart> multiparts;
        private Charset encoding;
        private Map<String, Multipart[]> mapped;

        MultipartsImpl(List<Multipart> multiparts, Charset encoding) {
            this.multiparts = multiparts;
            this.encoding = encoding;
        }

        @Override
        public Iterator<Multipart> iterator() {
            return this.multiparts.iterator();
        }

        @Override
        public List<String> getValues(String key) {
            String[] values = asMap().get(key);
            if (ArrayUtils.isEmpty(values)) {
                return Collections.emptyList();
            } else {
                return Arrays.asList(values);
            }
        }

        @Override
        public Map<String, String[]> asMap() {
            Map<String, Multipart[]> mps = extract();
            Map<String, String[]> sms = Maps.newEmptyHashMap();
            for (Entry<String, Multipart[]> e : mps.entrySet()) {
                String[] values = ArrayUtils.newArray();
                for (Multipart m : e.getValue()) {
                    ArrayUtils.add(String.class, new String(m.getBytes(), encoding), values);
                }
                sms.put(e.getKey(), values);
            }
            return null;
        }

        @Override
        public Multipart[] getMultiparts(String name) {
            return extract().get(name);
        }

        private Map<String, Multipart[]> extract() {
            if (mapped == null) {
                mapped = Maps.newEmptyHashMap();
                for (Multipart mp : this.multiparts) {
                    if (mapped.containsKey(mp.getName())) {
                        mapped.put(mp.getName(),
                                ArrayUtils.add(Multipart.class, mp, mapped.get(mp.getName())));
                    } else {
                        mapped.put(mp.getName(), ArrayUtils.newArray(mp));
                    }
                }
            }
            return mapped;
        }
    }
}
