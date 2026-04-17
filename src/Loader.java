import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

// 画像、フォント、サウンドを読み込むためのクラス
public class Loader {
    public static BufferedImage ImageLoader(String path) {
        try {
            //System.out.println("Loading image from path: " + path);
            InputStream is = Loader.class.getResourceAsStream(path);
            if(is == null) {
                throw new IllegalArgumentException("Image file not found: " + path);
            }
            return ImageIO.read(is);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Font loadFont(String path, int size) {
        try {
            //System.out.println("Loading font from path: " + path);
            InputStream is = Loader.class.getResourceAsStream(path);
            if(is == null) {
               throw new IllegalArgumentException("Font file not found: " + path);
            }   
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Serif", Font.PLAIN, size);
        }
    }

    public static Clip loadSound(String path) {
        try {
            //System.out.println("Loading sound from path: " + path);
            InputStream is = Loader.class.getResourceAsStream(path);
            if(is == null) {
                throw new IllegalArgumentException("Sound file not found: " + path);
            }
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(is));
            return clip;
        } catch(LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        return null;
    }
}
