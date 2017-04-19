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
                    sharpenPixel(rgb, x, y);
                }

                // Set the pixel in the new image to preserve old data.
                newImage.setPixel(x, y, rgb);
            }
        }

        // Update this image.
        this.setPixels(newImage);
    }

    private void sharpenPixel(int[] pixel, int x, int y) {
        int redTotal;
        int greenTotal;
        int blueTotal;
        redTotal = greenTotal = blueTotal = 0;
        int[] neighbor = new int[3];


        // Get the neighboring pixel
        // Subtract the red, green, and blue neighbors
        // left neighbor
        getPixel(x - 1, y, neighbor);
        redTotal -= neighbor[0];
        greenTotal -= neighbor[1];
        blueTotal -= neighbor[2];
        // right neighbor
        getPixel(x + 1, y, neighbor);
        redTotal -= neighbor[0];
        greenTotal -= neighbor[1];
        blueTotal -= neighbor[2];
        // top neighbor
        getPixel(x, y - 1, neighbor);
        redTotal -= neighbor[0];
        greenTotal -= neighbor[1];
        blueTotal -= neighbor[2];
        // bottom neighbor
        getPixel(x, y + 1, neighbor);
        redTotal -= neighbor[0];
        greenTotal -= neighbor[1];
        blueTotal -= neighbor[2];

        // Add 6 times the middle
        getPixel(x, y, neighbor);
        redTotal += 6*neighbor[0];
        greenTotal += 6*neighbor[1];
        blueTotal += 6*neighbor[2];

        // Divide by 2
        pixel[0] = redTotal / 2;
        pixel[1] = greenTotal / 2;
        pixel[2] = blueTotal / 2;

        // Clip to the range [0,255]
        for (int i = 0; i < 3; i++) {
            if (pixel[i] < 0) pixel[i] = 0;
            else if (pixel[i] > 255) pixel[i] = 255;
        }
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
     * Performs a uniform pixel blur using 8 surrounding neighbors.
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
     * TODO: THIS IS NOT FINISHED
     * Performs a median pixel blur using 8 surrounding neighbors.
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

                // Add the red, green, and blue values to lists.
                reds.add(neighbor[0]);
                greens.add(neighbor[1]);
                blues.add(neighbor[2]);

            }
        }
        // Sort
        java.util.Collections.sort(reds);
        java.util.Collections.sort(greens);
        java.util.Collections.sort(blues);

        // Get the median color
        int medianColor[] = new int[3];
        medianColor[0] = reds.get(5);
        medianColor[1] = greens.get(5);
        medianColor[2] = blues.get(5);

        // Find which neighboring pixel is "closest" to the median color
        // by treating the colors as vectors and computing the
        // squared distance between them and using the least distance.
        // Squared distance is (r2-r1)^2 + (g2-g1)^2 + (b2-b1)^2

        int closestPixel[] = neighbor;

        // Make the least distance negative so it will be updated on the first iteration.
        double least = -1;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                // Get the neighboring pixel
                getPixel(x + i, y + j, neighbor);

                // Compute squared distance
                double dist = squaredDistance(neighbor, medianColor);

                // Update the lowest distance and closest pixel.
                if ((least < 0) || (dist < least)) {
                    least = dist;
                    closestPixel = neighbor;
                }
            }
        }

        pixel[0] = closestPixel[0];
        pixel[1] = closestPixel[1];
        pixel[2] = closestPixel[2];
    }

    /**
     * Compute the squared distance between 2 pixels:
     *   (r2-r1)^2 + (g2-g1)^2 + (b2-b1)^2
     * @param p1 First pixel
     * @param p2 Second pixel
     * @return Squared distance between them.
     */
    private double squaredDistance(int[] p1, int[] p2) {
        double r = (p2[0] - p1[0]);
        double g = (p2[1] - p1[1]);
        double b = (p2[2] - p1[2]);
        return r*r + g*g + b*b;
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
