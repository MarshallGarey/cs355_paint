package cs355.controller;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.CS355Drawing;
import cs355.model.drawing.Shape;
import cs355.model.scene.CS355Scene;
import cs355.model.scene.Point3D;
import cs355.view.View;

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
    private double startingAngle;
    private boolean rotateCurrentShape = false;

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

    // Current zoom level
    private double currentZoom = 1;
    private final double MIN_ZOOM = 0.25;
    private final double MAX_ZOOM = 4;

    // Viewport
    private Point2D.Double viewportOrigin = new Point2D.Double(0, 0);

    private final int CANVAS_SIZE = 2048;
    private final int CANVAS_MAX = CANVAS_SIZE - 1;
    private final int VIEW_SIZE = 512;
    private final int VIEW_MAX = VIEW_SIZE - 1;

    // When zooming in/out, I change the size and position of the scrollbars.
    // This causes the scrollbarChanged functions to get called.
    // In those functions, check if this value is true; if so, the view
    // doesn't need to be refreshed again.
    private boolean viewRefreshed = false;

    // If true, display the 3D model
    private boolean model3DIsOn = false;

    // The 3D model
    private CS355Scene scene = null;

    // Virtual camera
    private Point3D originalCameraPosition = null;
    private double originalCameraRotation;

    // For debugging
    private boolean ADD_TEST_SHAPES = false;

    /**
     * Default constructor
     */
    public PaintController() {
    }

    public void init() {
        // Initialize the scroll bars
        setScrollbars(currentZoom, currentZoom);
        GUIFunctions.setHScrollBarMax(CANVAS_MAX);
        GUIFunctions.setVScrollBarMax(CANVAS_MAX);

        if (ADD_TEST_SHAPES) {
            makeTestShapes();
        }
    }

    private void makeTestShapes() {
        // Manually add shapes for debugging
        int start = 256;
        int size = 40;
        Model.getModel().makeNewShape(
                SQUARE,
                new Point2D.Double(start, start),
                Color.BLUE);
        Model.getModel().modifyShape(0, SQUARE, new Point2D.Double(start+size,start+size),
                new Point2D.Double(start-size,start-size));

        start = 1024;
        Model.getModel().makeNewShape(
                SQUARE,
                new Point2D.Double(start, start),
                Color.RED);
        Model.getModel().modifyShape(1, SQUARE, new Point2D.Double(start+size,start+size),
                new Point2D.Double(start-size,start-size));
    }

    @Override
    public void colorButtonHit(Color c) {
        // Update current color indicator.
        currentColor = new Color(c.getRGB());
        GUIFunctions.changeSelectedColor(currentColor);

        // Change the color of the selected shape and redraw.
        if (shapeIsSelected()) {
            Model.getModel().getShape(currentShapeIndex).setColor(currentColor);
            Model.getModel().redraw();
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
        if (currentZoom < MAX_ZOOM) {
            double oldZoom = currentZoom;
            currentZoom *= 2;
            GUIFunctions.setZoomText(currentZoom);

            // Change size and position of scrollbars
            viewRefreshed = true;
            setScrollbars(currentZoom, oldZoom);
            GUIFunctions.refresh();
            viewRefreshed = false;
        }
    }

    @Override
    public void zoomOutButtonHit() {
        if (currentZoom > MIN_ZOOM) {
            double oldZoom = currentZoom;
            currentZoom /= 2;
            GUIFunctions.setZoomText(currentZoom);

            // Change size and position of scrollbars
            viewRefreshed = true;
            setScrollbars(currentZoom, oldZoom);
            GUIFunctions.refresh();
            viewRefreshed = false;
        }
    }

    @Override
    public void hScrollbarChanged(int value) {
        // Change viewport position and refresh view
        viewportOrigin.x = value;
        if (!viewRefreshed)
            GUIFunctions.refresh();
    }

    @Override
    public void vScrollbarChanged(int value) {
        // Change viewport position and refresh view
        viewportOrigin.y = value;
        if (!viewRefreshed)
            GUIFunctions.refresh();
    }

    @Override
    public void openScene(File file) {
        scene = new CS355Scene();
        scene.open(file);

        // Initialize virtual camera
        originalCameraPosition = new Point3D(0,0,0);//new Point3D(1024, 1024, 0);
        scene.setCameraPosition(originalCameraPosition);
        originalCameraRotation = scene.getCameraRotation();

        // Draw the scene
        model3DIsOn = true; // on as soon as the scene is opened.
        GUIFunctions.refresh();
    }

    @Override
    public void toggle3DModelDisplay() {
        // Keep false if scene hasn't been opened.
        if (scene == null)
            return;
        model3DIsOn = !model3DIsOn;
        GUIFunctions.refresh();
    }

    @Override
    public void keyPressed(Iterator<Integer> iterator) {
        // Do nothing if 3D model display is turned off.
        if (!model3DIsOn) {
            return;
        }
        Point3D newPos;
        // TODO: Handle key presses here.
        while(iterator.hasNext()) {
            int key = iterator.next();
            Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO,
                    "key pressed: " + key);
            switch (key) {
                case 'A': // Move left
                    newPos = new Point3D(
                            scene.getCameraPosition().x - 5,
                            scene.getCameraPosition().y,
                            scene.getCameraPosition().z);
                    scene.setCameraPosition(newPos);
                    break;
                case 'D': // Move right
                    newPos = new Point3D(
                            scene.getCameraPosition().x + 5,
                            scene.getCameraPosition().y,
                            scene.getCameraPosition().z);
                    scene.setCameraPosition(newPos);
                    break;
                case 'W': // Move forward
                    newPos = new Point3D(
                            scene.getCameraPosition().x,
                            scene.getCameraPosition().y,
                            scene.getCameraPosition().z + 5);
                    scene.setCameraPosition(newPos);
                    break;
                case 'S': // Move backward
                    newPos = new Point3D(
                            scene.getCameraPosition().x,
                            scene.getCameraPosition().y,
                            scene.getCameraPosition().z - 5);
                    scene.setCameraPosition(newPos);
                    break;
                case 'Q': // Turn left
                    scene.setCameraRotation(scene.getCameraRotation() - 0.2);
                    break;
                case 'E': // Turn right
                    scene.setCameraRotation(scene.getCameraRotation() + 0.2);
                    break;
                case 'R': // Move up
                    newPos = new Point3D(
                            scene.getCameraPosition().x,
                            scene.getCameraPosition().y + 5,
                            scene.getCameraPosition().z);
                    scene.setCameraPosition(newPos);
                    break;
                case 'F': // Move down
                    newPos = new Point3D(
                            scene.getCameraPosition().x,
                            scene.getCameraPosition().y - 5,
                            scene.getCameraPosition().z);
                    scene.setCameraPosition(newPos);
                    break;
                case 'H': // Return to the original (home) position and orientation
                    scene.setCameraPosition(originalCameraPosition);
                    scene.setCameraRotation(originalCameraRotation);
                    break;
                case 'O': // Switch to orthographic projection - ignore for lab 5
                    break;
                case 'P': // Switch to perspective projection - ignore for lab 5
                    break;
                default:
                    break;
            }
        }
        GUIFunctions.refresh();
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
            currentShapeIndex = Model.getModel().getShapes().size() - 1;
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
        if (!mouseInCanvas(e)) {
            return;
        }

        // Transform mouse screen coordinates to world coordinates.
        Point2D.Double mouseWorld = viewToWorld(e.getX(), e.getY());

        switch (selectedTool) {
            case TRIANGLE:
                switch (triangleNumPointsSelected) {
                    case 0:
                        trianglePoints = new ArrayList<>();
                        trianglePoints.add(mouseWorld);
                        triangleNumPointsSelected++;
                        break;
                    case 1:
                        trianglePoints.add(mouseWorld);
                        triangleNumPointsSelected++;
                        break;
                    case 2:
                        trianglePoints.add(mouseWorld);
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
        // Do nothing if the mouse press wasn't in the canvas.
        if (!mouseInCanvas(e)) {
            return;
        }

        // Transform mouse screen coordinates to world coordinates.
        Point2D.Double mouseWorld = viewToWorld(e.getX(), e.getY());

        // Retain the coordinates of the initial mouse press.
        // They're used when modifying/moving shapes.
        startingPoint = new Point2D.Double(mouseWorld.getX(), mouseWorld.getY());

        // Do something depending on the selected tool.
        switch (selectedTool) {
            // For every shape, just create a new shape.
            case LINE:
            case SQUARE:
            case RECTANGLE:
            case CIRCLE:
            case ELLIPSE:
                currentShapeIndex = Model.getModel().makeNewShape(selectedTool, mouseWorld, currentColor);
                break;
            case SELECT:
                // If a shape is currently selected, test if a handle is being selected.
                // If a handle is selected, set rotateCurrentShape to true
                // so the shape will rotate instead of move when dragged.
                // This takes priority over shape selection.
                if (shapeIsSelected()) {
                    if (rotateCurrentShape =
                            Model.getModel().getShape(currentShapeIndex).pointInHandle(
                            new Point2D.Double(mouseWorld.getX(), mouseWorld.getY())
                    )) {

                        // Store starting angle in object coordinates. Used for rotating shapes.
                        startingAngle = Model.getModel().findAngleBetweenMouseAndShape(
                                (int) mouseWorld.getX(), (int) mouseWorld.getY(), currentShapeIndex);

                        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO,
                                "Point in handle of " + Model.getModel().getShape(currentShapeIndex));

                        // Return without doing the selection test for shapes.
                        return;
                    }
                }

                // Test for shape selection.
                // Model.selectShape will return the index of the selected shape,
                // or a negative number if the selected point was not inside any shape.
                int prevShape = currentShapeIndex;
                currentShapeIndex = Model.getModel().selectShape(
                        (int) mouseWorld.getX(), (int) mouseWorld.getY());

                // Redraw the screen to update the highlights (if the selected shape changed)
                if (prevShape != currentShapeIndex) {
                    Model.getModel().redraw();
                }

                if (shapeIsSelected()) {
                    // Change the current color indicator to the selected shape's color.
                    currentColor = Model.getModel().getShape(currentShapeIndex).getColor();
                    GUIFunctions.changeSelectedColor(currentColor);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point2D.Double mouseWorld = viewToWorld(e.getX(), e.getY());

        // Do something depending on the selected tool.
        switch (selectedTool) {
            case LINE:
            case SQUARE:
            case RECTANGLE:
            case CIRCLE:
            case ELLIPSE:
                int shape = currentShapeIndex;
                currentShapeIndex = NO_SHAPE_SELECTED;
                Model.getModel().modifyShape(shape, selectedTool, mouseWorld, startingPoint);
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
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Do nothing if no shape is selected.
        if (!shapeIsSelected()) {
            return;
        }

        Point2D.Double mouseWorld = viewToWorld(e.getX(), e.getY());
        switch (selectedTool) {
            case LINE:
            case SQUARE:
            case RECTANGLE:
            case CIRCLE:
            case ELLIPSE:
            case TRIANGLE:
                Model.getModel().modifyShape(currentShapeIndex, selectedTool, mouseWorld, startingPoint);
                break;
            case SELECT:
                // Find out whether to rotate or move the shape.
                if (rotateCurrentShape) {
                    rotateShape((int) mouseWorld.getX(), (int) mouseWorld.getY());
                } else {
                    moveShape((int) mouseWorld.getX(), (int) mouseWorld.getY());
                }
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
     * Transform screen coordinates to world coordinates:
     * (1) reverse scale (1/zoom),
     * (2) translate (origin or world to origin of viewport)
     *
     * @param x Screen x coordinate
     * @param y Screen y coordinate
     * @return The world coordinates of the screen (x,y) position
     */
    private Point2D.Double viewToWorld(int x, int y) {
        Point2D.Double worldPoint = new Point2D.Double(x, y);

        // Inverse scale
        worldPoint.x /= currentZoom;
        worldPoint.y /= currentZoom;

        // Translate
        worldPoint.x += viewportOrigin.x;
        worldPoint.y += viewportOrigin.y;

        return worldPoint;
    }

    /**
     * Rotate the current shape
     *
     * @param mouseX Current mouse world x position
     * @param mouseY Current mouse world y position
     */
    private void rotateShape(int mouseX, int mouseY) {
        // Ask the model to rotate the shape.
        // It will return the angle between the current mouse point and the shape's x axis.
        // Update the starting angle so the next mouse drag is compared to the newest mouse point.
        startingAngle = Model.getModel().rotateShape(
                currentShapeIndex,
                startingAngle,
                mouseX,
                mouseY
        );
    }

    /**
     * Ask the model to move the current shape.
     *
     * @param mouseX Current mouse world x position
     * @param mouseY Current mouse world y position
     */
    private void moveShape(int mouseX, int mouseY) {
        // Move the selected shape.
        Model.getModel().moveShape(currentShapeIndex,
                startingPoint,
                mouseX - (int) startingPoint.x,
                mouseY - (int) startingPoint.y);

        // Update the starting mouse coordinates so they next time the mouse
        // is dragged, the change in x and y will be relative to this mouse
        // move and not the initial press.
        startingPoint.x = mouseX;
        startingPoint.y = mouseY;
    }

    /**
     * @return True if any shape is selected. False otherwise.
     */
    private boolean shapeIsSelected() {
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

    /**
     * Change the scrollbar size and position to match the zoom level
     */
    private void setScrollbars(double newZoom, double oldZoom) {
        // Set scrollbar size
        int scrollbarSize = (int) (VIEW_SIZE / newZoom);
        GUIFunctions.setHScrollBarKnob(scrollbarSize);
        GUIFunctions.setVScrollBarKnob(scrollbarSize);

        // Scrollbar position is:
        //   Center of screen - scrollbar size / 2
        //      but limited to between 0 and (2048 - scrollbar size)
        double prevScrollbarSize = VIEW_SIZE / oldZoom;
        int screenCenterX = (int)(viewportOrigin.x + prevScrollbarSize / 2);
        int screenCenterY = (int)(viewportOrigin.y + prevScrollbarSize / 2);

        // Values I only need to calculate once but use multiple times
        int halfScrollbarSize = scrollbarSize / 2;
        int maxScrollbarPos = CANVAS_SIZE - scrollbarSize;

        // Find horizontal scrollbar position
        int positionX = screenCenterX - halfScrollbarSize;
        if (positionX < 0) {
            positionX = 0;
        } else if (positionX > (maxScrollbarPos)) {
            positionX = maxScrollbarPos;
        }

        // Find vertical scrollbar position
        int positionY = screenCenterY - halfScrollbarSize;
        if (positionY < 0) {
            positionY = 0;
        } else if (positionY > (maxScrollbarPos)) {
            positionY = maxScrollbarPos;
        }

        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO,
                "scrollbar positions (x,y) = " + positionX + ", " + positionY);

        // Set positions of scrollbars
        GUIFunctions.setHScrollBarPosit(positionX);
        GUIFunctions.setVScrollBarPosit(positionY);
    }

    /**
     * @return Radius of the rotation handle, scaled to the current zoom factor.
     */
    public double getHandleRadius() {
        return View.HANDLE_RADIUS / currentZoom;
    }

    // ***********************************************************************
    // Getters/setters
    // ***********************************************************************

    public double getCurrentZoom() {
        return currentZoom;
    }

    public Point2D.Double getViewportOrigin() {
        return viewportOrigin;
    }

    public CS355Scene getScene() {
        return scene;
    }

    public boolean getModel3DIsOn() {
        return model3DIsOn;
    }
}
