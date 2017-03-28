package cs355.controller;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.*;
import cs355.model.drawing.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cs355.controller.PaintController.Tool.*;

/**
 * Created by Marshall on 1/24/2017.
 * Main controller class
 */
public class PaintController implements CS355Controller, MouseListener, MouseMotionListener {

    // Currently selected color.
    private Color currentColor;

    // Index of currently selected shape.
    // A negative value indicates no shape is selected.
    private final int NO_SHAPE_SELECTED = -1;
    private int currentShapeIndex = NO_SHAPE_SELECTED;

    // Initial point of the mouse press.
    private Point2D.Double startingPoint;

    // Currently selected tool.
    public enum Tool {
        NONE, LINE, SQUARE, RECTANGLE, CIRCLE, ELLIPSE, TRIANGLE, SELECT, ZOOM_IN, ZOOM_OUT
    }
    private Tool selectedTool = NONE;

    // Triangles need special handling.
    // Keep track of how many points have been selected and what they are.
    private int triangleNumPointsSelected;

    // And keep track of what the points are (for the triangle).
    private ArrayList<Point2D.Double> trianglePoints;

    /**
     * Default constructor
     */
    public PaintController() {}

    @Override
    public void colorButtonHit(Color c) {
        // Update current color indicator.
        currentColor = new Color(c.getRGB());
        GUIFunctions.changeSelectedColor(currentColor);

        // Change the color of the selected shape and redraw.
        if (shapeIsSelected()) {
            Model.getModel().getShape(currentShapeIndex).setColor(currentColor);
            Model.getModel().updateObservers();
        }
    }

    @Override
    public void lineButtonHit() {
        selectedTool = LINE;
    }

    @Override
    public void squareButtonHit() {
        selectedTool = SQUARE;
    }

    @Override
    public void rectangleButtonHit() {
        selectedTool = RECTANGLE;
    }

    @Override
    public void circleButtonHit() {
        selectedTool = CIRCLE;
    }

    @Override
    public void ellipseButtonHit() {
        selectedTool = ELLIPSE;
    }

    @Override
    public void triangleButtonHit() {
        selectedTool = TRIANGLE;

        // Reset number of triangle points selected (to restart triangle drawing).
        triangleNumPointsSelected = 0;
    }

    @Override
    public void selectButtonHit() {
        selectedTool = SELECT;
    }

    @Override
    public void zoomInButtonHit() {
        selectedTool = ZOOM_IN;
    }

    @Override
    public void zoomOutButtonHit() {
        selectedTool = ZOOM_OUT;
    }

    @Override
    public void hScrollbarChanged(int value) {

    }

    @Override
    public void vScrollbarChanged(int value) {

    }

    @Override
    public void openScene(File file) {

    }

    @Override
    public void toggle3DModelDisplay() {

    }

    @Override
    public void keyPressed(Iterator<Integer> iterator) {

    }

    @Override
    public void openImage(File file) {

    }

    @Override
    public void saveImage(File file) {

    }

    @Override
    public void toggleBackgroundDisplay() {

    }

    @Override
    public void saveDrawing(File file) {
        Model.getModel().save(file);
    }

