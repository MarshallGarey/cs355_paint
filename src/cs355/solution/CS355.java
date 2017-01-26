package cs355.solution;

import cs355.GUIFunctions;
import cs355.controller.PaintController;
import cs355.view.View;

import java.awt.*;

/**
 * This is the main class. The program starts here.
 * Make you add code below to initialize your model,
 * view, and controller and give them to the app.
 */
public class CS355 {

	/**
	 * This is where it starts.
	 * @param args = the command line arguments
	 */
	public static void main(String[] args) {

		// Fill in the parameters below with your controller and view.
		PaintController controller = new PaintController();
		View view = new View();
		GUIFunctions.createCS355Frame(controller, view);

		GUIFunctions.refresh();

		// Initialize color
        controller.colorButtonHit(Color.BLUE);
	}
}
