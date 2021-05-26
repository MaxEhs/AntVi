package model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import controller.Controller;
import grid.Grid;

/**
 * The AntVi Model class - It contains the basic structure of a class that
 * implements an ACO-Algortihm - All algorithm-classes must extend this class.
 * 
 * @author Max Ehringhausen
 *
 */
public abstract class Model {

	private Controller controller;
	private int modelSpeed = 30;
	private int modelTicks;
	private List<PropertyChangeListener> listeners = new ArrayList<>();
	private Grid grid;
	private Queue<Ant> ants = new LinkedList<>();
	private int antCount;
	private double pheromoneStrength;
	private double evaporationSpeed;
	private double pheromoneFallOff;
	private double randomMoveChance;
	private boolean usingFallOff;
	private boolean usingDissipation;
	private int foodGathered;

	protected Model(Controller controller, Grid grid) {
		this.controller = controller;
		this.grid = grid;

		// DEFAULT VALUES
		setAntCount(0);
		setPheromoneStrength(8.0);
		setEvaporationSpeed(0.95);
		setRandomMoveChance(0.01);
		setPheromoneFallOff(65.0);
	}

	/**
	 * This Method is called by the Controller every tick depending on the
	 * modelSpeed - It updates the model state.
	 */
	public synchronized void tick() {
		synchronized (ants) {
			synchronized (grid) {
				// Removing ants that are outside of the Grid (after scaling the Grid)
				int temp = ants.size();
				Queue<Ant> tempQueue = new LinkedList<>();
				while (!ants.isEmpty()) {
					Ant tempAnt = ants.poll();
					if (grid.getNode(tempAnt.getPosition()) != null) {
						tempQueue.add(tempAnt);
					}
				}

				if (temp != tempQueue.size()) {
					notifyListeners(this, "ModelChangedAntCount", null, tempQueue.size());
				}

				ants = tempQueue;

				// Updating the Model
				generateSolutions();
				daemonActions();
				pheromoneUpdate();
				setModelTicks(modelTicks + 1);
			}
		}
	}

	/**
	 * This method is called by the Controller every frame and renders all ants.
	 * 
	 * @param g The Graphics2D object of the DisplayWindow Canvas
	 */
	public synchronized void render(Graphics2D g) {
		synchronized (ants) {
			for (Ant ant : ants) {
				ant.render(g);
			}
		}
	}

	/**
	 * This method should be used by derived classes to find a move target for all
	 * ants and move them.
	 */
	public abstract void generateSolutions();

	/**
	 * This method should be used by derived classes to make all ants deposit
	 * pheromones and handle the state of each ant.
	 */
	public abstract void daemonActions();

	/**
	 * This method should be used by derived classes to globally update pheromones.
	 */
	public abstract void pheromoneUpdate();

	/**
	 * Gets the tick speed of the model as ticks per second.
	 * 
	 * @return the tick speed of the model
	 */
	public int getModelSpeed() {
		return modelSpeed;
	}

	/**
	 * Sets the tick speed of the model as ticks per second.
	 * 
	 * @param modelSpeed the new tick speed of the model
	 */
	public final void setModelSpeed(int modelSpeed) {
		this.modelSpeed = modelSpeed;
	}

	/**
	 * Gets the amount of ticks the model has been running for since the last reset.
	 * 
	 * @return the amount of ticks
	 */
	public int getModelTicks() {
		return modelTicks;
	}

	/**
	 * Updates the amount of ticks the model has been running for.
	 * 
	 * @param modelTicks the amount of ticks
	 */
	public void setModelTicks(int modelTicks) {

		notifyListeners(this, "ModelTicks", this.modelTicks, modelTicks);
		this.modelTicks = modelTicks;
	}

	/**
	 * This method is part of the event system used to notify the GUI about changes
	 * inside the model - It raises a new event that listeners can react to.
	 * 
	 * @param object   the object the notification is coming from, usually 'this'
	 * @param property the name of the property that has changed
	 * @param oldValue the old value of the property
	 * @param newValue the new value of the property
	 */
	private void notifyListeners(Object object, String property, Object oldValue, Object newValue) {
		for (PropertyChangeListener pcl : listeners) {
			pcl.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
		}
	}

	/**
	 * This method is part of the event system used to notify the GUI about changes
	 * inside the model - it adds a new listener to the list of listeners that are
	 * notified.
	 * 
	 * @param listener The PropertyChangeListener that should be notified in case a
	 *                 property changes in the Model
	 */
	public void addChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Gets the current pheromone strength of all ants in the model.
	 * 
	 * @return the pheromone strength
	 */
	public double getPheromoneStrength() {
		return pheromoneStrength;
	}

