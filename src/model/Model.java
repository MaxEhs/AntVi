package model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import controller.Controller;
import grid.Grid;

/**
 * The AntVi Model class. It contains the basic structure of a class that
 * implements an ACO-Algortihm. All algorithm-classes must extend this class.
 * 
 * @author Max Ehringhausen
 *
 */
public abstract class Model {

	private Controller controller;
	private Grid grid;
	private Queue<Ant> ants = new LinkedList<>();
	private int antCount;
	private double pheromoneStrength;
	private double evaporationSpeed;
	private double pheromoneFallOff;
	private double randomTurnChance;
	private boolean usingFallOff;
	private int foodGathered;

	protected Model(Controller controller, Grid grid) {
		this.controller = controller;
		this.grid = grid;

		// DEFAULT VALUES
		antCount = 0;
		pheromoneStrength = 6.0;
		evaporationSpeed = 0.40;
		randomTurnChance = 0.08;
		setPheromoneFallOff(75.0);
	}

	public synchronized void tick() {
		synchronized (ants) {
			synchronized (grid) {
				// Removing ants that are outside of the Grid
				Queue<Ant> tempQueue = new LinkedList<>();
				while (!ants.isEmpty()) {
					Ant tempAnt = ants.poll();
					if (grid.getNode(tempAnt.getPosition()) != null) {
						tempQueue.add(tempAnt);
					}
				}
				controller.getView().getSettingsWindow().getAntCountInput().setValue(tempQueue.size());
				ants = tempQueue;

				generateSolutions();
				daemonActions();
				pheromoneUpdate();
			}
		}

	}

	public synchronized void render(Graphics2D g) {
		synchronized (ants) {
			for (Ant ant : ants) {
				ant.render(g);
			}
		}
	}

	public abstract void generateSolutions();

	public abstract void daemonActions();

	public abstract void pheromoneUpdate();

	public double getPheromoneStrength() {
		return pheromoneStrength;
	}

	public void setPheromoneStrength(double value) {
		pheromoneStrength = value;
	}

	public double getEvaporationSpeed() {
		return evaporationSpeed;
	}

	public void setEvaporationSpeed(double evaporationSpeed) {
		this.evaporationSpeed = evaporationSpeed;
	}

	public double getRandomMoveChance() {
		return randomTurnChance;
	}

	public void setRandomTurnChance(double randomTurnChance) {
		this.randomTurnChance = randomTurnChance;
	}

	public double getPheromoneFallOff() {
		return pheromoneFallOff;
	}

	public final void setPheromoneFallOff(double pheromoneFallOff) {
		if (pheromoneFallOff < 1) {
			usingFallOff = false;
		} else {
			usingFallOff = true;
			this.pheromoneFallOff = 100 - pheromoneFallOff;
		}
	}

	public boolean isUsingFallOff() {
		return usingFallOff;
	}

	public List<Ant> getAnts() {
		return new ArrayList<>(ants);
	}

	public int getAntCount() {
		return antCount;
	}

	public synchronized void setAntCount(int antCount) {
		synchronized (ants) {
			this.antCount = antCount;

			int difference = ants.size() - antCount;

			if (difference < 0) {
				// Ants need to be added
				while (difference < 0) {
					ants.add(new Ant(this, new Point(grid.getNestPos())));
					difference++;
				}
			} else if (difference > 0) {
				// Remove the oldest Ants first
				while (difference > 0) {
					ants.poll();
					difference--;
				}
			} else {
				// AntCount is the same as before, nothing to do...
			}
		}
	}

	public Grid getGrid() {
		return grid;
	}

	public Controller getController() {
		return controller;
	}

	public int getFoodGathered() {
		return foodGathered;
	}

	public void increaseFoodGathered() {
		foodGathered++;
		controller.getView().getSettingsWindow().getFoodGatheredLabel().setText(String.format("%s", foodGathered));
	}

	public void setFoodGathered(int value) {
		foodGathered = value;
		controller.getView().getSettingsWindow().getFoodGatheredLabel().setText(String.format("%s", foodGathered));
	}
}
