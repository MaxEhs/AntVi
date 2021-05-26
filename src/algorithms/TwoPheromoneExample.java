package algorithms;

import java.util.List;
import java.util.Random;

import controller.Controller;
import grid.FoodSource;
import grid.Grid;
import grid.GridNode;
import grid.Nest;
import model.Ant;
import model.Model;

/**
 * This is an example implementation of an ACO system.
 * 
 * @author Max Ehringhausen
 *
 */
public class TwoPheromoneExample extends Model {

	private static final Random random = new Random();

	private static final int PHEROMONE_ONE = 0;
	private static final int PHEROMONE_TWO = 1;

	public TwoPheromoneExample(Controller controller, Grid grid) {
		super(controller, grid);
	}

	@Override
	public void generateSolutions() {

		for (Ant ant : getAnts()) {

			List<GridNode> lookingAt;

			// If a Nest or FoodSource has just been visited, look around
			if (ant.getStepsWalked() == 1) {
				lookingAt = ant.getSurroundingNodes();

				// Otherwise just look ahead
			} else {
				lookingAt = ant.getThreeInFront();
			}

			// Ant is in a dead-end or stuck - let her check more of her surroundings
			if (lookingAt.isEmpty()) {
				lookingAt = ant.getFiveInFront();
				if (lookingAt.isEmpty()) {
					lookingAt = ant.getSurroundingNodes();
				}
			}

			// Randomly move based on a percent chance
			if (random.nextDouble() < getRandomMoveChance()) {
				if (!lookingAt.isEmpty()) {
					ant.moveTo(lookingAt.get(random.nextInt(lookingAt.size())));
					ant.increaseStepsWalked();
				}
				continue;
			}

			GridNode bestChoice = null;
			if (ant.isCarryingFood()) {

				// Probably move to node with highest pheromone one
				bestChoice = ant.getNodeByProbablility(PHEROMONE_ONE, lookingAt, random, true, true, 1.1D);

			} else {

				// Probably move to node with highest pheromone two
				bestChoice = ant.getNodeByProbablility(PHEROMONE_TWO, lookingAt, random, true, true, 1.1D);
			}

			if (bestChoice != null) {
				ant.moveTo(bestChoice);
				ant.increaseStepsWalked();
			}
		}

	}

	@Override
	public void daemonActions() {
		for (Ant ant : getAnts()) {
			if (ant.isCarryingFood()) {

				// Deposit pheromone two on current node
				getGrid().getNode(ant.getPosition()).increasePheromoneBy(PHEROMONE_TWO,
						getPheromoneStrength() / (ant.getStepsWalked() / getPheromoneFallOff()));

				// If the nest is reached, set carrying food to false
				if (getGrid().getNode(ant.getPosition()) instanceof Nest) {
					ant.setCarryingFood(false);
					ant.resetStepsWalked();
					increaseFoodGathered();
					ant.turnAround();
				}

				// If another FoodSource is visited, reset stepsWalked
				if (getGrid().getNode(ant.getPosition()) instanceof FoodSource) {
					ant.resetStepsWalked();
				}

			} else {
				// Deposit pheromone one
				getGrid().getNode(ant.getPosition()).increasePheromoneBy(PHEROMONE_ONE,
						getPheromoneStrength() / (ant.getStepsWalked() / getPheromoneFallOff()));

				// If a FoodSource is reached, set carrying food to true
				if (getGrid().getNode(ant.getPosition()) instanceof FoodSource) {
					ant.setCarryingFood(true);
					ant.resetStepsWalked();
					ant.turnAround();
				}

				// If the Nest is visited again, reset stepsWalked
				if (getGrid().getNode(ant.getPosition()) instanceof Nest) {
					ant.resetStepsWalked();
				}
			}
		}
	}

	@Override
	public void pheromoneUpdate() {
		for (int x = 0; x < getGrid().getCellCount(); x++) {
			for (int y = 0; y < getGrid().getCellCount(); y++) {
				// Iterating through all GridNodes
				GridNode tempNode = getGrid().getNode(x, y);
				for (int i = 0; i < tempNode.getPheromones().length; i++) {

					// Naive implementation of dissipation of pheromone to surrounding GridNodes
					if (isUsingDissipation()) {
						for (GridNode gn : tempNode.getNearbyNodes()) {

							double temp = (tempNode.getPheromoneAmount(i) / GridNode.getMaxPheromone());
							gn.increasePheromoneBy(i, temp);
							tempNode.decreasePheromoneBy(i,
									temp + (getEvaporationSpeed() / tempNode.getNearbyNodes().size()));
						}
					}

					// Globally decreasing all pheromones
					tempNode.decreasePheromoneBy(i, getEvaporationSpeed());
				}
			}
		}
	}

}
