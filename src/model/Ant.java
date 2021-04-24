package model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import grid.FoodSource;
import grid.GridNode;
import grid.Nest;
import utils.ObjectWithPercentage;

/**
 * The AntVi Ant class. It contains methods to create Ant behaviour with, as
 * well as a render method for graphics.
 * 
 * @author Max Ehringhausen
 *
 */
public class Ant {

	Model model;
	BufferedImage icon;
	private Point position;
	private boolean carryingFood;
	private boolean followingTrail;
	private Facing facing;
	public static final Random random = new Random();

	public enum Facing {
		UP, DOWN, LEFT, RIGHT, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT
	}

	public Ant(Model model, Point position) {
		this.model = model;
		this.position = position;
		try {
			icon = ImageIO.read(new File("src/ant.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Initially face a random direction
		final List<Facing> directions = Collections.unmodifiableList(Arrays.asList(Facing.values()));
		this.facing = directions.get(random.nextInt(directions.size()));
	}

	public void render(Graphics2D g) {

		int gridCellSize = model.getGrid().getCellSize();
		int initialGridCellSize = model.getGrid().getInitialCellSize();

		double scalingPercentage = ((double) gridCellSize / initialGridCellSize);
		int offset = model.getGrid().getOffset();
		double rotation = Math.toRadians(0);

		// All +45° because the icon is tilted
		switch (facing) {
		case UP:
			rotation = Math.toRadians(45.0);
			break;
		case DOWN:
			rotation = Math.toRadians(225.0);
			break;
		case LEFT:
			rotation = Math.toRadians(315.0);
			break;
		case RIGHT:
			rotation = Math.toRadians(135.0);
			break;
		case UPLEFT:
			rotation = Math.toRadians(360.0);
			break;
		case UPRIGHT:
			rotation = Math.toRadians(90.0);
			break;
		case DOWNLEFT:
			rotation = Math.toRadians(270.0);
			break;
		case DOWNRIGHT:
			rotation = Math.toRadians(180.0);
			break;
		}

		int drawLocationX = (int) ((position.x * gridCellSize + offset) + (gridCellSize / 2D)
				- (scalingPercentage * icon.getWidth() / 2));
		int drawLocationY = (int) ((position.y * gridCellSize + offset) + (gridCellSize / 2D)
				- (scalingPercentage * icon.getWidth() / 2));

		double imageCenter = scalingPercentage * icon.getWidth() / 2D;

		AffineTransform transform = AffineTransform.getRotateInstance(rotation, imageCenter, imageCenter);
		transform.scale(scalingPercentage, scalingPercentage);

		AffineTransformOp transformOperation = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

		g.drawImage(transformOperation.filter(icon, null), drawLocationX, drawLocationY, null);
	}

	/**
	 * Finds the three GridNodes in front of an Ant based on the direction it is
	 * facing.
	 * 
	 * @return a List of GridNodes that are walkable and in front of the Ant
	 */
	public List<GridNode> getLookingAt() {

		ArrayList<GridNode> lookingAt = new ArrayList<>();
		Point left = null;
		Point front = null;
		Point right = null;

		switch (facing) {
		case UP:
			left = new Point(position.x - 1, position.y - 1);
			front = new Point(position.x, position.y - 1);
			right = new Point(position.x + 1, position.y - 1);
			break;
		case DOWN:
			left = new Point(position.x + 1, position.y + 1);
			front = new Point(position.x, position.y + 1);
			right = new Point(position.x - 1, position.y + 1);
			break;
		case LEFT:
			left = new Point(position.x - 1, position.y + 1);
			front = new Point(position.x - 1, position.y);
			right = new Point(position.x - 1, position.y - 1);
			break;
		case RIGHT:
			left = new Point(position.x + 1, position.y - 1);
			front = new Point(position.x + 1, position.y);
			right = new Point(position.x + 1, position.y + 1);
			break;
		case UPLEFT:
			left = new Point(position.x - 1, position.y);
			front = new Point(position.x - 1, position.y - 1);
			right = new Point(position.x, position.y - 1);
			break;
		case UPRIGHT:
			left = new Point(position.x, position.y - 1);
			front = new Point(position.x + 1, position.y - 1);
			right = new Point(position.x + 1, position.y);
			break;
		case DOWNLEFT:
			left = new Point(position.x, position.y + 1);
			front = new Point(position.x - 1, position.y + 1);
			right = new Point(position.x - 1, position.y);
			break;
		case DOWNRIGHT:
			left = new Point(position.x + 1, position.y);
			front = new Point(position.x + 1, position.y + 1);
			right = new Point(position.x, position.y + 1);
			break;
		}

		if (model.getGrid().getNode(left) != null && !model.getGrid().getNode(left).isBlocking()) {
			lookingAt.add(model.getGrid().getNode(left));
		}
		if (model.getGrid().getNode(front) != null && !model.getGrid().getNode(front).isBlocking()) {
			lookingAt.add(model.getGrid().getNode(front));
		}
		if (model.getGrid().getNode(right) != null && !model.getGrid().getNode(right).isBlocking()) {
			lookingAt.add(model.getGrid().getNode(right));
		}

		return lookingAt;
	}

	/**
	 * Turns the Ant by 180°.
	 */
	public void turnAround() {
		switch (facing) {
		case UP:
			facing = Facing.DOWN;
			break;
		case DOWN:
			facing = Facing.UP;
			break;
		case LEFT:
			facing = Facing.RIGHT;
			break;
		case RIGHT:
			facing = Facing.LEFT;
			break;
		case UPLEFT:
			facing = Facing.DOWNRIGHT;
			break;
		case UPRIGHT:
			facing = Facing.DOWNLEFT;
			break;
		case DOWNLEFT:
			facing = Facing.UPRIGHT;
			break;
		case DOWNRIGHT:
			facing = Facing.UPLEFT;
			break;
		}
	}

	/**
	 * Turns the ant to face a given direction.<br>
	 * 0 = UP<br>
	 * 1 = DOWN<br>
	 * 2 = LEFT<br>
	 * 3 = RIGHT<br>
	 * 4 = UPLEFT<br>
	 * 5 = UPRIGHT<br>
	 * 6 = DOWNLEFT<br>
	 * 7 = DOWNRIGHT
	 * 
	 * @param direction as integer
	 */
	public void turnTowards(int direction) {
		if (direction >= 0 && direction < Facing.values().length) {
			facing = Facing.values()[direction];
		}
	}

	public void moveTo(GridNode gn) {

		if (gn == null) {
			return;
		}

		Point targetPos = gn.getGridPosition();

		if (targetPos.x < position.x && targetPos.y < position.y) {
			// UPLEFT
			setPosition(targetPos);
			facing = Facing.UPLEFT;

		} else if (targetPos.x > position.x && targetPos.y > position.y) {
			// DOWNRIGHT
			setPosition(targetPos);
			facing = Facing.DOWNRIGHT;

		} else if (targetPos.x > position.x && targetPos.y < position.y) {
			// UPRIGHT
			setPosition(targetPos);
			facing = Facing.UPRIGHT;

		} else if (targetPos.x < position.x && targetPos.y > position.y) {
			// DOWNLEFT
			setPosition(targetPos);
			facing = Facing.DOWNLEFT;

		} else if (targetPos.x == position.x && targetPos.y < position.y) {
			// UP
			setPosition(targetPos);
			facing = Facing.UP;

		} else if (targetPos.x == position.x && targetPos.y > position.y) {
			// DOWN
			setPosition(targetPos);
			facing = Facing.DOWN;

		} else if (targetPos.x < position.x && targetPos.y == position.y) {
			// LEFT
			setPosition(targetPos);
			facing = Facing.LEFT;

		} else if (targetPos.x > position.x && targetPos.y == position.y) {
			// RIGHT
			setPosition(targetPos);
			facing = Facing.RIGHT;
		}
	}

	/**
	 * Finds the GridNode with the highest concentration of a given pheromone from a
	 * list of GridNodes.
	 * 
	 * @param pheromone   the ID of the pheromone (0-7)
	 * @param nodes       a list of GridNodes to check
	 * @param lookForNest whether a Nest node should always be preferred, prefers
	 *                    FoodSource nodes if false
	 * @return the GridNode with the highest concentration of the given pheromone, a
	 *         random GridNode from the list if they are all the same.
	 */
	public GridNode getNodeWithHighestConcentration(int pheromone, List<GridNode> nodes, boolean lookForNest) {

		GridNode target = nodes.get(random.nextInt(nodes.size()));

		// Find node with highest pheromone or Nest or FoodSource
		for (GridNode gn : nodes) {
			if (target.getPheromoneAmount(pheromone) < gn.getPheromoneAmount(pheromone)) {
				target = gn;
			}
			if ((lookForNest && gn instanceof Nest) || (!lookForNest && gn instanceof FoodSource)) {
				target = gn;
				break;
			}
		}

		return target;
	}

	/**
	 * Finds the GridNode with the lowest concentration of a given pheromone from a
	 * list of GridNodes.
	 * 
	 * @param pheromone   the ID of the pheromone (0-7)
	 * @param nodes       a list of GridNodes to check
	 * @param lookForNest whether a Nest node should always be preferred, prefers
	 *                    FoodSource nodes if false
	 * @return the GridNode with the lowest concentration of the given pheromone, a
	 *         random GridNode from the list if they are all the same.
	 */
	public GridNode getNodeWithLowestConcentration(int pheromone, List<GridNode> nodes, boolean lookForNest) {

		GridNode target = nodes.get(random.nextInt(nodes.size()));

		// Find node with lowest pheromone or Nest or FoodSource
		for (GridNode gn : nodes) {
			if (target.getPheromoneAmount(pheromone) > gn.getPheromoneAmount(pheromone)) {
				target = gn;
			}
			if ((lookForNest && gn instanceof Nest) || (!lookForNest && gn instanceof FoodSource)) {
				target = gn;
				break;
			}
		}
		return target;
	}

	/**
	 * Picks a node by chance. Nodes with the highest concentration of a given
	 * pheromone have the biggest chance.
	 * 
	 * @param pheromone   the pheromone used for making a decision
	 * @param nodes       a list of nodes from which should be picked
	 * @param lookForNest whether a Nest node should always be preferred, prefers
	 *                    FoodSource nodes if false
	 * @return the node that was picked.
	 */
	public GridNode getNodeByChance(int pheromone, List<GridNode> nodes, boolean lookForNest) {

		GridNode target = null;
		ArrayList<ObjectWithPercentage<GridNode>> chanceList = new ArrayList<>();
		double totalPercentages = 0;

		// Roll for a Node by chance, based on pheromone concentrations
		for (GridNode gn : nodes) {
			double pheromoneSaturation = gn.getPheromoneAmount(pheromone) / GridNode.MAX_PHEROMONE;
			chanceList.add(new ObjectWithPercentage<>(gn, pheromoneSaturation));
			totalPercentages += pheromoneSaturation;
		}
		
		double chance = totalPercentages * random.nextDouble();
		double sum = 0;
		for (int i = 0; i < chanceList.size(); i++) {
			sum += chanceList.get(i).getPercentage();
			if (sum >= chance) {
				target = chanceList.get(i).getObject();
			}
		}
		if (target == null) {
			target = chanceList.get(chanceList.size() - 1).getObject();
		}

		return target;
	}

	/**
	 * Gets unobstructed nodes around the Ant.
	 * 
	 * @return a List of walkable GridNodes adjacent to the Ant.
	 */
	public List<GridNode> getSurroundingNodes() {
		ArrayList<GridNode> list = new ArrayList<>();

		for (GridNode gn : model.getGrid().getNode(position).getNearbyNodes()) {
			if (!gn.isBlocking()) {
				list.add(gn);
			}
		}

		return list;
	}

	public Facing getFacing() {
		return facing;
	}

	public void setFacing(Facing facing) {
		this.facing = facing;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public boolean isCarryingFood() {
		return carryingFood;
	}

	public void setCarryingFood(boolean carryingFood) {
		this.carryingFood = carryingFood;
	}

	public boolean isFollowingTrail() {
		return followingTrail;
	}

	public void setFollowingTrail(boolean followingTrail) {
		this.followingTrail = followingTrail;
	}

}
