package grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * The AntVi Tile class - One of the three types of GridNodes - Contains logic for
 * interactions, rendering, and pathfinding.
 * 
 * @author Max Ehringhausen
 *
 */
public class Tile extends GridNode {

	/**
	 * Creates a Tile.
	 * 
	 * @param grid     the Grid this Tile is part of
	 * @param x        the x-coordinate in the Grid
	 * @param y        the y-coordinate in the Grid
	 * @param cellSize the cellSize based on the Grid
	 * @param offset   the offset in absolute pixels
	 * @param hovering whether the mouse is hovering over this GridNode
	 */
	Tile(Grid grid, int x, int y, int cellSize, int offset, boolean hovering) {
		super(grid, x, y, cellSize, offset);
		this.hovering = hovering;
	}

	@Override
	public void tick() {

		// Collect variables
		int cellSize = grid.getCellSize();
		int offset = grid.getOffset();
		boolean foodSourceButton = grid.getController().getKeyManager().keyJustPressed(KeyEvent.VK_F);
		boolean nestButton = grid.getController().getKeyManager().keyJustPressed(KeyEvent.VK_N);

		if (hovering && foodSourceButton) {
			// Replace this Tile with a new FoodSource
			grid.setNode(getX(), getY(), new FoodSource(grid, getX(), getY(), cellSize, offset, hovering));
			grid.getFoodPositions().add(getGridPosition());
		}
		if (hovering && nestButton) {
			// Replace this Tile with a Nest
			grid.setNode(getX(), getY(), new Nest(grid, getX(), getY(), cellSize, offset, hovering));
			// Update nest position list
			grid.getNestPositions().add(getGridPosition());
		}
	}

	@Override
	public void render(Graphics g) {
		if (isBlocking()) {
			g.setColor(Color.DARK_GRAY);
		} else {
			// Get the index of the most prevalent pheromone
			int index = getPheromoneWithHighestValue();
			// Calculate hue based on index
			float hue = 0.49F + 0.1F * index;
			// Calculate saturation based on pheromone amount
			float saturation = (float) (getPheromoneAmount(index) / GridNode.getMaxPheromone());
			// Set the color of this tile
			g.setColor(Color.getHSBColor(hue, saturation, 1));
		}

		// Display this Tile
		g.fillRect(bounds.x + 1, bounds.y + 1, bounds.width - 1, bounds.height - 1);
	}

	@Override
	public void onClick() {
		blocking = !blocking;
	}

}
