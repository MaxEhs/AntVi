package controller;

import java.awt.EventQueue;

/**
 * The AntVi Launcher class is only used for launching the application.
 * 
 * @author Max Ehringhausen
 *
 */
public class Launcher {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Controller(1000, 1000).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
