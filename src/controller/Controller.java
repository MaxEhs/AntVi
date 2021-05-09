package controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import algorithms.TwoPheromoneExample;
import grid.Grid;
import grid.GridNode;
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

	private static final int DEFAULT_GRID_CELL_COUNT = 30;
	private static final int RENDER_BUFFERS = 2;
	private static final int MAXIMUM_FRAMES_PER_SECOND = 60;

	private View view;
	private Grid grid;
	private Model model;
	private AStarPathfinding pathfinding;

	private boolean running;
	private boolean modelRunning;
	private Thread thread;

	private KeyManager keyManager;
	private MouseManager mouseManager;

	public Controller(int width, int height) {
		grid = new Grid(this, DEFAULT_GRID_CELL_COUNT, height);
		model = new TwoPheromoneExample(this, grid);
		// Initiate Grid and Model before the View, or the event system breaks
		view = new View(this, width, height);
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

		// Listens to Events triggered in the SettingsWindow
		view.getSettingsWindow().addChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent e) {
				switch (e.getPropertyName()) {
				case "CellCountChanged":
					grid.setCellCount((int) e.getNewValue());
					break;
				case "PheromoneStrengthChanged":
					model.setPheromoneStrength((double) e.getNewValue());
					break;
				case "PheromoneEvaporationChanged":
					model.setEvaporationSpeed((double) e.getNewValue());
					break;
				case "PheromoneFallOffChanged":
					model.setPheromoneFallOff((double) e.getNewValue());
					break;
				case "RandomMoveChanceChanged":
					model.setRandomTurnChance((double) e.getNewValue());
					break;
				case "MaximumSaturationChanged":
					GridNode.setMaxPheromone((int) e.getNewValue());
					break;
				case "AntCountChanged":
					model.setAntCount((int) e.getNewValue());
					break;
				case "ModelRunningChanged":
					setModelRunning((boolean) e.getNewValue());
					break;
				case "ResetModel":
					// Reset A* paths
					pathfinding.findAllNeighbours();

					// Reset Simulation
					setModelRunning(false);
					model.setFoodGathered(0);
					model.setModelTicks(0);

					// Reset Ants
					model.setAntCount(0);

					// Reset pheromones
					for (int x = 0; x < grid.getCellCount(); x++) {
						for (int y = 0; y < grid.getCellCount(); y++) {
							// Iterating through all GridNodes and decreasing all pheromones to 0
							GridNode tempNode = grid.getNode(x, y);
							for (int i = 0; i < tempNode.getPheromones().length; i++) {
								tempNode.decreasePheromoneBy(i, Integer.MAX_VALUE);
							}
						}
					}
					break;
				case "ClearGrid":
					grid.setCellCount(grid.getCellCount());
					break;
				case "ModelSpeedChanged":
					model.setModelSpeed((int) e.getNewValue());
					break;
				case "ShowShortestPaths":
					if ((boolean) e.getNewValue()) {
						// Reset previous paths
						pathfinding.findAllNeighbours();

						// For each Nest find the shortest path(s) to all FoodSources
						for (Point nestPos : getGrid().getNestPositions()) {
							for (Point foodPos : getGrid().getFoodPositions()) {

								int startX = foodPos.x;
								int startY = foodPos.y;

								int targetX = nestPos.x;
								int targetY = nestPos.y;
								pathfinding.findPath(startX, startY, targetX, targetY);
							}
						}
					} else {
						// Reset paths
						pathfinding.findAllNeighbours();
					}
					break;
				case "UsingDissipationChanged":
					model.setUsingDissipation((boolean) e.getNewValue());
					break;
				default:
					break;
				}
			}
		});
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

			// Every one second check if the performance is still good
			if (timer >= 1_000_000_000) {
				if (ticks < MAXIMUM_FRAMES_PER_SECOND - 5) {
					System.out.println("There might be a perfomance problem.");
					System.out.println("Current frames per second: " + ticks);
				}
				timer = 0;
			}

			// Update the model <modelSpeed> ticks per second
			if (modelRunning && (modelTimer >= 1_000_000_000 / model.getModelSpeed())) {
				// Update the model
				model.tick();
				modelTimer = 0;
			}

			if (delta >= 1) {
				// Update the Grid
				keyManager.tick();
				grid.tick();

				// Render everything
				render();
				ticks++;
				delta--;
			}
		}

		stop();
	}

	/**
	 * Used for rendering the Grid and Model each tick
	 */
	private void render() {

		// Use buffered Graphics to avoid any visual stuttering or artifacts
		BufferStrategy bufferStrategy = view.getDisplayWindow().getCanvas().getBufferStrategy();
		if (bufferStrategy == null) {
			view.getDisplayWindow().getCanvas().createBufferStrategy(RENDER_BUFFERS);
			return;
		}
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

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
	 * Used for starting the application
	 */
	public synchronized void start() {
		if (running) {
			return;
		}
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Used for stopping the application
	 */
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

	public synchronized void setModelRunning(boolean running) {
		modelRunning = running;
	}

	public Grid getGrid() {
		return grid;
	}

	public KeyManager getKeyManager() {
		return keyManager;
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

}
