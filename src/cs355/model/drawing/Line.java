package cs355.model.drawing;

import cs355.GUIFunctions;
import cs355.solution.CS355;
import cs355.view.View;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Add your line code here. You can add fields, but you cannot
 * change the ones that already exist. This includes the names!
 */
public class Line extends Shape {

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
		super(color, start);

		// Set the field.
		this.end = end;
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

	/**
	 * Add your code to do an intersection test
	 * here. You <i>will</i> need the tolerance.
	 * @param pt = the point to test against.
	 * @param tolerance = the allowable tolerance.
	 * @return true if pt is in the shape,
	 *		   false otherwise.
	 */
	@Override
	public boolean pointInShape(Point2D.Double pt, double tolerance) {

		/*
  float minimum_distance(vec2 v, vec2 w, vec2 p) {
  // Return minimum distance between line segment vw and point p
  const float l2 = length_squared(v, w);  // i.e. |w-v|^2 -  avoid a sqrt
  if (l2 == 0.0) return distance(p, v);   // v == w case
  // Consider the line extending the segment, parameterized as v + t (w - v).
  // We find projection of point p onto the line.
  // It falls where t = [(p-v) . (w-v)] / |w-v|^2
  // We clamp t from [0,1] to handle points outside the segment vw.
  const float t = max(0, min(1, dot(p - v, w - v) / l2));
  const vec2 projection = v + t * (w - v);  // Projection falls on the segment
  return distance(p, projection);
}
		 */

		// Center is in world coordinates, but end is in object coordinates where center is the origin (0,0)
		// So maybe make a local copy of end in world coordinates...
		Point2D.Double end = new Point2D.Double(this.end.x + center.x, this.end.y + center.y);

		// Line normal = (-dy,dx)
		// Unit normal: divide by vector length
		double length = Point2D.Double.distance(center.x, center.y, end.x, end.y);
		double dy = (end.y - center.y) / length;
		double dx = (end.x - center.x) / length;
		Point2D.Double normal = new Point2D.Double(-dy, dx);

		// How close pt is to the line:
		// abs( pt . n - start . n )
		double distanceToLine = Math.abs(dotProduct(pt, normal) - dotProduct(center, normal));

		// The line parameterized as start + t(end-start)
		// Project pt onto this line, limiting t to the range [0,1] to ensure we're inside
		// the line segment.
		//   t = [(pt-start) . (pt-end)] / length^2  (use length^2 to avoid a square root)
		// max(0, min(1, dot(pt-start, pt-end)/lengthSquared))
		//
		//   projectionPoint = start + t(end-start)
		// distance(pt, projectionPoint) is the distance from the line segment to the point.
		double dotProductResult = dotProduct(
				new Point2D.Double(pt.x - center.x, pt.y - center.y),
				new Point2D.Double(pt.x - end.x, pt.y - end.y)
		);
		double t = Math.max(0.0, Math.min(1.0, dotProductResult / length));
		Point2D.Double projectionPoint = new Point2D.Double(
				center.x + t*(dx),
				center.y + t*(dy)
		);

		double distance = Point2D.distance(pt.x, pt.y, projectionPoint.x, projectionPoint.y);
		GUIFunctions.printf("To Line: %f, Dist: %f, line length: %f", distanceToLine, distance, length);

		// If this distance is within the line segment endpoints,
		// count it as falling within the line.
		return distance < length && distanceToLine < tolerance;

	}

	/**
	 * @param pt Point to test, in world coordinates.
	 * @return True if the pt is in either handle, false otherwise.
	 */
	@Override
    public boolean pointInHandle(Point2D.Double pt) {
		// Make a local copy of the end point in world coordinates.
		Point2D.Double end = new Point2D.Double(this.end.x + center.x, this.end.y + center.y);

		double distanceStart = Point2D.distance(center.x, center.y, pt.x, pt.y);
		double distanceEnd = Point2D.distance(end.x, end.y, pt.x, pt.y);
		double radius = CS355.getController().getHandleRadius();
		return ((distanceStart < radius) || (distanceEnd < radius));
    }

	@Override
	public void rotate(double newAngle, double startingAngle) {
		// do nothing
	}

	/**
	 * This method name is a misnomer. Rather than rotating the line,
	 * we move one of the endpoints.
	 *
	 * @param startingAngle Not used
	 * @param mouseX Mouse world x position.
	 * @param mouseY Mouse world y position.
	 * @return 0 - also not needed.
	 */
	@Override
	public double rotate(double startingAngle, int mouseX, int mouseY) {
		// Convert line end point to world coordinates and store as a local variable.
		Point2D.Double end = new Point2D.Double(center.x + this.end.x, center.y + this.end.y);

		// Find distance from the mouse to each endpoint.
		double distanceStart = Point2D.distance(center.x, center.y, mouseX, mouseY);
		double distanceEnd = Point2D.distance(end.x, end.y, mouseX, mouseY);
		if (distanceStart < distanceEnd) {
//			center.x += dx;
//			center.y += dy;
		}
		return 0;
	}

	private double dotProduct(Point2D.Double p1, Point2D.Double p2) {
		return p1.x * p2.x + p1.y * p2.y;
	}

}
