package site.scalarstudios.scalarbudget.loader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageCacheUtil {
    private static final String CACHE_DIR = System.getProperty("java.io.tmpdir") + File.separator + "scalarbudget_image_cache";

    /**
     * Downloads and caches an image from the given URL if not already cached.
     * Returns the local file path to the cached image, or null if download fails.
     */
    public static String getCachedImagePath(String urlString) {
        try {
            // Use a hash of the URL as the filename to avoid collisions
            String fileName = Integer.toHexString(urlString.hashCode()) + getFileExtension(urlString);
            Path cacheDir = Paths.get(CACHE_DIR);
            if (!Files.exists(cacheDir)) {
                Files.createDirectories(cacheDir);
            }
            Path cachedFile = cacheDir.resolve(fileName);
            if (Files.exists(cachedFile)) {
                return cachedFile.toAbsolutePath().toString();
            }
            // Download the image
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // Some servers require a user agent
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            if (conn.getResponseCode() != 200) {
                System.out.println("ImageCacheUtil: Failed to download image, HTTP " + conn.getResponseCode());
                return null;
            }
            try (InputStream in = conn.getInputStream();
                 OutputStream out = Files.newOutputStream(cachedFile)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
            return cachedFile.toAbsolutePath().toString();
        } catch (Exception e) {
            System.out.println("ImageCacheUtil: Exception caching image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String getFileExtension(String url) {
        int lastDot = url.lastIndexOf('.');
        if (lastDot != -1 && lastDot > url.lastIndexOf('/')) {
            return url.substring(lastDot);
        }
        return ".img";
    }
}

