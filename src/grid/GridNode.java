package grid;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * The AntVi GridNode class. Every type of Node extends this class. It contains Grid
 * logic, A*-pathfinding elements, as well as methods and fields needed by ACO.
 * 
 * @author Max Ehringhausen
 *
 */
public abstract class GridNode {

	// Used for ACO
	/*
	 * This array is initialized with a size of 8, meaning there can be 8 different
	 * "kinds" of pheromones based on the index.
	 */
	private double[] pheromoneAmount;
	public static final double MAX_PHEROMONE = 100.0D;

	// Used for A* path finding
	private GridNode previousNode;
	private List<GridNode> nearbyNodes;
	private int gCost;
	private int fCost;
	private int hCost;
	protected boolean blocking;

	// Used for interactions & rendering
	private Point gridPosition;
	protected Grid grid;
	protected Rectangle bounds;
	protected boolean hovering;

	/**
	 * Creates a GridNode object.
	 * 
	 * @param grid     the Grid this GridNode is part of
	 * @param x        the x-coordinate in the Grid
	 * @param y        the y-coordinate in the Grid
	 * @param cellSize the cellSize in pixels based on the Grid
	 * @param offset   the offset in absolute pixels
	 */
	protected GridNode(Grid grid, int x, int y, int cellSize, int offset) {
		this.grid = grid;
		bounds = new Rectangle((x * cellSize) + offset, (y * cellSize) + offset, cellSize, cellSize);
		gridPosition = new Point(x, y);
		blocking = false;
		pheromoneAmount = new double[8];
	}

	// Abstract methods
	public abstract void tick();

	public abstract void render(Graphics g);

	public abstract void onClick();

	// On mouse move
	public void onMouseMove(MouseEvent e) {
		if (bounds.contains(e.getX(), e.getY())) {
			hovering = true;
		} else {
			hovering = false;
		}
	}

	// On mouse button release
	public void onMouseRelease(MouseEvent e) {
		if (hovering) {
			onClick();
		}
	}

	public void calculateFCost() {
		fCost = gCost + hCost;
	}

	public int compareTo(Object obj) {
		GridNode node = (GridNode) obj;
		return Integer.compare(fCost, node.fCost);
	}

	/**
	 * Sets the bounds for a given GridNode. Bounds are used for rendering and
	 * interactivity.
	 * 
	 * @param x    the new absolute x-coordinate in pixels
	 * @param y    the new absolute y-coordinate in pixels
	 * @param size the new size (width) in pixels
	 */
	public void setBounds(int x, int y, int size) {
		bounds.x = x;
		bounds.y = y;
		bounds.width = size;
		bounds.height = size;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public GridNode getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(GridNode previousNode) {
		this.previousNode = previousNode;
	}

	public List<GridNode> getNearbyNodes() {
		return nearbyNodes;
	}

	public void setNearbyNodes(List<GridNode> nearbyNodes) {
		this.nearbyNodes = nearbyNodes;
	}

	public int getGCost() {
		return gCost;
	}

	public void setGCost(int gCost) {
		this.gCost = gCost;
	}

	public int getFCost() {
		return fCost;
	}

	public void setFCost(int fCost) {
		this.fCost = fCost;
	}

	public int getHCost() {
		return hCost;
	}

	public void setHCost(int hCost) {
		this.hCost = hCost;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	/**
	 * Getter for the x-coordinate in the Grid
	 * 
	 * @return the x-coordinate in the Grid
	 */
	public int getX() {
		return gridPosition.x;
	}

	/**
	 * Getter for the y-coordinate in the Grid
	 * 
	 * @return the y-coordinate in the Grid
	 */
	public int getY() {
		return gridPosition.y;
	}

	public Point getGridPosition() {
		return gridPosition;
	}

	public boolean isHovering() {
		return hovering;
	}

	/**
	 * Gets the pheromone concentration of a specific pheromone (specified by index)
	 * on this GridNode.
	 * 
	 * @param index the pheromone type index (0 to 7)
	 * @return the amount of pheromone of the given type that is currently on this
	 *         GridNode
	 */
	public double getPheromoneAmount(int index) {
		return pheromoneAmount[index];
	}

	public double[] getPheromones() {
		return pheromoneAmount;
	}

	/**
	 * Gets the index of the pheromone with the highest concentration.
	 * 
	 * @return the index of the pheromone with the highest concentration.
	 */
	public int getPheromoneWithHighestValue() {
		double highest = pheromoneAmount[0];
		int index = 0;
		for (int i = 0; i < pheromoneAmount.length; i++) {
			if (pheromoneAmount[i] > highest) {
				highest = pheromoneAmount[i];
				index = i;
			}
		}
		return index;
	}

	/**
	 * Increases the pheromone concentration of a specific pheromone (specified by
	 * index) of this GridNode by a given amount.
	 * 
	 * @param index  the pheromone to change (0 to 7)
	 * @param amount the amount that should be added to the pheromone concentration
	 *               of the GridNode
	 */
	public void increasePheromoneBy(int index, double amount) {

		pheromoneAmount[index] += amount;

		if (pheromoneAmount[index] > MAX_PHEROMONE) {
			pheromoneAmount[index] = MAX_PHEROMONE;
		}
	}

	/**
	 * Decreases the pheromone concentration of a specific pheromone (specified by
	 * index) of this GridNode by a given amount.
	 * 
	 * @param index  the pheromone to change (0 to 7)
	 * @param amount the amount that should be subtracted from the pheromone
	 *               concentration of the GridNode
	 */
	public void decreasePheromoneBy(int index, double amount) {

		pheromoneAmount[index] -= amount;

		if (pheromoneAmount[index] < 0) {
			pheromoneAmount[index] = 0;
		}
	}

}
