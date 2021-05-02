package grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * The AntVi Nest class. One of the three types of GridNodes.
 * 
 * @author Max Ehringhausen
 *
 */
public class Nest extends GridNode {

	/**
	 * Creates a Nest.
	 * 
	 * @param grid     the Grid this Nest is part of
	 * @param x        the x-coordinate in the Grid
	 * @param y        the y-coordinate in the Grid
	 * @param cellSize the cellSize based on the Grid
	 * @param offset   the offset in absolute pixels
	 */
	protected Nest(Grid grid, int x, int y, int cellSize, int offset) {
		super(grid, x, y, cellSize, offset);
	}

	@Override
	public void tick() {
		if (grid.getNestPositions().size() > 1 && hovering
				&& grid.getController().getKeyManager().keyJustPressed(KeyEvent.VK_N)) {
			// Remove existing Nest, but always keep at least one
			grid.setNode(getX(), getY(), new Tile(grid, getX(), getY(), grid.getCellSize(), grid.getOffset()));
			grid.getNestPositions().remove(getGridPosition());
		}
	}

	@Override
	public void render(Graphics g) {
		// RGB brown
		g.setColor(new Color(133, 87, 35));
		// Display this Nest
		g.fillRect(bounds.x + 1, bounds.y + 1, bounds.width - 1, bounds.height - 1);

	}

	@Override
	public void onClick() {
		// Nothing to do.

	}

}