	/**
	 * Sets the pheromone strength of all ants in the model.
	 * 
	 * @param value the new pheromone strength
	 */
	public final void setPheromoneStrength(double value) {
		pheromoneStrength = value;
	}

	/**
	 * Gets the current global evaporation speed of pheromones.
	 * 
	 * @return the evaporation speed
	 */
	public double getEvaporationSpeed() {
		return evaporationSpeed;
	}

	/**
	 * Sets the global evaporation speed of pheromones.
	 * 
	 * @param evaporationSpeed the new evaporation speed
	 */
	public final void setEvaporationSpeed(double evaporationSpeed) {
		this.evaporationSpeed = evaporationSpeed;
	}

	/**
	 * Gets the chance of an ant to move randomly.
	 * 
	 * @return the chance as Double, where 1.0 is a 100% random movement chance
	 */
	public double getRandomMoveChance() {
		return randomMoveChance;
	}

	/**
	 * Sets the chance of an ant to move randomly.
	 * 
	 * @param randomMoveChance the chance as Double, where 1.0 is a 100% random
	 *                         movement chance
	 */
	public final void setRandomMoveChance(double randomMoveChance) {
		this.randomMoveChance = randomMoveChance;
	}

	/**
	 * Gets the amount of pheromone falloff.
	 * 
	 * @return the pheromone falloff
	 */
	public double getPheromoneFallOff() {
		return pheromoneFallOff;
	}

	/**
	 * Sets the amount of pheromone falloff - This mechanic is disabled if the
	 * falloff amount is smaller than 1.
	 * 
	 * @param pheromoneFallOff the new amount of pheromone falloff
	 */
	public final void setPheromoneFallOff(double pheromoneFallOff) {
		if (pheromoneFallOff < 1) {
			usingFallOff = false;
		} else {
			usingFallOff = true;
			this.pheromoneFallOff = 100 - pheromoneFallOff;
		}
	}

	/**
	 * Whether the pheromone falloff mechanic is used.
	 * 
	 * @return true if used, false otherwise
	 */
	public boolean isUsingFallOff() {
		return usingFallOff;
	}

	/**
	 * Whether the pheromone dissipation mechanic is used.
	 * 
	 * @return true if used, false otherwise
	 */
	public boolean isUsingDissipation() {
		return usingDissipation;
	}

	/**
	 * Sets whether the pheromone dissipation mechanic should be used.
	 * 
	 * @param usingDissipation true if used, false otherwise
	 */
	public final void setUsingDissipation(boolean usingDissipation) {
		this.usingDissipation = usingDissipation;
	}

	/**
	 * Gets the List of ants that are currently active in the model.
	 * 
	 * @return the List of ants
	 */
	public List<Ant> getAnts() {
		return new ArrayList<>(ants);
	}

	/**
	 * Gets the sum of all ants that are currently active in the model.
	 * 
	 * @return the sum of all ants
	 */
	public synchronized int getAntCount() {
		return antCount;
	}

	/**
	 * Sets the amount of ants that are currently active in the model.
	 * 
	 * @param antCount the new amount of ants
	 */
	public final synchronized void setAntCount(int antCount) {
		synchronized (ants) {
			this.antCount = antCount;

			int difference = ants.size() - antCount;

			if (difference < 0) {
				// Ants need to be added
				while (difference < 0) {
					ants.add(new Ant(this, new Point(grid.getNestPositions().get(0))));
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

	/**
	 * Gets the global Grid instance.
	 * 
	 * @return the Grid
	 */
	public Grid getGrid() {
		return grid;
	}

	/**
	 * Gets the global Controller.
	 * 
	 * @return the Controller
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * Gets the amount of food the ants have gathered until the last reset.
	 * 
	 * @return amount of food the ants have gathered
	 */
	public int getFoodGathered() {
		return foodGathered;
	}

	/**
	 * Increases the amount of food the ants have gathered until the last reset by
	 * one.
	 */
	public final void increaseFoodGathered() {
		notifyListeners(this, "FoodGathered", foodGathered, foodGathered + 1);
		foodGathered++;
	}

	/**
	 * Sets the amount of food the ants have gathered.
	 * 
	 * @param value amount of food the ants have gathered
	 */
	public final void setFoodGathered(int value) {
		notifyListeners(this, "FoodGathered", foodGathered, value);
		foodGathered = value;
	}
}
