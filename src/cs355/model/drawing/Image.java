package cs355.model.drawing;

import cs355.model.image.CS355Image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Marshall Garey
 */
public class Image extends CS355Image {

    public Image() {
        super();
    }

    // Convert to a buffered image
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

    // Convert the image to HSB, zero the saturation channel, and convert back to RGB.
    @Override
    public void grayscale() {
        // Preallocate the arrays.
        int[] rgb = new int[3];
        float[] hsb = new float[3];
        int width = getWidth();
        int height = getHeight();

        // For each pixel:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the color from the image.
                getPixel(x, y, rgb);
                // Convert to HSB.
                Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
                // Zero the saturation
                hsb[1] = 0;
                // Convert back to RGB.
                Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
                rgb[0] = c.getRed();
                rgb[1] = c.getGreen();
                rgb[2] = c.getBlue();
                // Set the pixel.
                setPixel(x, y, rgb);

            }
        }
    }

    // TODO: contrast isn't working correctly
    // Range [-100,100], where 100 is maximum contrast (8x) and -100 is zero contrast
    // and 0 is no change.
    // With input brightness in the range [0,1] and amount as the contrast parameter:
    //   outputBrightness=((amount+100)/100)^4*(inputBrightness-0.5) + 0.5
    @Override
    public void contrast(int amount) {
        // Preallocate the arrays.
        int[] rgb = new int[3];
        float[] hsb = new float[3];
        int width = getWidth();
        int height = getHeight();

        // Amount must be in the range [-100,100]
        if (amount < -100) amount = -100;
        else if (amount > 100) amount = 100;

        // For each pixel:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the color from the image.
                getPixel(x, y, rgb);
                // Convert to HSB.
                Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
                // Contrast and clip brightness to the range [0,1]
                float newB = (float)(Math.pow(((double)amount+100)/100,4)*(hsb[2]-0.5)+0.5);
                hsb[2] = newB;
                if (hsb[2] > 1) hsb[2] = 1;
                else if (hsb[2] < 0) hsb[2] = 0;
                // Convert back to RGB.
                Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
                rgb[0] = c.getRed();
                rgb[1] = c.getGreen();
                rgb[2] = c.getBlue();
                // Set the pixel.
                setPixel(x, y, rgb);

            }
        }
    }

    @Override
    public void brightness(int amount) {
        // Preallocate the arrays.
        int[] rgb = new int[3];
        float[] hsb = new float[3];
        int width = getWidth();
        int height = getHeight();

        // For each pixel:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the color from the image.
                getPixel(x, y, rgb);
                // Convert to HSB.
                Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
                // Brighten and clip to the range [0,1].
                // Since amount is a percentage, convert it to a percentage.
                float percent = (float)amount*(float)0.01;
                hsb[2] += percent;
                if (hsb[2] > 1) hsb[2] = 1;
                else if (hsb[2] < 0) hsb[2] = 0;
                // Convert back to RGB.
                Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
                rgb[0] = c.getRed();
                rgb[1] = c.getGreen();
                rgb[2] = c.getBlue();
                // Set the pixel.
                setPixel(x, y, rgb);

            }
        }
    }

}