    @Override
    public void openDrawing(File file) {
        Model.getModel().open(file);
        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "OpenDrawing");
    }

    @Override
    public void doDeleteShape() {
        if (shapeIsSelected()) {
            int index = currentShapeIndex;
            currentShapeIndex = NO_SHAPE_SELECTED;
            Model.getModel().deleteShape(index);
        }
    }

    @Override
    public void doEdgeDetection() {

    }

    @Override
    public void doSharpen() {

    }

    @Override
    public void doMedianBlur() {

    }

    @Override
    public void doUniformBlur() {

    }

    @Override
    public void doGrayscale() {

    }

    @Override
    public void doChangeContrast(int contrastAmountNum) {

    }

    @Override
    public void doChangeBrightness(int brightnessAmountNum) {

    }

    @Override
    public void doMoveForward() {
        if (shapeIsSelected()) {
            if (Model.getModel().hasShapeInFront(currentShapeIndex)) {
                Model.getModel().moveForward(currentShapeIndex);
                currentShapeIndex++;
            }
        }
    }

    @Override
    public void doMoveBackward() {
        if (shapeIsSelected()) {
            if (Model.getModel().hasShapeBehind(currentShapeIndex)) {
                Model.getModel().moveBackward(currentShapeIndex);
                currentShapeIndex--;
            }
        }
    }

    @Override
    public void doSendToFront() {
        if (shapeIsSelected()) {
            Model.getModel().moveToFront(currentShapeIndex);
            currentShapeIndex = Model.getModel().getShapes().size()-1;
        }
    }

    @Override
    public void doSendtoBack() {
        if (shapeIsSelected()) {
            Model.getModel().movetoBack(currentShapeIndex);
            currentShapeIndex = 0;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (selectedTool) {
            case TRIANGLE:
                switch (triangleNumPointsSelected) {
                    case 0:
                        trianglePoints = new ArrayList<>();
                        trianglePoints.add(new Point2D.Double(e.getX(), e.getY()));
                        triangleNumPointsSelected++;
                        break;
                    case 1:
                        trianglePoints.add(new Point2D.Double(e.getX(), e.getY()));
                        triangleNumPointsSelected++;
                        break;
                    case 2:
                        trianglePoints.add(new Point2D.Double(e.getX(), e.getY()));
                        Model.getModel().makeNewTriangle(currentColor, trianglePoints);
                        triangleNumPointsSelected = 0;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        GUIFunctions.printf("Mouse pressed");
        // Do nothing if the mouse press wasn't in the canvas.
        if (!mouseInCanvas(e)) {
            Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "mousePressed not in canvas");
            return;
        }

        // Retain the coordinates of the initial mouse press.
        // They're used when modifying/moving shapes.
        startingPoint = new Point2D.Double(e.getX(), e.getY());

        // Do something depending on the selected tool.
        switch (selectedTool) {
            // For every shape, just create a new shape.
            case LINE: case SQUARE: case RECTANGLE: case CIRCLE: case ELLIPSE:
                currentShapeIndex = Model.getModel().makeNewShape(selectedTool, e, currentColor);
                break;
            case SELECT:
                // Model.selectShape will return the index of the selected shape, or a negative number if the selected
                // point was not inside any shape.
                currentShapeIndex = Model.getModel().selectShape(e.getX(), e.getY());

                // Redraw the screen to update the highlights
                // TODO: only do this if a currentShapeIndex changed
                // TODO (if needed): Optimization: only redraw what needs to be redrawn instead of the whole canvas
                Model.getModel().updateObservers();

                if (shapeIsSelected()) {
                    // A shape was selected.
                    Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO,
                            "Selected shape is " +
                            Model.getModel().getShapes().get(currentShapeIndex).toString());
                }
                else {
                    // The clicked point wasn't inside any shape.
                    Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "Selected blank space.");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Do something depending on the selected tool.
        switch (selectedTool) {
            case LINE: case SQUARE: case RECTANGLE: case CIRCLE: case ELLIPSE:
                int shape = currentShapeIndex;
                currentShapeIndex = NO_SHAPE_SELECTED;
                Model.getModel().modifyShape(shape, selectedTool, e, startingPoint);
                break;
            default:
                break;
        }

        // Now that the mouse has been released,
        // get rid of the starting coordinates of the mouse press.
        startingPoint = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        GUIFunctions.printf("");
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Do nothing if no shape is selected.
        if (!shapeIsSelected()) {
            return;
        }
        switch(selectedTool) {
            case LINE: case SQUARE: case RECTANGLE: case CIRCLE: case ELLIPSE: case TRIANGLE:
                Model.getModel().modifyShape(currentShapeIndex, selectedTool, e, startingPoint);
                break;
            case SELECT:
                // Move the selected shape.
                Model.getModel().moveShape(currentShapeIndex,
                        startingPoint,
                        e.getX() - (int)startingPoint.x,
                        e.getY() - (int)startingPoint.y);

                // Update the starting mouse coordinates so they next time the mouse
                // is dragged, the change in x and y will be relative to this mouse
                // move and not the initial press.
                startingPoint.x = e.getX();
                startingPoint.y = e.getY();
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    // ***********************************************************************
    // Helper methods
    // ***********************************************************************

    /**
     * @return True if any shape is selected. False otherwise.
     */
    private boolean shapeIsSelected() {
        // TODO: Make it so that a shape isn't selected while it's being drawn.
        return (currentShapeIndex >= 0);
    }

    /**
     * @param s The shape to test.
     * @return True if shape s is selected, false otherwise.
     */
    public boolean isSelected(Shape s) {
        return (currentShapeIndex >= 0) && (Model.getModel().getShape(currentShapeIndex).equals(s));
    }

    /**
     * @param e Mouse event, which contains the (x,y) coordinates of the mouse.
     * @return True if the mouse is inside the canvas, false otherwise.
     */
    private boolean mouseInCanvas(MouseEvent e) {
        return (e.getX() > 0) && (e.getY() > 0);
    }

}
