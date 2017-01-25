package cs355.view;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.*;
import cs355.model.drawing.Rectangle;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Marshall on 1/24/2017.
 * Main viewer class - draws objects on the screen
 */
public class View implements ViewRefresher, Observer {

    /**
     * Default constructor
     */
    public View() {
        Model.getModel().addObserver(this);
    }

    // TODO: finish
    @Override
    public void refreshView(Graphics2D g2d) {
        // Shell builds and passes in Graphics2D object
        // Draw on this object
        // Shell refreshes the drawing area
//        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "Refresh viewer");
        for (cs355.model.drawing.Shape s : Model.getModel().getShapes()) {
            g2d.setColor(s.getColor());
            if (s instanceof Line) {
                Line l = (Line) s;
                g2d.drawLine((int)l.getStart().x, (int)l.getStart().y, (int)l.getEnd().x, (int)l.getEnd().y);
            }
            else if (s instanceof Square) {
                Square square = (Square) s;
                g2d.drawRect(
                        (int)square.getUpperLeft().x, // Upper left x
                        (int)square.getUpperLeft().y, // Upper left y
                        (int)square.getSize(), // width
                        (int)square.getSize() // height
                );
            }
            else if (s instanceof Rectangle) {
                Rectangle rect = (Rectangle) s;
                g2d.drawRect(
                        (int) rect.getUpperLeft().x, // Upper left x
                        (int)rect.getUpperLeft().y, // Upper left y
                        (int)rect.getWidth(), // width
                        (int)rect.getHeight() // height
                );
            }
        }
    }

    // TODO: finish
    @Override
    public void update(Observable o, Object arg) {
//        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "Update viewer");
        GUIFunctions.refresh();
    }
}
