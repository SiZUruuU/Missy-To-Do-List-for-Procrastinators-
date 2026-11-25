package Components.Utilities;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontManager {

    private final Map<String, Font> fonts = new HashMap<>();

    public Font loadFont(String path) {

        Font baseFont = fonts.get(path);

        if (baseFont == null) {
            try (InputStream stream =
                    getClass().getResourceAsStream(path)) {

                if (stream == null) {
                    throw new IllegalArgumentException(
                        "Font not found: " + path
                    );
                }

                baseFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    stream
                );

                fonts.put(path, baseFont);

            } catch (Exception e) {
                throw new RuntimeException(
                    "Failed to load font: " + path, e
                );
            }
        }

        return baseFont;
    }
}