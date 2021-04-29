package grid;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import controller.Controller;

/**
 * The AntVi Grid class. It manages all GridNodes and has corresponding methods.
 * 
 * @author Max Ehringhausen
 *
 */
public class Grid {

	private Controller controller;
	private int offset;
	private int size;
	private int cellCount;
	private int cellSize;
	private int initialCellSize;
	private Point nestPos = new Point(0, 0);
	private GridNode[][] nodes;

	/**
	 * Creates a grid of a certain size and with a certain amount of cells.
	 * 
	 * @param cellCount the amount of columns or rows in the Grid
	 * @param size      the overall width in pixels. A Grid is always rectangular
	 */
	public Grid(Controller controller, int cellCount, int size) {

		this.controller = controller;
		nodes = new GridNode[cellCount][cellCount];
		this.size = size;
		this.cellCount = cellCount;
		cellSize = size / cellCount;
		initialCellSize = cellSize;

		calculateOffset();
		initialize();
	}

	/**
	 * Populates the Grid with Tiles.
	 */
	private synchronized void initialize() {
		synchronized (nodes) {
			nodes = new GridNode[cellCount][cellCount];

			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {

					// Filling the Grid with empty Tiles
					nodes[x][y] = new Tile(this, x, y, cellSize, offset);
				}
			}

			// Placing Nest at (0/0)
			nodes[0][0] = new Nest(this, 0, 0, cellSize, offset);
			nestPos.setLocation(0, 0);
		}
	}

	/**
	 * Iterates through all GridNodes and updates them.
	 */
	public synchronized void tick() {
		synchronized (nodes) {
			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {
					nodes[x][y].tick();
				}
			}
		}
	}

	/**
	 * Iterates through all GridNodes and renders them.
	 * 
	 * @param g the AWT Graphics object to be used for rendering
	 */
	public synchronized void render(Graphics2D g) {
		synchronized (nodes) {
			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {
					nodes[x][y].render(g);
				}
			}
		}
	}

	public synchronized void onMouseMove(MouseEvent e) {
		synchronized (nodes) {
			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {
					nodes[x][y].onMouseMove(e);
				}
			}
		}
	}

	public synchronized void onMouseRelease(MouseEvent e) {
		synchronized (nodes) {
			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {
					nodes[x][y].onMouseRelease(e);
				}
			}
		}
	}

	private void calculateOffset() {
		// Calculating the Grid offset so it can be displayed centered
		offset = (size - (cellSize * cellCount));
		offset = (offset / 2);
	}

	/**
	 * Used to change the cell count of the Grid. Also re-initializes the changed
	 * Grid.
	 * 
	 * @param cellCount the amount of cells that the Grid should have
	 */
	public synchronized void setCellCount(int cellCount) {
		synchronized (nodes) {
			this.cellCount = cellCount;
			cellSize = size / cellCount;
			calculateOffset();
			initialize();
			controller.getPathfinding().findAllNeighbours();
			controller.getView().getSettingsWindow().getNestPositionLabel().setText("0 / 0");
		}
	}

	public synchronized int getCellCount() {
		return cellCount;
	}

	public int getCellSize() {
		return cellSize;
	}

	public int getInitialCellSize() {
		return initialCellSize;
	}

	/**
	 * Sets the node at a set of given coordinates in the Grid.
	 * 
	 * @param x    the x coordinate in the Grid
	 * @param y    the y coordinate in the Grid
	 * @param node the GridNode object to place in the Grid
	 */
	public synchronized void setNode(int x, int y, GridNode node) {
		synchronized (nodes) {
			nodes[x][y] = node;
			controller.getPathfinding().findAllNeighbours();
		}
	}

	/**
	 * Gets the GridNode from the specified coordinates on the Grid.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the GridNode from the specified coordinates, otherwise null if
	 *         coordinates are out of bounds.
	 */
	public synchronized GridNode getNode(int x, int y) {
		synchronized (nodes) {
			try {
				return nodes[x][y];
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
	}

	/**
	 * Gets the GridNode from the specified Point on the Grid.
	 * 
	 * @param point the Point with x and y coordinates
	 * @return the GridNode from the specified Point, otherwise null if the Point is
	 *         out of bounds.
	 */
	public synchronized GridNode getNode(Point point) {
		synchronized (nodes) {
			try {
				return nodes[point.x][point.y];
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
	}

	public GridNode[][] getNodes() {
		return nodes;
	}

	public Controller getController() {
		return controller;
	}

	public int getSize() {
		return size;
	}

	public int getOffset() {
		return offset;
	}

	public Point getNestPos() {
		return nestPos;
	}

	public void setNestPos(int x, int y) {
		nestPos.setLocation(x, y);
		controller.getView().getSettingsWindow().getNestPositionLabel().setText(String.format("%s / %s", x, y));
		controller.getPathfinding().findAllNeighbours();
	}

}
