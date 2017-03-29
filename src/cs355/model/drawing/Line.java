package cs355.model.drawing;

import cs355.GUIFunctions;

import java.awt.Color;
import java.awt.geom.Point2D;

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
		double t =
				Math.max(0.0, Math.min(1.0,
						dotProduct(new Point2D.Double(pt.x - center.x, pt.y - center.y),
								new Point2D.Double(pt.x - end.x, pt.y - end.y)
						) / length));
		Point2D.Double projectionPoint = new Point2D.Double(
				center.x + t*(dx),
				center.y + t*(dy)
		);

		double distance = Point2D.distance(pt.x, pt.y, projectionPoint.x, projectionPoint.y);
		GUIFunctions.printf("To Line: %f, Dist: %f", distanceToLine, distance);

		// If this distance is within the line segment endpoints,
		// count it as falling within the line.
		return distance < length && distanceToLine < tolerance;

	}

	@Override
    public boolean pointInHandle(Point2D.Double pt) {
	    return false;
    }

	public void move(Point2D.Double startingPoint) {
		// Figure out which point to move
		double distanceStart = Point2D.distance(center.x, center.y, startingPoint.x, startingPoint.y);
		double distanceEnd = Point2D.distance(end.x, end.y, startingPoint.x, startingPoint.y);

		// Starting point
		if (distanceStart < distanceEnd) {
			double dx = startingPoint.x - center.x;
			double dy = startingPoint.y - center.y;
			center.x += dx;
			center.y += dy;
		}
		// Ending point
		else {
			double dx = startingPoint.x - end.x;
			double dy = startingPoint.y - end.y;
			end.x += dx;
			end.y += dy;
		}
	}

	private double dotProduct(Point2D.Double p1, Point2D.Double p2) {
		return p1.x * p2.x + p1.y * p2.y;
	}

}
