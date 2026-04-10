package com.reviewy.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the 'mem:' protocol to serve images from memory.
 */
public class MemoryURLStreamHandler extends URLStreamHandler {
    private static final Map<String, byte[]> images = new HashMap<>();

    public static void register() {
        try {
            URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
                @Override
                public URLStreamHandler createURLStreamHandler(String protocol) {
                    if ("mem".equals(protocol)) {
                        return new MemoryURLStreamHandler();
                    }
                    return null;
                }
            });
        } catch (Error e) {
            // Protocol factory already set, that's fine for simple cases
        }
    }

    public static void putImage(String key, BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            images.put(key, baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        images.clear();
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new URLConnection(u) {
            @Override
            public void connect() throws IOException {}

            @Override
            public InputStream getInputStream() throws IOException {
                byte[] data = images.get(u.getPath());
                if (data == null) throw new IOException("Image not found: " + u.getPath());
                return new ByteArrayInputStream(data);
            }

            @Override
            public String getContentType() {
                return "image/png";
            }
        };
    }
}
