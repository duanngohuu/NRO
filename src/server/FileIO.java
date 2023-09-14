package server;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FileIO {

    public static byte[] readFile(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            byte[] ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            fis = null;
            return ab;
        } catch (IOException e) {
            // Util.logException(FileIO.class, e);
        }
        return null;
    }

    public static void writeFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
            fos = null;
            f = null;
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
    
    public static BufferedImage readImg(String url) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(url));
        } catch (Exception e) {
            Util.logException(FileIO.class, e);
        }
        return bufferedImage;
    }
}
