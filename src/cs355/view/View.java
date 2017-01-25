package cs355.view;

import cs355.GUIFunctions;
import cs355.model.Model;
import cs355.model.drawing.CS355Drawing;

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

    @Override
    public void refreshView(Graphics2D g2d) {

    }

    // TODO: finish
    @Override
    public void update(Observable o, Object arg) {
        GUIFunctions.refresh();
        Logger.getLogger(CS355Drawing.class.getName()).log(Level.INFO, "Update viewer");
    }
}
