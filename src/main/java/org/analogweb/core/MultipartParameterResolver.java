package org.analogweb.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
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
import java.util.StringTokenizer;

import org.analogweb.InvocationMetadata;
import org.analogweb.MediaType;
import org.analogweb.Multipart;
import org.analogweb.MultipartParameters;
import org.analogweb.RequestContext;
import org.analogweb.util.ArrayUtils;
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

    @Override
    public boolean supports(MediaType mediaType) {
        return MediaTypes.MULTIPART_FORM_DATA_TYPE.isCompatible(mediaType);
    }

    @Override
    public Object resolveValue(RequestContext request, InvocationMetadata metadata, String name,
            Class<?> requiredType, Annotation[] annotations) {
        try {
            MultipartParameters<Multipart> multiparts = extractMultipart(request.getContentType().toString(), request.getRequestBody());
            return multiparts.getMultiparts(name);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    private MultipartParameters<Multipart> extractMultipart(String contentType, InputStream in)
            throws IOException {
        byte[] boundary = extractBoundary(contentType);
        System.out.println("BD: " + new String(boundary));
        List<Multipart> params = new LinkedList<Multipart>();
        ReadableByteChannel source = Channels.newChannel(in);
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        source.read(buffer);
        buffer.flip();
        int beginOfBoundary = nextPosition(0, boundary, buffer);
        System.out.println("B: " + beginOfBoundary);
        while (true) {
            int endOfBoundary = beginOfBoundary + boundary.length;
            System.out.println("EB: " + endOfBoundary);
            int endOfHeader = nextPosition(endOfBoundary, new byte[] { 13, 10, 13, 10 }, buffer);
            System.out.println("EH: " + endOfHeader);
            if (endOfHeader == -1) {
                break;
            }
            byte[] dst = new byte[endOfHeader - endOfBoundary - 2];
            buffer.position(endOfBoundary + 2);// CRLF
            buffer.get(dst);
            Map<String, String> header = parseHeader(new String(dst));
            System.out.println("HEADER: " + header);
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
            System.out.println("NB: " + nextBeginOfBoundary);
            int remaining = (nextBeginOfBoundary - 8) - endOfHeader;
            System.out.println("Remaining: " + remaining);
            dst = new byte[remaining];
            buffer.get(dst);
            fo.write(dst);
            fo.flush();
            fo.close();
            params.add(new MultipartImpl(header,file,raf));
            System.out.println("BODY: " + new String(dst));
            beginOfBoundary = nextBeginOfBoundary;
            System.out.println("B: " + beginOfBoundary);
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

    private static Map<String, String> parseHeader(String buffer) {
        BufferedReader r = new BufferedReader(new StringReader(buffer));
        String aLine = null;
        Map<String, String> m = Maps.newEmptyHashMap();
        try {
            while ((aLine = r.readLine()) != null && aLine.trim().length() != 0) {
                int i = aLine.indexOf(':');
                if (i > 0 && i < aLine.length()) {
                    String key = aLine.substring(0, i).trim();
                    String value = aLine.substring(i + 1).trim();
                    m.put(key, value);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m;
    }

    static final class MultipartImpl implements Multipart {

        private Map<String,String> header;
        private Map<String,String> contentDisposition;
        private File tempFile;
        private RandomAccessFile memory;
        
        MultipartImpl(Map<String,String> header,File temp,RandomAccessFile random){
            this.header = header;
            this.tempFile = temp;
            this.memory = random;
        }

        @Override
        public String getName() {
            return getContentDisposition().get("name");
        }
        
        public void dispose(){
            IOUtils.closeQuietly(this.memory);
            this.tempFile.deleteOnExit();
        }

        @Override
        public String getResourceName() {
            return getContentDisposition().get("filename");
        }
        
        private Map<String,String> getContentDisposition(){
            if(contentDisposition == null){
                contentDisposition = Maps.newEmptyHashMap();
                String value = this.header.get("Content-Disposition");
                for (String param : StringUtils.split(value, ';')) {
                    List<String> sp = StringUtils.split(param.trim(), '=');
                    if (sp.size() > 1) {
                        contentDisposition.put(sp.get(0), sp.get(1));
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
                return new FileInputStream(memory.getFD());
            } catch (IOException e) {
                throw new ApplicationRuntimeException(e) {
                    private static final long serialVersionUID = 1L;
                };
            }
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
        
        MultipartsImpl(List<Multipart> multiparts,Charset encoding){
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
                        ArrayUtils.add(Multipart.class, mp, mapped.get(mp.getName()));
                    } else {
                        mapped.put(mp.getName(), ArrayUtils.newArray(mp));
                    }
                }
            }
            return mapped;
        }
    }
}
