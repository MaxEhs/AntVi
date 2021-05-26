package view;

import java.awt.Dimension;
import java.awt.Toolkit;

import controller.Controller;

/**
 * The AntVi View class - It manages the two application windows.
 * 
 * @author Max Ehringhausen
 *
 */
public class View {
	private DisplayWindow displayWindow;
	private SettingsWindow settingsWindow;

	public View(Controller controller, int width, int height) {
		// Creating the windows
		settingsWindow = new SettingsWindow(controller, "AntVi - Parameters", 400, 805);
		displayWindow = new DisplayWindow("AntVi - Simulation", width, height);

		// Setting the window positions
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		displayWindow.getFrame().setLocation(d.width / 2 - displayWindow.getFrame().getSize().width / 2,
				d.height / 2 - displayWindow.getFrame().getSize().height / 2);
		settingsWindow.getFrame().setLocation(
				(d.width / 2 - displayWindow.getFrame().getSize().width / 2) - settingsWindow.getFrame().getWidth(),
				d.height / 2 - displayWindow.getFrame().getSize().height / 2);
	}

	public DisplayWindow getDisplayWindow() {
		return displayWindow;
	}

	public SettingsWindow getSettingsWindow() {
		return settingsWindow;
	}

	public int getWidth() {
		return displayWindow.getCanvas().getWidth();
	}

	public int getHeight() {
		return displayWindow.getCanvas().getHeight();
	}

}
