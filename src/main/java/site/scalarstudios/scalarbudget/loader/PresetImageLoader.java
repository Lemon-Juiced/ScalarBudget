package site.scalarstudios.scalarbudget.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads preset images from images.json and provides lookup by name.
 */
public class PresetImageLoader {
    private static final Map<String, String> PRESET_IMAGES;

    static {
        Map<String, String> map = new HashMap<>();
        try (InputStream is = PresetImageLoader.class.getResourceAsStream("/site/scalarstudios/scalarbudget/presets/images.json")) {
            if (is != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> loaded = mapper.readValue(is, Map.class);
                map.putAll(loaded);
            }
        } catch (IOException e) {
            // Ignore, leave map empty
        }
        PRESET_IMAGES = Collections.unmodifiableMap(map);
    }

    public static String getImageUrlForName(String name) {
        return PRESET_IMAGES.get(name);
    }

    public static boolean hasPresetForName(String name) {
        return PRESET_IMAGES.containsKey(name);
    }

    public static Map<String, String> getAllPresets() {
        return PRESET_IMAGES;
    }
}

