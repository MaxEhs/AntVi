package controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import algorithms.TwoPheromoneExample;
import grid.Grid;
import model.Model;
import utils.AStarPathfinding;
import utils.KeyManager;
import utils.MouseManager;
import view.View;

/**
 * The AntVi Controller class. It manages all other components, as well as the
 * main simulation and update loop.
 * 
 * @author Max Ehringhausen
 *
 */
public class Controller implements Runnable {

	private static final int DEFAULT_GRID_CELL_COUNT = 20;
	private static final int RENDER_BUFFERS = 2;
	private static final int MAXIMUM_FRAMES_PER_SECOND = 30;

	private View view;
	private Grid grid;
	private Model model;
	private AStarPathfinding pathfinding;

	private boolean running;
	private boolean modelRunning;
	private int modelSpeed = 30;
	private int modelTicks = 0;
	private Thread thread;
	private Graphics2D g;

	private KeyManager keyManager;
	private MouseManager mouseManager;

	public Controller(int width, int height) {
		view = new View(this, width, height);
		grid = new Grid(this, DEFAULT_GRID_CELL_COUNT, height);
		model = new TwoPheromoneExample(this, grid);
		keyManager = new KeyManager();
		pathfinding = new AStarPathfinding(grid);
		mouseManager = new MouseManager(grid);
		initializeListeners();
	}

	private void initializeListeners() {
		view.getDisplayWindow().getFrame().addKeyListener(keyManager);
		view.getDisplayWindow().getFrame().addMouseListener(mouseManager);
		view.getDisplayWindow().getCanvas().addMouseListener(mouseManager);
		view.getDisplayWindow().getFrame().addMouseMotionListener(mouseManager);
		view.getDisplayWindow().getCanvas().addMouseMotionListener(mouseManager);
	}

	private void tick() {
		keyManager.tick();
		grid.tick();
	}

	/**
	 * Used for rendering each frame
	 */
	private void render() {

		BufferStrategy bufferStrategy = view.getDisplayWindow().getCanvas().getBufferStrategy();
		if (bufferStrategy == null) {
			view.getDisplayWindow().getCanvas().createBufferStrategy(RENDER_BUFFERS);
			return;
		}
		g = (Graphics2D) bufferStrategy.getDrawGraphics();

		// Clear Screen
		g.clearRect(0, 0, view.getWidth(), view.getHeight());
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, view.getWidth(), view.getHeight());

		// Rendering
		grid.render(g);
		pathfinding.render(g);
		model.render(g);

		bufferStrategy.show();
		g.dispose();
	}

	/**
	 * Logic for running the simulation
	 */
	public void run() {

		// Variables for delta time and metrics
		int fps = MAXIMUM_FRAMES_PER_SECOND;
		double timePerTick = (1_000_000_000 / fps);
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		long modelTimer = 0;
		int ticks = 0;

		// Main simulation loop
		while (running) {
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			modelTimer += now - lastTime;
			lastTime = now;

			if (timer >= 1_000_000_000) {
				if (ticks < MAXIMUM_FRAMES_PER_SECOND - 5) {
					System.out.println("There might be a perfomance problem.");
					System.out.println("Current frames per second: " + ticks);
				}
				timer = 0;
			}

			if (modelRunning && (modelTimer >= 1_000_000_000 / modelSpeed)) {
				model.tick();
				modelTicks++;
				view.getSettingsWindow().getModelTicksLabel().setText(String.format("%s", modelTicks));
				modelTimer = 0;
			}

			if (delta >= 1) {
				tick();
				render();
				ticks++;
				delta--;
			}
		}

		stop();
	}

	/**
	 * Thread-safe way of starting the simulation
	 */
	public synchronized void start() {
		if (running) {
			return;
		}
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void setModelRunning(boolean running) {
		modelRunning = running;
	}

	public synchronized void stop() {
		if (!running) {
			return;
		}
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int getModelTicks() {
		return modelTicks;
	}

	public void setModelTicks(int x) {
		modelTicks = x;
	}

	public Boolean isRunning() {
		return running;
	}

	public Grid getGrid() {
		return grid;
	}

	public KeyManager getKeyManager() {
		return keyManager;
	}

	public MouseManager getMouseManager() {
		return mouseManager;
	}

	public Graphics getGraphics() {
		return g;
	}

	public View getView() {
		return view;
	}

	public AStarPathfinding getPathfinding() {
		return pathfinding;
	}

	public synchronized boolean isModelRunning() {
		return modelRunning;
	}

	public Model getModel() {
		return model;
	}

	public int getModelSpeed() {
		return modelSpeed;
	}

	public void setModelSpeed(int modelSpeed) {
		this.modelSpeed = modelSpeed;
	}

}
