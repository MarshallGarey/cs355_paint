package cs355.model;

import cs355.controller.CS355Controller;
import cs355.controller.PaintController;
import cs355.model.drawing.*;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;
import cs355.solution.CS355;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marshall on 1/24/2017.
 * Main model for paint program.
 * Maintains list of objects on the canvas.
 * This class inherits from CS355Drawing, which inherits from the Observable class.
 * The instance of this model will need to call addObserver on the object that implements the Observer class -
 *   in my case, the view needs to observe the model:
 *     model.addObserver(view);
 * To make changes in the model be reflected in the view:
 *   this.setChanged();
 *   this.notifyObservers();
 * I have encapsulated this in this class's redraw() method.
 */
public class Model extends CS355Drawing {

    /**
     * There is a single model in the program.
     */
    private static Model model = new Model();

    /**
     * List of shapes.
     * Index 0 is the BACK of the canvas (as specified by the CS355Drawing abstract class);
     * higher indexed shapes are IN FRONT of lower indexed shapes.
     */
    private ArrayList<Shape> shapes;

    /**
     * Default constructor - private because this class is a singleton.
     */
    private Model() {
        shapes = new ArrayList<>();
    }

    /**
     * @return The (single/only) instance of the model.
     */
    public static Model getModel() {
        return model;
    }

    @Override
    public Shape getShape(int index) {
        return shapes.get(index);
    }

    @Override
    public int addShape(Shape s) {
        shapes.add(s);
        setChanged();
        return shapes.indexOf(s);
    }

    @Override
    public void deleteShape(int index) {
        shapes.remove(index);
        redraw();
    }

    // Note: the "front" of the canvas is actually the end of the list (high index).
    // Store the shape in a temporary variable,
    // remove it from the list,
    // then add it at the correct index (in this case, simply adding it will put it at the end).
    @Override
    public void moveToFront(int index) {
        Shape s = shapes.get(index);
        shapes.remove(index);
        shapes.add(s);

        // Redraw.
        redraw();
    }

    // Note: the "back" of the canvas is the beginning of the list (index zero).
    // Store the shape in a temporary variable,
    // remove it from the list,
    // then add it at the correct index (index zero).
    @Override
    public void movetoBack(int index) {
        Shape s = shapes.get(index);
        shapes.remove(index);
        shapes.add(0, s);

        // Redraw.
        redraw();
    }

    public boolean hasShapeInFront(int currentShapeIndex) {
        return currentShapeIndex < (shapes.size() - 1);
    }

    // Swap indices of the shape with the next highest index
    @Override
    public void moveForward(int index) {
        // Check if a shape is in front of the selected one.
        if (!hasShapeInFront(index)) {
            return;
        }

        // Yes, do the swap.
        Shape toMoveForward = shapes.get(index);
        Shape toMoveBackward = shapes.get(index+1);
        shapes.set(index, toMoveBackward);
        shapes.set(index+1, toMoveForward);

        // Redraw.
        redraw();
    }

    public boolean hasShapeBehind(int currentShapeIndex) {
        return currentShapeIndex > 0;
    }

    // Swap indices of the shape with the next lowest index
    @Override
    public void moveBackward(int index) {
        // Check if a shape is in behind of the selected one.
        if (!hasShapeBehind(index)) {
            return;
        }

        // Yes, do the swap.
        Shape toMoveForward = shapes.get(index-1);
        Shape toMoveBackward = shapes.get(index);
        shapes.set(index-1, toMoveBackward);
        shapes.set(index, toMoveForward);

        // Redraw.
        redraw();
    }

    @Override
    public List<Shape> getShapes() {
        return shapes;
    }

    @Override
    public List<Shape> getShapesReversed() {
        ArrayList<Shape> shapesReversed = new ArrayList<>(shapes.size());
        for (int i = shapes.size() - 1; i >= 0; i--) {
            shapesReversed.add(shapes.get(i));
        }
        return shapesReversed;
    }

