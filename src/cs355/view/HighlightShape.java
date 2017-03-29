package cs355.view;

import cs355.model.drawing.Shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Created by Marshall Garey.
 * A HighlightShape contains the data needed to highlight a shape.
 * The purpose of this class is to store information so it doesn't need to be recalculated.
 * I'm sure there are better ways to do what I need.
 */
class HighlightShape {
    HighlightShape() {
        shape = null;
        objTransform = null;
        upperLeftX = 0;
        upperLeftY = 0;
        width = 0;
        height = 0;
        xPoints = null;
        yPoints = null;
        endPointA = new Point2D.Double(0,0);
        endPointB = null;
        centerX = 0;
        centerY = 0;
    }

    Shape shape;
    AffineTransform objTransform;
    int upperLeftX;
    int upperLeftY;
    int width;
    int height;
    int xPoints[];
    int yPoints[];
    Point2D.Double endPointA;
    Point2D.Double endPointB;
    int centerX;
    int centerY;
}
