package cs355.model;

import cs355.model.drawing.CS355Drawing;
import cs355.model.drawing.Shape;

import java.util.List;

/**
 * Created by Marshall on 1/24/2017.
 * Main model for paint program.
 * Maintains list of objects on the canvas.
 */
public class Model extends CS355Drawing {

    /**
     * Default constructor
     */
    public Model() {}

    @Override
    public Shape getShape(int index) {
        return null;
    }

    @Override
    public int addShape(Shape s) {
        return 0;
    }

    @Override
    public void deleteShape(int index) {

    }

    @Override
    public void moveToFront(int index) {

    }

    @Override
    public void movetoBack(int index) {

    }

    @Override
    public void moveForward(int index) {

    }

    @Override
    public void moveBackward(int index) {

    }

    @Override
    public List<Shape> getShapes() {
        return null;
    }

    @Override
    public List<Shape> getShapesReversed() {
        return null;
    }

    @Override
    public void setShapes(List<Shape> shapes) {

    }
}
