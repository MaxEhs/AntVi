package grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * The AntVi FoodSource class - One of the three types of GridNodes.
 * 
 * @author Max Ehringhausen
 *
 */
public class FoodSource extends GridNode {

	/**
	 * Creates a FoodSource.
	 * 
	 * @param grid     the Grid this FoodSource is part of
	 * @param x        the x-coordinate in the Grid
	 * @param y        the y-coordinate in the Grid
	 * @param cellSize the cellSize based on the Grid
	 * @param offset   the offset in absolute pixels
	 * @param hovering whether the mouse is hovering over this GridNode
	 */
	protected FoodSource(Grid grid, int x, int y, int cellSize, int offset, boolean hovering) {
		super(grid, x, y, cellSize, offset);
		this.hovering = hovering;
		blocking = false;
	}

	@Override
	public void tick() {
		if (hovering && grid.getController().getKeyManager().keyJustPressed(KeyEvent.VK_F)) {
			// Remove existing FoodSource
			grid.setNode(getX(), getY(),
					new Tile(grid, getX(), getY(), grid.getCellSize(), grid.getOffset(), hovering));
			grid.getFoodPositions().remove(getGridPosition());
		}
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(bounds.x + 1, bounds.y + 1, bounds.width - 1, bounds.height - 1);
	}

	@Override
	public void onClick() {
		// Nothing to do.
	}

}
