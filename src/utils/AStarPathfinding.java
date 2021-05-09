package utils;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import grid.Grid;
import grid.GridNode;

/**
 * Basic implementation of A* pathfinding to be used with the Grid and GridNode
 * classes.
 * 
 * @author Max Ehringhausen
 *
 */
public class AStarPathfinding {
	private static final int MOVE_STRAIGHT_COST = 10;
	private static final int MOVE_DIAGONAL_COST = 14; // +40% for diagonal movement
	private Grid grid;
	private CopyOnWriteArrayList<List<GridNode>> paths = new CopyOnWriteArrayList<>();

	public AStarPathfinding(Grid grid) {
		this.grid = grid;

		// Calculate all nearby nodes for each node ONCE and save them
		findAllNeighbours();
	}

	/**
	 * This (debug) method simply draws red dots on found paths.
	 * 
	 * @param g the AWT Graphics to draw on
	 */
	public void render(Graphics g) {
		synchronized (paths) {
			for (List<GridNode> path : paths) {
				for (GridNode gn : path) {
					g.setColor(Color.red);
					g.fillRect((gn.getBounds().x + gn.getBounds().width / 3) + grid.getOffset() / 3,
							(gn.getBounds().y + gn.getBounds().height / 3) + grid.getOffset() / 3,
							gn.getBounds().width / 3, gn.getBounds().height / 3);
				}
			}
		}
	}

	/**
	 * This method should be called if the the Grid has changed. It caches all
	 * neighbours of all nodes.
	 */
	public final void findAllNeighbours() {
		// Resetting previously found paths
		paths = new CopyOnWriteArrayList<>();

		// Finding neighbours
		for (int x = 0; x < grid.getCellCount(); x++) {
			for (int y = 0; y < grid.getCellCount(); y++) {
				GridNode gn = grid.getNode(x, y);
				gn.setNearbyNodes(findNearbyNodes(gn));
			}
		}
	}

	/**
	 * If possible, finds the shortest path between two GridNodes.
	 * 
	 * @param startX the Gird-x-coordinate of the starting GridNode
	 * @param startY the Gird-y-coordinate of the starting GridNode
	 * @param endX   the Gird-x-coordinate of the destination GridNode
	 * @param endY   the Gird-y-coordinate of the destination GridNode
	 * @return A list of GridNodes if a path was found, otherwise null
	 */
	public List<GridNode> findPath(int startX, int startY, int endX, int endY) {

		List<GridNode> openNodes = new ArrayList<>();
		List<GridNode> closedNodes = new ArrayList<>();
		GridNode startNode = grid.getNode(startX, startY);
		GridNode endNode = grid.getNode(endX, endY);

		if (startNode == endNode) {
			return new ArrayList<>();
		}
		openNodes.add(startNode);

		// Resetting all GridNodes from previous operations
		for (int x = 0; x < grid.getCellCount(); x++) {
			for (int y = 0; y < grid.getCellCount(); y++) {
				GridNode gn = grid.getNode(x, y);
				gn.setGCost(Integer.MAX_VALUE);
				gn.calculateFCost();
				gn.setPreviousNode(null);
			}
		}

		startNode.setGCost(0);
		startNode.setHCost(calculateDistanceCost(startNode, endNode));
		startNode.calculateFCost();

		while (!openNodes.isEmpty()) {

			GridNode currentNode = findLowestFCostNode(openNodes);

			if (currentNode == endNode) {
				// Reached end
				paths.add(calculatePath(endNode));
				return paths.get(paths.size() - 1);
			}

			openNodes.remove(currentNode);
			closedNodes.add(currentNode);

			for (GridNode nearbyNode : currentNode.getNearbyNodes()) {

				if (closedNodes.contains(nearbyNode)) {
					continue;
				}

				if (nearbyNode.isBlocking()) {
					closedNodes.add(nearbyNode);
					continue;
				}

				int pseudoGCost = currentNode.getGCost() + calculateDistanceCost(currentNode, nearbyNode);
				if (pseudoGCost < nearbyNode.getGCost()) {
					nearbyNode.setPreviousNode(currentNode);
					nearbyNode.setGCost(pseudoGCost);
					nearbyNode.setHCost(calculateDistanceCost(nearbyNode, endNode));
					nearbyNode.calculateFCost();

					if (!openNodes.contains(nearbyNode)) {
						openNodes.add(nearbyNode);
					}
				}
			}
		}

		// Ran out of nodes on the openNodes list - No path found
		return new ArrayList<>();
	}

