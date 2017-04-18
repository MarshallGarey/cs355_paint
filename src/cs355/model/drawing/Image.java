package cs355.model.drawing;

import cs355.model.image.CS355Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Marshall Garey
 */
public class Image extends CS355Image {

    public Image () {
        super();
    }

    public Image(int width, int height) {
        super(width, height);
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
        // Temporary image to write data to.
        Image newImage = new Image(getWidth(), getHeight());
        int[] rgb = new int[3];
        int width = getWidth();
        int height = getHeight();

        // For each pixel:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // Get the color from the image.
                getPixel(x, y, rgb);

                // Copy the border pixels to the new image, but don't filter them.
                // We need to copy or else the image will have a blank border.
                // So, if the pixel is not a border pixel, blur it.
                if (!(x == 0 || y == 0 || x == width-1 || y == height-1)) {

                    // Blur the pixel
                    medianBlurPixel(rgb, x, y);
                }

                // Set the pixel in the new image to preserve old data.
                newImage.setPixel(x, y, rgb);
            }
        }

        // Update this image.
        this.setPixels(newImage);
    }

    @Override
    public void uniformBlur() {
        // Temporary image to write data to.
        Image newImage = new Image(getWidth(), getHeight());
        int[] rgb = new int[3];
        int width = getWidth();
        int height = getHeight();

        // For each pixel:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // Get the color from the image.
                getPixel(x, y, rgb);

                // Copy the border pixels to the new image, but don't filter them.
                // We need to copy or else the image will have a blank border.
                // So, if the pixel is not a border pixel, blur it.
                if (!(x == 0 || y == 0 || x == width-1 || y == height-1)) {

                    // Blur the pixel
                    blurPixel(rgb, x, y);
                }

                // Set the pixel in the new image to preserve old data.
                newImage.setPixel(x, y, rgb);
            }
        }

        // Update this image.
        this.setPixels(newImage);
    }

    /**
     * Performs a pixel blur
     * Can potentially crash from indexing out of bounds, so I need to make
     * sure I don't index out of bounds
     *
     * @param pixel Return value
     * @param x X coordinate of the pixel
     * @param y Y coordinate of the pixel
     */
    private void blurPixel(int[] pixel, int x, int y) {

        int redTotal;
        int greenTotal;
        int blueTotal;
        redTotal = greenTotal = blueTotal = 0;
        int[] neighbor = new int[3];

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                // Get the neighboring pixel
                getPixel(x + i, y + j, neighbor);

                // Add the red, green, and blue values to totals
                redTotal += neighbor[0];
                greenTotal += neighbor[1];
                blueTotal += neighbor[2];

            }
        }

        pixel[0] = redTotal / 9;
        pixel[1] = greenTotal / 9;
        pixel[2] = blueTotal / 9;
    }

    /**
     * Performs a median pixel blur
     * Can potentially crash from indexing out of bounds, so I need to make
     * sure I don't index out of bounds
     *
     * @param pixel Return value
     * @param x X coordinate of the pixel
     * @param y Y coordinate of the pixel
     */
    private void medianBlurPixel(int[] pixel, int x, int y) {

        ArrayList<Integer> reds = new ArrayList<>();
        ArrayList<Integer> greens = new ArrayList<>();
        ArrayList<Integer> blues = new ArrayList<>();
        int[] neighbor = new int[3];

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                // Get the neighboring pixel
                getPixel(x + i, y + j, neighbor);

                // Add the red, green, and blue values to totals
                reds.add(neighbor[0]);
                greens.add(neighbor[1]);
                blues.add(neighbor[2]);

            }
        }
        // Sort
        java.util.Collections.sort(reds);
        java.util.Collections.sort(greens);
        java.util.Collections.sort(blues);

        // Get middle
        pixel[0] = reds.get(5);
        pixel[1] = greens.get(5);
        pixel[2] = blues.get(5);
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
