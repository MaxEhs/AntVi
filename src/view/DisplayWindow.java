package view;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * The AntVi DisplayWindow class. It contains the canvas that is used to display
 * the Grid and everything on it.
 * 
 * @author Max Ehringhausen
 *
 */
public class DisplayWindow {

	private JFrame frame;
	private Canvas canvas;

	private String title;
	private int width;
	private int height;

	public DisplayWindow(String title, int width, int height) {

		this.title = title;
		this.width = width;
		this.height = height;

		createDisplayWindow();
	}

	/**
	 * Used for initializing the main display window
	 */
	private void createDisplayWindow() {

		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setIconImage(new ImageIcon("src/ant.png").getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);

		frame.add(canvas);
		frame.pack();

	}

	public Canvas getCanvas() {
		return canvas;
	}

	public JFrame getFrame() {
		return frame;
	}

}