	/**
	 * Finds all neighbours for a given GridNode. A GridNode may have 3, 5, or 8
	 * neighbours, depending on where it is located in the Grid.
	 * 
	 * @param gn the GridNode for which all neighbours should be found
	 * @return all neighbours in the Grid
	 */
	private List<GridNode> findNearbyNodes(GridNode gn) {
		List<GridNode> nearbyList = new ArrayList<>();

		if (gn.getX() - 1 >= 0) {
			// Left
			nearbyList.add(grid.getNode(gn.getX() - 1, gn.getY()));

			if (gn.getY() + 1 < grid.getCellCount()) {
				// Down Left
				nearbyList.add(grid.getNode(gn.getX() - 1, gn.getY() + 1));
			}

			if (gn.getY() - 1 >= 0) {
				// Up Left
				nearbyList.add(grid.getNode(gn.getX() - 1, gn.getY() - 1));
			}
		}

		if (gn.getX() + 1 < grid.getCellCount()) {
			// Right
			nearbyList.add(grid.getNode(gn.getX() + 1, gn.getY()));

			if (gn.getY() + 1 < grid.getCellCount()) {
				// Down Right
				nearbyList.add(grid.getNode(gn.getX() + 1, gn.getY() + 1));
			}

			if (gn.getY() - 1 >= 0) {
				// Up Right
				nearbyList.add(grid.getNode(gn.getX() + 1, gn.getY() - 1));
			}
		}

		if (gn.getY() + 1 < grid.getCellCount()) {
			// Down
			nearbyList.add(grid.getNode(gn.getX(), gn.getY() + 1));
		}

		if (gn.getY() - 1 >= 0) {
			// Up
			nearbyList.add(grid.getNode(gn.getX(), gn.getY() - 1));
		}

		return nearbyList;
	}

	/**
	 * Traces back a found path by traversing through all previous Nodes of a given
	 * GridNode.
	 * 
	 * @param endNode the final GridNode of a found path
	 * @return a list of all GridNodes that a part of the found path
	 */
	private static List<GridNode> calculatePath(GridNode endNode) {
		List<GridNode> path = new ArrayList<>();
		path.add(endNode);

		GridNode currentNode = endNode;

		while (currentNode.getPreviousNode() != null) {
			path.add(currentNode.getPreviousNode());
			currentNode = currentNode.getPreviousNode();
		}

		// Reverse, since path is calculated backwards
		Collections.reverse(path);
		return path;
	}

	/**
	 * Calculates the effective cost of traversing the Grid from GridNode A to
	 * GridNode B.
	 * 
	 * @param a the first GridNode
	 * @param b the second GridNode
	 * @return the effective cost of traversing the Grid between A and B
	 */
	public static int calculateDistanceCost(GridNode a, GridNode b) {
		int xDistance = Math.abs(a.getX() - b.getX());
		int yDistance = Math.abs(a.getY() - b.getY());
		int remaining = Math.abs(xDistance - yDistance);
		return MOVE_DIAGONAL_COST * Math.min(xDistance, yDistance) + MOVE_STRAIGHT_COST * remaining;
	}

	/**
	 * Finds the GridNode with the lowest FCost from a list of GridNodes. Naive
	 * implementation, runtime is in O(n).
	 * 
	 * @param gridNodeList a list of GridNodes where the GridNode with the lowest
	 *                     FCost should be found
	 * @return the GridNode with the lowest FCost
	 */
	private static GridNode findLowestFCostNode(List<GridNode> gridNodeList) {

		GridNode lowestFCostNode = gridNodeList.get(0);
		for (int i = 1; i < gridNodeList.size(); i++) {
			if (gridNodeList.get(i).getFCost() < lowestFCostNode.getFCost()) {
				lowestFCostNode = gridNodeList.get(i);
			}
		}

		return lowestFCostNode;
	}
}
