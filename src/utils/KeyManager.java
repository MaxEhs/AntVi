package utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The AntVi KeyManager class. It implements the AWT KeyListener to provide
 * interactivity.
 * 
 * @author Max Ehringhausen
 *
 */
public class KeyManager implements KeyListener {

	// Smallest possible index needed (KeyEvent.VK_N) + 1
	private static final int KEYS_SIZE = 79;

	private boolean[] keys;
	private boolean[] justPressed;
	private boolean[] cantPress;
	private boolean foodKey;
	private boolean nestKey;

	public KeyManager() {
		keys = new boolean[KEYS_SIZE];
		justPressed = new boolean[keys.length];
		cantPress = new boolean[keys.length];
	}

	// Tick
	public void tick() {

		// Handle justPressed logic
		for (int i = 0; i < keys.length; i++) {

			if (cantPress[i] && !keys[i]) {
				cantPress[i] = false;
			} else if (justPressed[i]) {
				cantPress[i] = true;
				justPressed[i] = false;
			}

			if (!cantPress[i] && keys[i]) {
				justPressed[i] = true;
			}
		}

		foodKey = keys[KeyEvent.VK_F];
		nestKey = keys[KeyEvent.VK_N];
	}

	// Key just pressed
	public boolean keyJustPressed(int keyCode) {
		if (keyCode < 0 || keyCode >= keys.length) {
			return false;
		}
		return justPressed[keyCode];
	}

	// Key down
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() < 0 || e.getExtendedKeyCode() >= keys.length) {
			return;
		}
		keys[e.getKeyCode()] = true;

	}

	// Key released
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() < 0 || e.getExtendedKeyCode() >= keys.length) {
			return;
		}
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Currently unused

	}

	public boolean isFoodKey() {
		return foodKey;
	}

	public boolean isNestKey() {
		return nestKey;
	}
}
