package grid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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
	private ArrayList<Point> nestPositions;
	private ArrayList<Point> foodPositions;
	private GridNode[][] nodes;
	private Point clickedPoint;
	private Point releasedPoint;
	private Rectangle selection;

	/**
	 * Creates a grid of a certain size and with a certain amount of cells.
	 * 
	 * @param cellCount the amount of columns or rows in the Grid
	 * @param size      the overall width in pixels. A Grid is always rectangular
	 */
	public Grid(Controller controller, int cellCount, int size) {

		this.controller = controller;
		nodes = new GridNode[cellCount][cellCount];
		nestPositions = new ArrayList<>();
		foodPositions = new ArrayList<>();
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
			nestPositions = new ArrayList<>();
			foodPositions = new ArrayList<>();

			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {

					// Filling the Grid with empty Tiles
					nodes[x][y] = new Tile(this, x, y, cellSize, offset, false);
				}
			}

			// Placing first Nest at (0/0)
			nodes[0][0] = new Nest(this, 0, 0, cellSize, offset, false);
			nestPositions.add(nodes[0][0].getGridPosition());
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
	 * @param g the AWT Graphics2D object to be used for rendering
	 */
	public synchronized void render(Graphics2D g) {
		// Draw all GridNodes
		synchronized (nodes) {
			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {
					nodes[x][y].render(g);
				}
			}
		}

		// Draw the selection rectangle
		if (selection != null) {
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(2.0f));
			g.draw(selection);
		}
	}

	public synchronized void onMouseMove(MouseEvent e) {
		// Update GridNodes based on mouse position
		synchronized (nodes) {
			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {
					nodes[x][y].onMouseMove(e);
				}
			}
		}
	}

	public synchronized void onMousePressed(MouseEvent e) {
		// Store mouse click position
		clickedPoint = new Point(e.getPoint());
	}

	public synchronized void onMouseDragged(MouseEvent e) {
		// Mouse dragged, build a selection rectangle
		if (clickedPoint != null) {
			releasedPoint = new Point(e.getPoint());

			if (clickedPoint.distance(releasedPoint) > 4) {
				selection = new Rectangle(clickedPoint);
				selection.add(releasedPoint);
			}
		}
	}

	public synchronized void onMouseRelease(MouseEvent e) {

		synchronized (nodes) {
			for (int x = 0; x < cellCount; x++) {
				for (int y = 0; y < cellCount; y++) {
					nodes[x][y].onMouseMove(e);
					if (selection != null && selection.intersects(nodes[x][y].bounds) && nodes[x][y] instanceof Tile) {
						// If there is a selection box, invert all Tiles inside it
						nodes[x][y].setBlocking(!nodes[x][y].isBlocking());
					} else {
						// If there is no selection box, a GridNode was clicked
						nodes[x][y].onMouseRelease(e);
					}
				}
			}
		}
		// Reset selection
		clickedPoint = null;
		releasedPoint = null;
		selection = null;
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

	/**
	 * Getter for the two-dimensional Array of GridNodes that make up the Grid.
	 * 
	 * @return a two-dimensional Array containing all GridNodes that are currently
	 *         part of the Grid
	 */
	public GridNode[][] getNodes() {
		return nodes;
	}

	public Controller getController() {
		return controller;
	}

	/**
	 * Getter for the offset in pixels that should be applied to the X and Y of
	 * everything that is rendered on the Grid. This is used for centering all
	 * content.
	 * 
	 * @return offset in pixels
	 */
	public int getOffset() {
		return offset;
	}

	public List<Point> getNestPositions() {
		return nestPositions;
	}

	public List<Point> getFoodPositions() {
		return foodPositions;
	}
}
