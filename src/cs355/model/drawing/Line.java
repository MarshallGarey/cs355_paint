package cs355.model.drawing;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Add your line code here. You can add fields, but you cannot
 * change the ones that already exist. This includes the names!
 */
public class Line extends Shape {

	// The starting point of the line.
	private Point2D.Double start;

	// The ending point of the line.
	private Point2D.Double end;

	/**
	 * Basic constructor that sets all fields.
	 * @param color the color for the new shape.
	 * @param start the starting point.
	 * @param end the ending point.
	 */
	public Line(Color color, Point2D.Double start, Point2D.Double end) {

		// Initialize the superclass.
		super(color);

		// Set fields.
		this.start = start;
		this.end = end;
	}

	/**
	 * Getter for this Line's starting point.
	 * @return the starting point as a Java point.
	 */
	public Point2D.Double getStart() {
		return start;
	}

	/**
	 * Setter for this Line's starting point.
	 * @param start the new starting point for the Line.
	 */
	public void setStart(Point2D.Double start) {
		this.start = start;
	}

	/**
	 * Getter for this Line's ending point.
	 * @return the ending point as a Java point.
	 */
	public Point2D.Double getEnd() {
		return end;
	}

	/**
	 * Setter for this Line's ending point.
	 * @param end the new ending point for the Line.
	 */
	public void setEnd(Point2D.Double end) {
		this.end = end;
	}
}
