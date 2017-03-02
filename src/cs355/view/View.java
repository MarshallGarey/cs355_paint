package cs355.view;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.*;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;

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

    // TODO: finish
    // Shell builds and passes in Graphics2D object
    // Draw on this object
    // Shell refreshes the drawing area
    @Override
    public void refreshView(Graphics2D g2d) {
        for (cs355.model.drawing.Shape s : Model.getModel().getShapes()) {
            drawShape(s, g2d);
        }
    }

    // TODO: finish
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

        // Now draw the appropriate shape:
        if (s instanceof Line) {
            Line l = (Line) s;
            g2d.drawLine((int)l.getCenter().x, (int)l.getCenter().y, (int)l.getEnd().x, (int)l.getEnd().y);
        }
        else if (s instanceof Square) {
            Square square = (Square) s;
            g2d.fillRect(
                    (int)-square.getSize()/2, // Upper left x
                    (int)-square.getSize()/2, // Upper left y
                    (int)square.getSize(), // width
                    (int)square.getSize() // height
            );
        }
        else if (s instanceof Rectangle) {
            Rectangle rect = (Rectangle) s;
            g2d.fillRect(
                    (int)-rect.getWidth()/2, // Upper left x
                    (int)-rect.getHeight()/2, // Upper left y
                    (int)rect.getWidth(), // width
                    (int)rect.getHeight() // height
            );
        }
        else if (s instanceof Circle) {
            Circle circle = (Circle) s;
            int r = (int) circle.getRadius();
            g2d.fillOval(
                    (-r), // Upper left x coordinate of bounding box
                    (-r), // Upper left y coordinate of bounding box
                    r*2, r*2 // Width and height
            );
        }
        else if (s instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) s;
            int w = (int) ellipse.getWidth();
            int h = (int) ellipse.getHeight();
            g2d.fillOval(
                    (-(w/2)), // Upper left x coordinate of bounding box
                    (-(h/2)), // Upper left y coordinate of bounding box
                    w, h // Width and height
            );
        }
        else if (s instanceof Triangle) {
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
        }
    }

}
