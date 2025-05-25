package retrologin;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class RetroArcadeFontLoader {

    public static Font loadPixelFont(float size) {
        try {
            File fontFile = new File("pixel_font.ttf");

            if (!fontFile.exists()) {
                System.err.println("Pixel font file not found: " + fontFile.getAbsolutePath());
                return new Font(Font.MONOSPACED, Font.PLAIN, (int) size);
            }

            Font pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return pixelFont.deriveFont(Font.PLAIN, size);

        } catch (FontFormatException e) {
            System.err.println("Font format error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error loading font: " + e.getMessage());
        }
        return new Font(Font.MONOSPACED, Font.PLAIN, (int) size);
    }
}