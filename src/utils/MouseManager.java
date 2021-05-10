package utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import grid.Grid;

/**
 * The AntVi MouseManager class. It implements the AWT MouseListener and
 * MouseMotionListener to provide interactivity.
 * 
 * @author Max Ehringhausen
 *
 */
public class MouseManager implements MouseListener, MouseMotionListener {

	private boolean leftPressed;
	private boolean rightPressed;
	private Grid grid;

	public MouseManager(Grid grid) {
		this.grid = grid;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftPressed = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			rightPressed = true;
		}

		grid.onMousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftPressed = false;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			rightPressed = false;
		}

		grid.onMouseRelease(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		grid.onMouseMove(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		grid.onMouseDragged(e);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean isLeftPressed() {
		return leftPressed;
	}

	public boolean isRightPressed() {
		return rightPressed;
	}

}