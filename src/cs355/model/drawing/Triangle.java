package cs355.model.drawing;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Add your triangle code here. You can add fields, but you cannot
 * change the ones that already exist. This includes the names!
 */
public class Triangle extends Shape {

	// The three points of the triangle in shape (not world) coordinates.
	private Point2D.Double a;
	private Point2D.Double b;
	private Point2D.Double c;

	/**
	 * Basic constructor that sets all fields.
	 * @param color the color for the new shape.
	 * @param center the center of the new shape.
	 * @param a the first point, relative to the center.
	 * @param b the second point, relative to the center.
	 * @param c the third point, relative to the center.
	 */
	public Triangle(Color color, Point2D.Double center, Point2D.Double a,
					Point2D.Double b, Point2D.Double c)
	{

		// Initialize the superclass.
		super(color, center);

		// Set fields. We actually want these to be organized in a specific order,
        // such that going from a->b->c->a goes clockwise around the triangle.
        // This makes selection testing much simpler.

        // Find which point has the leftmost (smallest) x. This is point A.

		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 * Getter for the first point.
	 * @return the first point as a Java point.
	 */
	public Point2D.Double getA() {
		return a;
	}

	/**
	 * Setter for the first point.
	 * @param a the new first point.
	 */
	public void setA(Point2D.Double a) {
		this.a = a;
	}

	/**
	 * Getter for the second point.
	 * @return the second point as a Java point.
	 */
	public Point2D.Double getB() {
		return b;
	}

	/**
	 * Setter for the second point.
	 * @param b the new second point.
	 */
	public void setB(Point2D.Double b) {
		this.b = b;
	}

	/**
	 * Getter for the third point.
	 * @return the third point as a Java point.
	 */
	public Point2D.Double getC() {
		return c;
	}

	/**
	 * Setter for the third point.
	 * @param c the new third point.
	 */
	public void setC(Point2D.Double c) {
		this.c = c;
	}

	/**
	 * Calculate the center of the triangle with specified vertices in world coordinates.
	 * @param p1 Vertex 1.
	 * @param p2 Vertex 2.
	 * @param p3 Vertex 3.
	 * @return A point defining the the center of the triangle.
	 */
	public static Point2D.Double findCenter(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
		double xAverage = (p1.x + p2.x + p3.x) / 3;
		double yAverage = (p1.y + p2.y + p3.y) / 3;
		return new Point2D.Double(xAverage, yAverage);
	}

	/**
	 * Calculate the vertex coordinates relative to the center coordinate.
	 * @param center The center of the triangle, in world coordinates.
	 * @param vertex The given vertex, in world coordinates.
	 * @return The (x,y) coordinates of the vertex in object coordinates (relative to the center).
	 */
	public static Point2D.Double findVertexRelativeToCenter(Point2D.Double center, Point2D.Double vertex) {
		double xPos = (vertex.x - center.x);
		double yPos = (vertex.y - center.y);
		return new Point2D.Double(xPos, yPos);
	}

	/**
	 * Add your code to do an intersection test
	 * here. You shouldn't need the tolerance.
	 * @param pt = the point to test against.
	 * @param tolerance = the allowable tolerance.
	 * @return true if pt is in the shape,
	 *		   false otherwise.
	 */
	@Override
	public boolean pointInShape(Point2D.Double pt, double tolerance) {
		Point2D.Double selectObjectPoint = transformScreenToObjectCoordinates(pt);

		/* Do the following tests to find out which side of each line segment
		the point is on.
		(q − p1) · (p2 − p1)⊥ > 0
		(q − p2) · (p3 − p2)⊥ > 0
		(q − p3) · (p1 − p3)⊥ > 0
		 */
		boolean sideA = pointOnTriangleSide(selectObjectPoint, b, a);
		boolean sideB =	pointOnTriangleSide(selectObjectPoint, c, b);
		boolean sideC = pointOnTriangleSide(selectObjectPoint, a, c);

        return (sideA == sideB) && (sideB == sideC);

	}

	/**
	 * Apply the following test to find out which side of line segment the point is on.
	 * (pt - triA) . (triB - triA)⊥ > 0
	 * Use (dy,-dx) for the normal, where dy = triB.y-triA.y and dx = triB.x-triA.x
	 *
	 * @param pt The selected point.
	 * @param triA One triangle vertex.
	 * @param triB Another triangle vertex.
	 * @return True if the point is on the "right" side of the line segment,
	 * if triB is the top vertex and triA is the bottom.
	 */
	private boolean pointOnTriangleSide(Point2D.Double pt, Point2D.Double triA, Point2D.Double triB) {
		double xDot = (pt.x - triA.x) * (triB.y - triA.y);
		double yDot = (pt.y - triA.y) * -(triB.x - triA.x);

		return (xDot + yDot) > 0;
	}

}
