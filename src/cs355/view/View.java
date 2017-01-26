package cs355.view;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.*;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;

import java.awt.*;
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

    private void drawShape(Shape s, Graphics2D g2d) {
        g2d.setColor(s.getColor());
        if (s instanceof Line) {
            Line l = (Line) s;
            g2d.drawLine((int)l.getStart().x, (int)l.getStart().y, (int)l.getEnd().x, (int)l.getEnd().y);
        }
        else if (s instanceof Square) {
            Square square = (Square) s;
            g2d.fillRect(
                    (int)square.getUpperLeft().x, // Upper left x
                    (int)square.getUpperLeft().y, // Upper left y
                    (int)square.getSize(), // width
                    (int)square.getSize() // height
            );
        }
        else if (s instanceof Rectangle) {
            Rectangle rect = (Rectangle) s;
            g2d.fillRect(
                    (int)rect.getUpperLeft().x, // Upper left x
                    (int)rect.getUpperLeft().y, // Upper left y
                    (int)rect.getWidth(), // width
                    (int)rect.getHeight() // height
            );
        }
        else if (s instanceof Circle) {
            Circle circle = (Circle) s;
            int r = (int) circle.getRadius();
            g2d.fillOval(
                    (int) (circle.getCenter().x - r),
                    (int) (circle.getCenter().y - r),
                    r*2, r*2
            );
        }
        else if (s instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) s;
            int w = (int) ellipse.getWidth();
            int h = (int) ellipse.getHeight();
            g2d.fillOval(
                    (int) (ellipse.getCenter().x - (w/2)),
                    (int) (ellipse.getCenter().y - (h/2)),
                    w, h
            );
        }
        else if (s instanceof Triangle) {
            Triangle triangle = (Triangle) s;

            // List of X coordinates.
            int xPoints[] = new int[3];
            xPoints[0] = (int)triangle.getA().x;
            xPoints[1] = (int)triangle.getB().x;
            xPoints[2] = (int)triangle.getC().x;

            // List of Y coordinates.
            int yPoints[] = new int[3];
            yPoints[0] = (int)triangle.getA().y;
            yPoints[1] = (int)triangle.getB().y;
            yPoints[2] = (int)triangle.getC().y;

            // Now I can draw.
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }

}
