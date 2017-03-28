package cs355.view;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.*;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;
import cs355.solution.CS355;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Marshall on 1/24/2017.
 * Main viewer class - draws objects on the screen
 */
public class View implements ViewRefresher, Observer {

    /**
     * Default constructor
     */
    public View() {
        Model.getModel().addObserver(this);
    }

    // Shell builds and passes in Graphics2D object
    // Draw on this object
    // Shell refreshes the drawing area
    @Override
    public void refreshView(Graphics2D g2d) {
        for (cs355.model.drawing.Shape s : Model.getModel().getShapes()) {
            drawShape(s, g2d);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        GUIFunctions.refresh();
    }

    /**
     * Draw a shape.
     * @param s The shape to be drawn.
     * @param g2d This object is used to draw graphics on the screen.
     */
    private void drawShape(Shape s, Graphics2D g2d) {
        g2d.setColor(s.getColor());

        // We need to transform the object coordinates to world/screen coordinates
        // (which are the same thing for now - they'll be different later):
        //     First rotate, then translate.
        // IMPORTANT: Java applies the transformations in the REVERSE ORDER in which I specify them.
        AffineTransform objToWorld = new AffineTransform();

        // Translate
        objToWorld.translate(s.getCenter().x, s.getCenter().y);

        // Rotate
        objToWorld.rotate(s.getRotation());

        // Apply the transformation
        g2d.setTransform(objToWorld);

        // Is a shape selected?
        boolean doHighlight = CS355.getController().isSelected(s);

        // Now draw the appropriate shape:
        if (s instanceof Line) {
            drawLine(s, g2d, doHighlight);
        }
        else if (s instanceof Square) {
            drawSquare(s, g2d, doHighlight);
        }
        else if (s instanceof Rectangle) {
            drawRectangle(s, g2d, doHighlight);
        }
        else if (s instanceof Circle) {
            drawCircle(s, g2d, doHighlight);
        }
        else if (s instanceof Ellipse) {
            drawEllipse(s, g2d, doHighlight);
        }
        else if (s instanceof Triangle) {
            drawTriangle(s, g2d, doHighlight);
        }
    }

    private void drawLine(Shape s, Graphics2D g2d, boolean doHighlight) {
        Line l = (Line) s;
        g2d.drawLine(0, 0, (int)l.getEnd().x, (int)l.getEnd().y);

        // TODO: highlight
    }

    private void drawSquare(Shape s, Graphics2D g2d, boolean doHighlight) {
        Square square = (Square) s;
        int upperLeftX = (int)-square.getSize()/2;
        int upperLeftY = (int)-square.getSize()/2;
        int width = (int)square.getSize();
        int height = width;
        g2d.fillRect(upperLeftX, upperLeftY, width, height);

        if (doHighlight) {
            // Highlight the shape. Use a different color so it can be seen.
            Color complementaryColor = s.getComplementaryColor();
            g2d.setColor(complementaryColor);
            g2d.drawRect(upperLeftX, upperLeftY, width, height);

            // Draw a rotation handle
            drawRotationHandle(0, 0, g2d);
        }
    }

    private void drawRectangle(Shape s, Graphics2D g2d, boolean doHighlight) {
        Rectangle rect = (Rectangle) s;
        int upperLeftX = (int)-rect.getWidth()/2;
        int upperLeftY = (int)-rect.getHeight()/2;
        int width = (int)rect.getWidth();
        int height = (int)rect.getHeight();
        g2d.fillRect(upperLeftX, upperLeftY, width, height);

        if (doHighlight) {
            // Highlight the shape. Use a different color so it can be seen.
            Color complementaryColor = s.getComplementaryColor();
            g2d.setColor(complementaryColor);
            g2d.drawRect(upperLeftX, upperLeftY, width, height);

            // Draw a rotation handle
            drawRotationHandle(0, 0, g2d);
        }
    }

    private void drawCircle(Shape s, Graphics2D g2d, boolean doHighlight) {
        Circle circle = (Circle) s;
        int r = (int) circle.getRadius();
        int upperLeftX = -r;
        int upperLeftY = -r;
        int width = r*2;
        int height = width;
        g2d.fillOval(upperLeftX, upperLeftY, width, height // Width and height
        );

        if (doHighlight) {
            // Highlight the shape. Use a different color so it can be seen.
            Color complementaryColor = s.getComplementaryColor();
            g2d.setColor(complementaryColor);
            g2d.drawOval(upperLeftX, upperLeftY, width, height);

            // We don't need a rotation handle for a circle because it is the same
            // no matter how it is rotated.
        }
    }

    private void drawEllipse(Shape s, Graphics2D g2d, boolean doHighlight) {
        Ellipse ellipse = (Ellipse) s;
        int width = (int) ellipse.getWidth();
        int height = (int) ellipse.getHeight();
        int upperLeftX = (-(width/2));
        int upperLeftY = (-(height/2));
        g2d.fillOval(
                upperLeftX, // Upper left x coordinate of bounding box
                upperLeftY, // Upper left y coordinate of bounding box
                width, height // Width and height
        );

        if (doHighlight) {
            // Highlight the shape. Use a different color so it can be seen.
            Color complementaryColor = s.getComplementaryColor();
            g2d.setColor(complementaryColor);
            g2d.drawOval(upperLeftX, upperLeftY, width, height);

            // Draw a rotation handle.
            drawRotationHandle(0, 0, g2d);
        }
    }

    private void drawTriangle(Shape s, Graphics2D g2d, boolean doHighlight) {
        Triangle triangle = (Triangle) s;

        // List of X coordinates.
        int xPoints[] = new int[3];
        xPoints[0] = (int)(triangle.getA().x);
        xPoints[1] = (int)(triangle.getB().x);
        xPoints[2] = (int)(triangle.getC().x);

        // List of Y coordinates.
        int yPoints[] = new int[3];
        yPoints[0] = (int)(triangle.getA().y);
        yPoints[1] = (int)(triangle.getB().y);
        yPoints[2] = (int)(triangle.getC().y);

        // Now I can draw.
        g2d.fillPolygon(xPoints, yPoints, 3);

        if (doHighlight) {
            // Highlight the shape. Use a different color so it can be seen.
            Color complementaryColor = s.getComplementaryColor();
            g2d.setColor(complementaryColor);
            g2d.drawPolygon(xPoints, yPoints, 3);

            // Draw a rotation handle.
            drawRotationHandle(
                    (xPoints[0]+xPoints[1]+xPoints[2])/3, // center x coordinate
                    (yPoints[0]+yPoints[1]+yPoints[2])/3, // center y coordinate
                    g2d);
        }
    }

    private void drawRotationHandle(int centerX, int centerY, Graphics2D g2d) {
        // I am arbitrarily deciding the default radius of the rotation handle.
        int handleRadius = 4; // TODO: multiply by scale for zooming in/out
        int width = handleRadius*2;
        int height = width;
        g2d.drawOval(centerX-handleRadius, centerY-handleRadius, width, height);
    }
}
