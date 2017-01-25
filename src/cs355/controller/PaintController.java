package cs355.controller;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.*;
import cs355.solution.CS355;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;
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
    private int currentShapeIndex = -1;

    // Currently selected tool.
    public enum Tool {
        NONE, COLOR, LINE, SQUARE, RECTANGLE, CIRCLE, ELLIPSE, TRIANGLE, SELECT, ZOOM_IN, ZOOM_OUT
    }
    private Tool selectedTool = NONE;

    /**
     * Default constructor
     */
    public PaintController() {}

    @Override
    public void colorButtonHit(Color c) {
        // Update current color indicator.
        currentColor = new Color(c.getRGB());
        GUIFunctions.changeSelectedColor(currentColor);
        selectedTool = Tool.COLOR;
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

    }

    @Override
    public void doMoveBackward() {

    }

    @Override
    public void doSendToFront() {

    }

    @Override
    public void doSendtoBack() {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        GUIFunctions.printf("Mouse pressed");
        // Do nothing if the mouse press wasn't in the canvas.
        if (!mouseInCanvas(e)) {
            Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "mousePressed not in canvas");
            return;
        }

        // Do something depending on the selected tool.
        switch (selectedTool) {
            // TODO: perhaps for every shape, I'll just make a single call, like this:
            // case LINE: case SQUARE: ... case TRIANGLE:
            //     currentShapeIndex = Model.getModel().makeNewShape(selectedTool, e);
            case LINE:
                makeNewLine(e);
                break;
            default:
                Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO,
                        "Mouse pressed, tool = " + selectedTool.toString());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        GUIFunctions.printf("Mouse released");
        // Do something depending on the selected tool.
        switch (selectedTool) {
            case LINE:
                Model.getModel().modifyShape(currentShapeIndex, selectedTool, e);
                currentShapeIndex = -1;
                break;
            default:
                Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO,
                        "Mouse released, tool = " + selectedTool.toString());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        GUIFunctions.printf("Mouse dragged");
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        GUIFunctions.printf("Mouse moved");
        switch(selectedTool) {
            case LINE:
                if (currentShapeIndex >= 0)
                    Model.getModel().modifyShape(currentShapeIndex, selectedTool, e);
                break;
            default:
                break;
        }
    }

    // Helper methods

    private boolean mouseInCanvas(MouseEvent e) {
        return (e.getX() > 0) && (e.getY() > 0);
    }

    private void makeNewLine(MouseEvent e) {
        // Endpoints: start and end are in the same place, initially.
        Point2D.Double start = new Point2D.Double(e.getX(), e.getY());
        Point2D.Double end = new Point2D.Double(e.getX(), e.getY());

        // Add the line to the model.
        currentShapeIndex = Model.getModel().addShape(new Line(currentColor, start, end));
    }



    private void finishLine(MouseEvent e) {
        Point2D.Double end = new Point2D.Double(e.getX(), e.getY());
    }

    // Getters and Setters

    public Color getCurrentColor() {
        return currentColor;
    }

    public Tool getSelectedTool() {
        return selectedTool;
    }
}
