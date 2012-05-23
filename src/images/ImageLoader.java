package images;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;

public class ImageLoader {

    /**
     * Paths for images.
     * NOTE THAT PATHS ARE CASE SENSITIVE!
     */
    private static String                 imagePaths[] = { "DestructibleWall.jpg", "IndestructibleWall.jpg", "EmptyField.jpg", "Exit.jpg", "Bomb.gif", "Player.gif", "Explosion.gif" };
    
    private static HashMap<String, Image> images       = new HashMap<String, Image>();

    static {
        // Loads images
        // This will be executed before first access to this class
        ClassLoader classLoader = ImageLoader.class.getClassLoader();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        for (int i = 0; i < imagePaths.length; i++) {
            String path = imagePaths[i];
            URL url = classLoader.getResource("images/" + path);
            if (url == null) {
                System.err.println("failed to locate image: " + path);
            } else {
                Image image = toolkit.createImage(url);
                String ext = path.substring(path.lastIndexOf('.')+1, path.length());
                if (!ext.equals("gif")) {
                    image = image.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                }
                if (image == null) {
                    System.err.println("failed to create image: " + path);
                } else {
                    path = path.substring(0, path.lastIndexOf('.'));
                    images.put(path, image);
                }
            }
        }
    }

    /**
     * Loads image by object name.
     * Note that the image path is case sensitive.
     * File extensions have to be omitted, e.g. "Image" instead of "Image.jpg".
     * 
     * @param object Object whose name is used to load the image
     * @return an image
     */
    public static Image getImage(Object object) {
        return getImage(object.getClass().getSimpleName());
    }

    /**
     * Loads image by name.
     * Note that the image path is case sensitive.
     * File extension has to be omitted, e.g. "Image" instead of "Image.jpg".
     * 
     * @param name String which is used to load the image
     * @return an image
     */
    public static Image getImage(String name) {
        return images.get(name);
    }
}
