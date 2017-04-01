package cs355.model.drawing;

import cs355.view.View;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * This is the base class for all of your shapes.
 * Make sure they all extend this class.
 */
public abstract class Shape {

	// The color of this shape.
	protected Color color;

	// The center of this shape.
	protected Point2D.Double center;

	// The rotation of this shape (in radians).
	protected double rotation;

	/**
	 * Basic constructor that sets fields.
	 * It initializes rotation to 0.
	 * @param color the color for the new shape.
	 * @param center the center point of the new shape.
	 */
	public Shape(Color color, Point2D.Double center) {
		this.color = color;
		this.center = center;
		rotation = 0.0;
	}

	/**
	 * Getter for this shape's color.
	 * @return the color of this shape.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Setter for this shape's color
	 * @param color the new color for the shape.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Getter for this shape's center.
	 * @return this shape's center as a Java point.
	 */
	public Point2D.Double getCenter() {
		return center;
	}

	/**
	 * Setter for this shape's center.
	 * @param center the new center as a Java point.
	 */
	public void setCenter(Point2D.Double center) {
		this.center = center;
	}

	/**
	 * Getter for this shape's rotation.
	 * @return the rotation as a double.
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Setter for this shape's rotation.
	 * @param rotation the new rotation.
	 */
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	/**
	 * Find the complement of the shape's color. Used for highlighting purposes.
	 * @return The complementary color of the shape's color.
	 */
	public Color getComplementaryColor() {
		return new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}

    /**
     * Transform the selection point pt to object coordinates (inverse translate, inverse rotate).
     * Remember that the transformation operations are performed in reverse order that I call them.
     * @param point The selection world xy coordinates.
     * @return The object xy coordinates.
     */
    public Point2D.Double transformWorldToObjectCoordinates(Point2D.Double point) {
        Point2D.Double selectObjectCoordinates = new Point2D.Double();
        AffineTransform transform = new AffineTransform();
        transform.rotate(-rotation);
        transform.translate(-center.x, -center.y);
        transform.transform(point, selectObjectCoordinates);
        return selectObjectCoordinates;
    }

    /**
     * Used to test for whether the user clicked inside a shape or not.
     * @param pt = the point to test whether it's in the shape or not.
     * @param tolerance = the tolerance for testing. Mostly used for lines.
     * @return true if pt is in the shape, false otherwise.
     */
    public abstract boolean pointInShape(Point2D.Double pt, double tolerance);

    /**
     * Move the shape.
     * @param dx Number of pixels to move in the x direction.
     * @param dy Number of pixels to move in the y direction.
     */
    public void move(double dx, double dy) {
        center.x += dx;
        center.y += dy;
    }

    public boolean pointInHandle(Point2D.Double pt) {
        // Transform to object coordinates, then test the boundaries.
        Point2D.Double selectObjectCoordinates = transformWorldToObjectCoordinates(pt);

        // True if the distance between pt and the center of the handle is less than the radius
        // of the handle.
        return (Point2D.Double.distance(selectObjectCoordinates.x,
                selectObjectCoordinates.y, 0, 0)
                <= View.HANDLE_RADIUS);
    }
}