    @Override
    public void setShapes(List<Shape> shapes) {
        this.shapes = new ArrayList<>(shapes);
        redraw();
        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "set of shapes: " + shapes.toString());
    }

    public void redraw() {
        this.setChanged();
        this.notifyObservers();
    }

    /*
     * The following methods are all modifyShape methods.
     * They are used when drawing a shape.
     * These methods modify the dimensions of the shape appropriately, according to the location of the mouse.
     * The modifyShape method figures out which shape to modify.
     */

    public void modifyShape(int currentShapeIndex, // Where the shape is in the shapes list
                            PaintController.Tool selectedTool, // Which shape
                            Point2D.Double point, // Position of the mouse
                            Point2D.Double drawStartingPoint // Starting position of drawing, needed for some shapes
    ) {
        // Do nothing if the shape is invalid.
        Shape s;
        try {
            s = shapes.get(currentShapeIndex);
        } catch (Exception exc) {
            exc.printStackTrace();
            return;
        }

        // Modify a shape.
        // The shape should always match the selectedTool; if it doesn't, then I've done something wrong.
        // The print statements are to help me debug.
        switch (selectedTool) {
            case LINE:
                if (s instanceof Line)
                    modifyLine(s, point);
                else
                    Logger.getLogger(CS355Drawing.class.getName()).log(Level.SEVERE,
                            "ERROR in modifyShape: shape isn't a line");
                break;
            case SQUARE:
                if (s instanceof Square)
                    modifySquare(s, point, drawStartingPoint);
                else
                    Logger.getLogger(CS355Drawing.class.getName()).log(Level.SEVERE,
                            "ERROR in modifyShape: shape isn't a square");
                break;
            case RECTANGLE:
                if (s instanceof Rectangle)
                    modifyRectangle(s, point, drawStartingPoint);
                else
                    Logger.getLogger(CS355Drawing.class.getName()).log(Level.SEVERE,
                            "ERROR in modifyShape: shape isn't a rectangle");
                break;
            case CIRCLE:
                if (s instanceof Circle)
                    modifyCircle(s, point, drawStartingPoint);
                else
                    Logger.getLogger(CS355Drawing.class.getName()).log(Level.SEVERE,
                            "ERROR in modifyShape: shape isn't a circle");
                break;
            case ELLIPSE:
                if (s instanceof Ellipse)
                    modifyEllipse(s, point, drawStartingPoint);
                else
                    Logger.getLogger(CS355Drawing.class.getName()).log(Level.SEVERE,
                            "ERROR in modifyShape: shape isn't an ellipse");
                break;
            default:
                break;
        }

        // Notify the view of the change.
        redraw();
    }

    private Point2D.Double findUpperLeft(Point2D.Double mouse, Point2D.Double drawStartingPoint,
                                         double width, double height) {
        Point2D.Double upperLeft = new Point2D.Double();

        // Find which corner of the rectangle or square is the upper left corner.
        // Keep in mind the coordinate system: (0,0) is top-left of the canvas.

        // Mouse is above starting corner.
        if (mouse.getY() < drawStartingPoint.y) {
            // Set the upper-left corner above the starting corner by the size.
            upperLeft.y = drawStartingPoint.y - height;

            // If the mouse is to the left, set upper-left corner left of the starting corner by the size.
            if (mouse.getX() < drawStartingPoint.x) {
                upperLeft.x = drawStartingPoint.x - width;
            }
            // Otherwise, the upper-left corner has the same x position as the starting corner.
            else {
                upperLeft.x = drawStartingPoint.x;
            }
        }
        // Mouse is below starting corner.
        else {
            upperLeft.y = drawStartingPoint.y;
            // If mouse event is left of current upper left corner,
            // set the upper-left corner left of the starting corner by the size.
            if (mouse.getX() < drawStartingPoint.x) {
                upperLeft.x = drawStartingPoint.x - width;
            }
            // Otherwise, the starting point is the upper-left corner.
            else {
                upperLeft.x = drawStartingPoint.x;
            }
        }
        return upperLeft;
    }

    private void modifyEllipse(Shape s, Point2D.Double mouse, Point2D.Double drawStartingPoint) {
        Ellipse ellipse = (Ellipse) s;

        // Find the radius: use the differences in X and Y.
        double width = Math.abs(drawStartingPoint.x - mouse.getX());
        double height = Math.abs(drawStartingPoint.y - mouse.getY());
        ellipse.setWidth(width);
        ellipse.setHeight(height);

        // Find which corner is upper left.
        Point2D upperLeft = findUpperLeft(mouse, drawStartingPoint, width, height);

        // Now set the center of the circle.
        ellipse.getCenter().x = upperLeft.getX() + (width / 2);
        ellipse.getCenter().y = upperLeft.getY() + (height / 2);
    }

    private void modifyCircle(Shape s, Point2D.Double mouse, Point2D.Double drawStartingPoint) {
        Circle circle = (Circle) s;

        // Find the radius: use the smaller of the differences in X and Y.
        double xDiff = Math.abs(drawStartingPoint.x - mouse.getX());
        double yDiff = Math.abs(drawStartingPoint.y - mouse.getY());
        double diameter = (xDiff < yDiff) ? xDiff : yDiff;
        double radius = diameter / 2;
        circle.setRadius(radius);

        // Find which corner is upper left.
        Point2D upperLeft = findUpperLeft(mouse, drawStartingPoint, diameter, diameter);

        // Now set the center of the circle.
        circle.getCenter().x = upperLeft.getX() + radius;
        circle.getCenter().y = upperLeft.getY() + radius;
    }

    private void modifyRectangle(Shape s, Point2D.Double mouse, Point2D.Double drawStartingPoint) {
        Rectangle rectangle = (Rectangle) s;

        // Find the width and height: use the differences in X and Y.
        double width = Math.abs(drawStartingPoint.x - mouse.getX());
        double height = Math.abs(drawStartingPoint.y - mouse.getY());
        rectangle.setWidth(width);
        rectangle.setHeight(height);

        // Find which corner is upper left (it can change).
        rectangle.setCenter(rectangle.findCenter(findUpperLeft(mouse, drawStartingPoint, width, height)));
    }

    private void modifySquare(Shape s, Point2D.Double mouse, Point2D.Double drawStartingPoint) {
        Square square = (Square) s;

        // Find the size: the smaller of the difference in X or Y.
        double xDiff = Math.abs(drawStartingPoint.x - mouse.getX());
        double yDiff = Math.abs(drawStartingPoint.y - mouse.getY());
        double size = (xDiff < yDiff) ? xDiff : yDiff;
        square.setSize(size);

        // Find which corner is upper left (it can change).
        square.setCenter(square.findCenter(findUpperLeft(mouse, drawStartingPoint, size, size)));
    }

    private void modifyLine(Shape s, Point2D.Double mouse) {
        Line l = (Line)s;
        Point2D.Double end = new Point2D.Double(mouse.getX() - l.getCenter().x, mouse.getY() - l.getCenter().y);
        l.setEnd(end);
    }

    /*
     * The following methods are all of the form "makeNew<NameOfShape>(MouseEvent, Color)
     * They initialize the shape, add it to the list of shapes, and return the index of the shape in the list.
     * The makeNewShape method figures out which shape to make. It returns the index of the shape in the list.
     */

    public int makeNewShape(PaintController.Tool selectedTool, Point2D.Double point, Color currentColor) {
        switch (selectedTool) {
            case LINE:
                return makeNewLine(point, currentColor);
            case SQUARE:
                return makeNewSquare(point, currentColor);
            case RECTANGLE:
                return makeNewRectangle(point, currentColor);
            case CIRCLE:
                return makeNewCircle(point, currentColor);
            case ELLIPSE:
                return makeNewEllipse(point, currentColor);
            default:
                // indicate that no shape was added
                return -1;
        }
    }

    private int makeNewCircle(Point2D.Double center, Color currentColor) {
        return addShape(new Circle(currentColor, center, 0));
    }

    private int makeNewEllipse(Point2D.Double center, Color currentColor) {
        return addShape(new Ellipse(currentColor, center, 0, 0));
    }

    private int makeNewSquare(Point2D.Double center, Color currentColor) {
        return addShape(new Square(currentColor, center, 0));
    }

    private int makeNewRectangle(Point2D.Double center, Color currentColor) {
        return addShape(new Rectangle(currentColor, center, 0, 0));
    }

    private int makeNewLine(Point2D.Double point, Color currentColor) {
        // Endpoints: start and end are in the same place, initially.
        // Add the line to the model.
        return addShape(new Line(currentColor, point, point));
    }

    /**
     * Adds a triangle to the model.
     *
     * @param currentColor Color of the triangle.
     * @param points The world positions of the 3 vertices of the triangle.
     * @return The index of the new triangle in the models list of shapes.
     */
    public int makeNewTriangle(Color currentColor, ArrayList<Point2D.Double> points) {
        // The world positions of the 3 vertices are passed. A triangle stores its center in world position,
        // with the vertices relative to the center. We need to calculate the center and the relative positions of the
        // vertices. The center is the average of the world coordinates of the vertices.
        Point2D.Double center = Triangle.findCenter(points.get(0), points.get(1), points.get(2));
        int index = addShape(new Triangle(
                currentColor,
                center,
                Triangle.findVertexRelativeToCenter(center, points.get(0)),
                Triangle.findVertexRelativeToCenter(center, points.get(1)),
                Triangle.findVertexRelativeToCenter(center, points.get(2)))
        );
        redraw();
        return index;
    }

    /**
     * Determine which (if any) shape was selected.
     * @param x The x coordinate of the selection.
     * @param y The y coordinate of the selection.
     * @return The index of the selected shape, or -1 if no shape was selected.
     */
    public int selectShape(int x, int y) {

        // Tolerance is given in the specs. Used for selecting a line. Scale it by the zoom factor.
        double tolerance = 4.0 / CS355.getController().getCurrentZoom();

        // This selected point is currently in world coordinates.
        // It will be translated to object coordinates
        // when tested if it intersects with an object.
        Point2D.Double selectedPoint = new Point2D.Double((double) x, (double) y);

        // Test every shape, in forward order (most front one first, most rear one last).
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape s = shapes.get(i);
            if (s.pointInShape(selectedPoint, tolerance)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Move a shape a dx and dy pixels (in world coordinates).
     *
     * @param currentShapeIndex Index to the shapes list to the shape to be moved.
     * @param startingPoint TODO
     * @param dx Change in x in world coordinates
     * @param dy Change in y in world coordinates
     */
    public void moveShape(int currentShapeIndex, Point2D.Double startingPoint, int dx, int dy) {
        Shape s = shapes.get(currentShapeIndex);
        s.move(dx, dy);

        // Moving lines is different than other shapes.
//        if (s instanceof Line) {
//            Line l = (Line) s;
//            l.move(startingPoint, dx, dy);
//        }
//        else {
//            s.move(dx, dy);
//        }

        // Redraw
        redraw();
    }

    /**
     * Rotate a shape.
     * The change in angle is calculated from the startingAngle, mouse position,
     * and shape position (all in world coordinates).
     *
     * @param currentShapeIndex Index to the shapes list of the shape to be rotated.
     * @param startingAngle Previous angle, subtract from new angle for difference.
     * @param mouseX Mouse world x position.
     * @param mouseY Mouse world y position.
     * @return New angle.
     */
    public double rotateShape(int currentShapeIndex, double startingAngle, int mouseX, int mouseY) {
        // Rotate, update the view and return the new angle.
        double newAngle = getShape(currentShapeIndex).rotate(startingAngle, mouseX, mouseY);
        redraw();
        return newAngle;
    }

    /**
     * Calculate the angle from the mouse and the shape's x axis.
     *
     * @param mouseX Mouse world x position.
     * @param mouseY Mouse world y position.
     * @param shapeIndex Index to the shapes list to get the shape.
     * @return The angle between mouse and shape's positive x axis.
     */
    public double findAngleBetweenMouseAndShape(int mouseX, int mouseY, int shapeIndex) {
        Shape s = getShape(shapeIndex);
        Point2D.Double point = s.transformWorldToObjectCoordinates(
                new Point2D.Double(mouseX, mouseY));
        return Math.atan2(point.y, point.x);
    }
}
