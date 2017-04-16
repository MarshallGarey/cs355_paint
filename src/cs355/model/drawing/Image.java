package cs355.model.drawing;

import cs355.model.image.CS355Image;

import java.awt.image.BufferedImage;

/**
 * Created by Marshall Garey
 */
public class Image extends CS355Image {

    public Image() {
        super();
    }

    // TODO: Convert to a buffered image
    @Override
    public BufferedImage getImage() {
        BufferedImage bufferedImage = new BufferedImage(
                getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = getWidth();
        int height = getHeight();
        int i = 0;
        int rgbArray[] = new int[width*height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x,y,getRgbPixel(x,y));
            }
        }

        return bufferedImage;
    }

    // TODO: I don't know if red is the lowest byte or the 3rd byte.
    // Using the direct color model, with red, green, and blue 1 byte each,
    //   the color is stored in 4 bytes like so: 0x00RRGGBB
    private int getRgbPixel(int x, int y) {
        int red = getRed(x, y);
        int green = getGreen(x, y);
        int blue = getBlue(x, y);
        return ((red&0xff)<<16) | ((green&0xff)<<8) | ((blue&0xff));
    }

    @Override
    public void edgeDetection() {

    }

    @Override
    public void sharpen() {

    }

    @Override
    public void medianBlur() {

    }

    @Override
    public void uniformBlur() {

    }

    @Override
    public void grayscale() {

    }

    @Override
    public void contrast(int amount) {

    }

    @Override
    public void brightness(int amount) {

    }

}
