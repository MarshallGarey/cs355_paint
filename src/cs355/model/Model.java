package cs355.model;

import com.sun.org.apache.xpath.internal.operations.Mod;
import cs355.controller.PaintController;
import cs355.model.drawing.*;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;
import cs355.solution.CS355;

import java.awt.*;
import java.awt.event.MouseEvent;
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
 * Always call the following when the model changes in order for those changes to be reflected in the view:
 *   this.setChanged();
 *   this.notifyObservers();
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
        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "Model constructor");
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
        updateObservers();
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
        updateObservers();
    }

    // TODO: Swap indices of the shape with the next highest index
    @Override
    public void moveForward(int index) {

    }

    // TODO: Swap indices of the shape with the next lowest index
    @Override
    public void moveBackward(int index) {

    }

    @Override
    public List<Shape> getShapes() {
        return shapes;
    }

    // TODO: implement
    @Override
    public List<Shape> getShapesReversed() {
        return null;
    }

    @Override
    public void setShapes(List<Shape> shapes) {
        this.shapes = new ArrayList<>(shapes);
        updateObservers();
        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "set of shapes: " + shapes.toString());
    }

    private void updateObservers() {
        this.setChanged();
        this.notifyObservers();
    }

    public void modifyShape(int currentShapeIndex, // Where the shape is in the shapes list
                            PaintController.Tool selectedTool, // Which shape
                            MouseEvent e, // Contains position of the mouse
                            Point2D.Double drawStartingPoint // Starting position of drawing, needed for some shapes
    ) {
        // Do nothing if the shape is invalid.
        Shape s = null;
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
                    modifyLine(s, e);
                else
                    CS355Drawing.logMessage("ERROR in modifyShape: shape isn't a line");
                break;
            case SQUARE:
                if (s instanceof Square)
                    modifySquare(s, e, drawStartingPoint);
                else
                    CS355Drawing.logMessage("ERROR in modifyShape: shape isn't a square");
                break;
            case RECTANGLE:
                if (s instanceof Rectangle)
                    modifyRectangle(s, e, drawStartingPoint);
                else
                    CS355Drawing.logMessage("ERROR in modifyShape: shape isn't a rectangle");
                break;
            default:
                break;
        }

        // Notify view of the change.
        updateObservers();
    }

    private Point2D.Double findUpperLeft(MouseEvent e, Point2D.Double drawStartingPoint, double width, double height) {
        Point2D.Double upperLeft = new Point2D.Double();

        // Find which corner of the rectangle or square is the upper left corner.
        // Keep in mind the coordinate system: (0,0) is top-left of the canvas.

        // Mouse is above starting corner.
        if (e.getY() < drawStartingPoint.y) {
            // Set the upper-left corner above the starting corner by the size.
            upperLeft.y = drawStartingPoint.y - height;

            // If the mouse is to the left, set upper-left corner left of the starting corner by the size.
            if (e.getX() < drawStartingPoint.x) {
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
            if (e.getX() < drawStartingPoint.x) {
                upperLeft.x = drawStartingPoint.x - width;
            }
            // Otherwise, the starting point is the upper-left corner.
            else {
                upperLeft.x = drawStartingPoint.x;
            }
        }
        return upperLeft;
    }

    private void modifyRectangle(Shape s, MouseEvent e, Point2D.Double drawStartingPoint) {
        Rectangle rectangle = (Rectangle) s;

        // Find the size: the smaller of the difference in X or Y.
        double width = Math.abs(drawStartingPoint.x - e.getX());
        double height = Math.abs(drawStartingPoint.y - e.getY());
        rectangle.setWidth(width);
        rectangle.setHeight(height);

        // Find which corner is upper left (it can change).
        rectangle.setUpperLeft(findUpperLeft(e, drawStartingPoint, width, height));
    }

    private void modifySquare(Shape s, MouseEvent e, Point2D.Double drawStartingPoint) {
        Square square = (Square) s;

        // Find the size: the smaller of the difference in X or Y.
        double xDiff = Math.abs(drawStartingPoint.x - e.getX());
        double yDiff = Math.abs(drawStartingPoint.y - e.getY());
        double size = (xDiff < yDiff) ? xDiff : yDiff;
        square.setSize(size);

        // Find which corner is upper left (it can change).
        square.setUpperLeft(findUpperLeft(e, drawStartingPoint, size, size));
    }

    private void modifyLine(Shape s, MouseEvent e) {
        Line l = (Line)s;
        Point2D.Double end = new Point2D.Double(e.getX(), e.getY());
        l.setEnd(end);
    }

    public int makeNewShape(PaintController.Tool selectedTool, MouseEvent e, Color currentColor) {
        switch (selectedTool) {
            case LINE:
                return makeNewLine(e, currentColor);
            case SQUARE:
                return makeNewSquare(e, currentColor);
            case RECTANGLE:
                return makeNewRectangle(e, currentColor);
            default:
                // indicate that no shape was added
                return -1;
        }
    }

    private int makeNewSquare(MouseEvent e, Color currentColor) {
        Point2D.Double upperLeft = new Point2D.Double(e.getX(), e.getY());
        return Model.getModel().addShape(new Square(currentColor, upperLeft, 0));
    }

    private int makeNewRectangle(MouseEvent e, Color currentColor) {
        Point2D.Double upperLeft = new Point2D.Double(e.getX(), e.getY());
        return Model.getModel().addShape(new Rectangle(currentColor, upperLeft, 0, 0));
    }

    private int makeNewLine(MouseEvent e, Color currentColor) {
        // Endpoints: start and end are in the same place, initially.
        Point2D.Double start = new Point2D.Double(e.getX(), e.getY());
        Point2D.Double end = new Point2D.Double(e.getX(), e.getY());

        // Add the line to the model.
        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO,
                "New line color = " + currentColor.toString());
        return Model.getModel().addShape(new Line(currentColor, start, end));
    }

    /**
     * @param p1 Point 1.
     * @param p2 Point 2.
     * @return The distance between the two points.
     */
    private double findDistance(Point2D.Double p1, Point2D.Double p2) {
        // Distance formula.
        return Math.sqrt( // Square root of
                ((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())) + // Square of the difference in X plus
                        ((p1.getY() - p2.getY()) * (p1.getY() - p2.getY())) // Square of the difference in Y
        );
    }

}
