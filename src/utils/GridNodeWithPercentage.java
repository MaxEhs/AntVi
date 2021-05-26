package utils;

import grid.GridNode;

/**
 * The AntVi GridNodeWithPercentage class - This is a helper class used by the
 * Ant class to roll for probabilities.
 * 
 * @author Max Ehringhausen
 */
public class GridNodeWithPercentage {

	private GridNode gridNode;
	private double percentage;

	public GridNodeWithPercentage(GridNode gridNode, double percentage) {
		this.gridNode = gridNode;
		this.percentage = percentage;
	}

	public GridNode getGridNode() {
		return gridNode;
	}

	public void setGridNode(GridNode gridNode) {
		this.gridNode = gridNode;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

}
