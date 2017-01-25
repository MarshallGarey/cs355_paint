package cs355.model;

import cs355.model.drawing.CS355Drawing;
import cs355.model.drawing.Shape;

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
}
