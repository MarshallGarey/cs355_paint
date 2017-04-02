package cs355.view;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.*;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;
import cs355.solution.CS355;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Marshall on 1/24/2017.
 * Main viewer class - draws objects on the screen
 */
public class View implements ViewRefresher, Observer {

    private HighlightShape highlightShape;
    public static final int HANDLE_RADIUS = 4;

    /**
     * Default constructor
     */
    public View() {
        Model.getModel().addObserver(this);
        highlightShape = new HighlightShape();
    }

    // Shell builds and passes in Graphics2D object
    // Draw on this object
    // Shell refreshes the drawing area
    @Override
    public void refreshView(Graphics2D g2d) {
        for (cs355.model.drawing.Shape s : Model.getModel().getShapes()) {
            drawShape(s, g2d);
        }
        if (highlightShape.shape != null) {
            g2d.setTransform(highlightShape.objTransform);
            doHighlight(g2d);
            highlightShape.shape = null;
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
        AffineTransform objToView = calculateTransformation(s);
        g2d.setTransform(objToView);

        // If this shape selected, we want to highlight it and draw a handle at the end,
        // so we need to save the shape and its transformation to avoid recalculation
        boolean selected = CS355.getController().isSelected(s);
        if (selected) {
            highlightShape.shape = s;
            highlightShape.objTransform = objToView;
        }

        // Now draw the appropriate shape:
        if (s instanceof Line) {
            drawLine(s, g2d, selected);
        }
        else if (s instanceof Square) {
            drawSquare(s, g2d, selected);
        }
        else if (s instanceof Rectangle) {
            drawRectangle(s, g2d, selected);
        }
        else if (s instanceof Circle) {
            drawCircle(s, g2d, selected);
        }
        else if (s instanceof Ellipse) {
            drawEllipse(s, g2d, selected);
        }
        else if (s instanceof Triangle) {
            drawTriangle(s, g2d, selected);
        }
    }

    private AffineTransform calculateTransformation(Shape s) {

        // We need to transform the object coordinates from object->world->view coordinates
        //     Object->world: first rotate, then translate.
        //     World->view: inverse translate, scale
        // The final matrix will look like this:
        // scale(rotateX) scale(-rotateY) scale(centerX-viewX)
        // scale(rotateY) scale(rotateX)  scale(centerY-viewY)
        // 0              0               1
        //
        // The AffineTransform expects the following matrix entries to its constructor:
        // xScale xShear xTranslate
        // yShear yScale yTranslate
        // 0      0      1
/*
        // Rotation:
        double xRotate = Math.cos(s.getRotation());
        double yRotate = Math.sin(s.getRotation()); // shearing

        // Translation (combining obj->world and world->view translation):
        Point2D.Double viewport = CS355.getController().getViewportOrigin();
        double xTranslate = s.getCenter().x - viewport.x;
        double yTranslate = s.getCenter().y - viewport.y;

        // Scale
        double scale = CS355.getController().getCurrentZoom();

        // Final matrix entries:
        double xScale = scale * xRotate; // x and y scale are the same
        double yScale = xScale;
        double xShear = scale * -yRotate;
        double yShear = scale * yRotate;
        xTranslate *= scale;
        yTranslate *= scale;

        return new AffineTransform(
                xScale, yShear, xShear, yScale, xTranslate, yTranslate
        );
*/

        /*
        // IMPORTANT: Java applies the transformations in the REVERSE ORDER
        // in which I specify them.
        */
        AffineTransform objToView = new AffineTransform();

        // *********************
        // World->view:
        // *********************

        // Scale
        double scale = CS355.getController().getCurrentZoom();
        objToView.scale(scale, scale);

        // Translate
        Point2D.Double viewport = CS355.getController().getViewportOrigin();
        objToView.translate(-viewport.x, -viewport.y);

        // *********************
        // Object->World:
        // *********************

        // Translate
        objToView.translate(s.getCenter().x, s.getCenter().y);

        // Rotate
        objToView.rotate(s.getRotation());

        return objToView;

    }

    private void drawLine(Shape s, Graphics2D g2d, boolean selected) {
        Line l = (Line) s;
        g2d.drawLine(0, 0, (int)l.getEnd().x, (int)l.getEnd().y);
        if (selected) {
            highlightShape.endPointB = l.getEnd();
        }
    }

    private void drawSquare(Shape s, Graphics2D g2d, boolean selected) {
        Square square = (Square) s;
        int upperLeftX = (int)-square.getSize()/2;
        int upperLeftY = (int)-square.getSize()/2;
        int width = (int)square.getSize();
        int height = width;
        g2d.fillRect(upperLeftX, upperLeftY, width, height);

        // Save info so it can be highlighted later without recalculating.
        if (selected) {
            highlightShape.centerX = highlightShape.centerY = 0;
            highlightShape.width = width;
            highlightShape.height = height;
            highlightShape.upperLeftX = upperLeftX;
            highlightShape.upperLeftY = upperLeftY;
        }
    }

    private void drawRectangle(Shape s, Graphics2D g2d, boolean selected) {
        Rectangle rect = (Rectangle) s;
        int upperLeftX = (int)-rect.getWidth()/2;
        int upperLeftY = (int)-rect.getHeight()/2;
        int width = (int)rect.getWidth();
        int height = (int)rect.getHeight();
        g2d.fillRect(upperLeftX, upperLeftY, width, height);

        // Save info so it can be highlighted later without recalculating.
        if (selected) {
            highlightShape.centerX = highlightShape.centerY = 0;
            highlightShape.width = width;
            highlightShape.height = height;
            highlightShape.upperLeftX = upperLeftX;
            highlightShape.upperLeftY = upperLeftY;
        }
    }

    private void drawCircle(Shape s, Graphics2D g2d, boolean selected) {
        Circle circle = (Circle) s;
        int r = (int) circle.getRadius();
        int upperLeftX = -r;
        int upperLeftY = -r;
        int width = r*2;
        int height = width;
        g2d.fillOval(upperLeftX, upperLeftY, width, height // Width and height
        );

        // Save info so it can be highlighted later without recalculating.
        if (selected) {
            highlightShape.centerX = highlightShape.centerY = 0;
            highlightShape.width = width;
            highlightShape.height = height;
            highlightShape.upperLeftX = upperLeftX;
            highlightShape.upperLeftY = upperLeftY;
        }
    }

    private void drawEllipse(Shape s, Graphics2D g2d, boolean selected) {
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

        // Save info so it can be highlighted later without recalculating.
        if (selected) {
            highlightShape.centerX = highlightShape.centerY = 0;
            highlightShape.width = width;
            highlightShape.height = height;
            highlightShape.upperLeftX = upperLeftX;
            highlightShape.upperLeftY = upperLeftY;
        }
    }

    private void drawTriangle(Shape s, Graphics2D g2d, boolean selected) {
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

        if (selected) {
            highlightShape.xPoints = xPoints;
            highlightShape.yPoints = yPoints;
            highlightShape.centerX = (xPoints[0] + xPoints[1] + xPoints[2]) / 3;
            highlightShape.centerY = (yPoints[0] + yPoints[1] + yPoints[2]) / 3;
        }
    }

    private void doHighlight(Graphics2D g2d) {
        Shape s = highlightShape.shape;

        // Highlight the shape in a different color.
        Color complementaryColor = s.getComplementaryColor();
        g2d.setColor(complementaryColor);

        // For a line, just draw handles on each endpoint.
        if (s instanceof Line) {
            drawRotationHandle((int)highlightShape.endPointA.x,
                    (int)highlightShape.endPointA.y, g2d);
            drawRotationHandle((int)highlightShape.endPointB.x,
                    (int)highlightShape.endPointB.y, g2d);
        }
        else {
            // Outline all other shapes.
            if (s instanceof Triangle) {
                g2d.drawPolygon(highlightShape.xPoints, highlightShape.yPoints, 3);
            }
            else if (s instanceof Circle || s instanceof Ellipse) {
                g2d.drawOval(highlightShape.upperLeftX, highlightShape.upperLeftY,
                        highlightShape.width, highlightShape.height);
            }
            else if (s instanceof Rectangle || s instanceof Square) {
                g2d.drawRect(highlightShape.upperLeftX, highlightShape.upperLeftY,
                        highlightShape.width, highlightShape.height);
            }

            // Draw the rotation handle in the center of the shape.
            drawRotationHandle(highlightShape.centerX, highlightShape.centerY, g2d);
        }
    }

    private void drawRotationHandle(int centerX, int centerY, Graphics2D g2d) {
        // Undo the scaling transformation because the handle
        // should be drawn the same size regardless of the zoom level.
        double scale = 1/CS355.getController().getCurrentZoom();
        g2d.scale(scale, scale);

        // Now draw the handle.
        int handleRadius = HANDLE_RADIUS;
        int width = handleRadius*2;
        int height = width;
        g2d.drawOval(centerX-handleRadius, centerY-handleRadius, width, height);
    }

}
