package cs355.model.drawing;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Add your square code here. You can add fields, but you cannot
 * change the ones that already exist. This includes the names!
 */
public class Square extends Shape {

    // The size of this Square.
    private double size;

    /**
     * Basic constructor that sets all fields.
     *
     * @param color  the color for the new shape.
     * @param center the center of the new shape.
     * @param size   the size of the new shape.
     */
    public Square(Color color, Point2D.Double center, double size) {

        // Initialize the superclass.
        super(color, center);

        // Set the field.
        this.size = size;
    }

    /**
     * Getter for this Square's size.
     *
     * @return the size as a double.
     */
    public double getSize() {
        return size;
    }

    /**
     * Setter for this Square's size.
     *
     * @param size the new size.
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * Calculates center of the rectangle from its upper-left corner.
     *
     * @param upperLeft Point defining the upper-left corner of the rectangle.
     * @return Point defining the center of the rectangle.
     */
    public Point2D.Double findCenter(Point2D.Double upperLeft) {
        return new Point2D.Double(upperLeft.x + size / 2, upperLeft.y + size / 2);
    }

    /**
     * Do an intersection test.
     *
     * @param pt        = the point to test against, in screen coordinates.
     * @param tolerance = the allowable tolerance.
     * @return true if pt is in the shape,
     * false otherwise.
     */
    @Override
    public boolean pointInShape(Point2D.Double pt, double tolerance) {

        // Transform to object coordinates, then test the boundaries.
        Point2D.Double selectObjectCoordinates = transformWorldToObjectCoordinates(pt);
        return  selectObjectCoordinates.y >= -(size / 2) && // Top
                selectObjectCoordinates.y <= (size / 2) &&  // Bottom
                selectObjectCoordinates.x >= -(size / 2) && // Left
                selectObjectCoordinates.x <= (size / 2);    // Right
    }

}
