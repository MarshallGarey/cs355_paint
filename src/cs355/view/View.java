package cs355.view;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.*;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "Refresh viewer");
        for (cs355.model.drawing.Shape s : Model.getModel().getShapes()) {
            if (s instanceof Line) {
                Line l = (Line) s;
                g2d.drawLine((int)l.getStart().x, (int)l.getStart().y, (int)l.getEnd().x, (int)l.getEnd().y);

            }
        }
    }

    // TODO: finish
    @Override
    public void update(Observable o, Object arg) {
        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "Update viewer");
        GUIFunctions.refresh();
    }
}
