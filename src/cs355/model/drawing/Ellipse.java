package cs355.model.drawing;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Add your ellipse code here. You can add fields, but you cannot
 * change the ones that already exist. This includes the names!
 */
public class Ellipse extends Shape {

	// The center of this shape.
	private Point2D.Double center;

	// The width of this shape.
	private double width;

	// The height of this shape.
	private double height;

	/**
	 * Basic constructor that sets all fields.
	 * @param color the color for the new shape.
	 * @param center the center of the new shape.
	 * @param width the width of the new shape.
	 * @param height the height of the new shape.
	 */
	public Ellipse(Color color, Point2D.Double center, double width, double height) {

		// Initialize the superclass.
		super(color);

		// Set fields.
		this.center = center;
		this.width = width;
		this.height = height;
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
	 * Getter for this shape's width.
	 * @return this shape's width as a double.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Setter for this shape's width.
	 * @param width the new width.
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Getter for this shape's height.
	 * @return this shape's height as a double.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Setter for this shape's height.
	 * @param height the new height.
	 */
	public void setHeight(double height) {
		this.height = height;
	}
}
