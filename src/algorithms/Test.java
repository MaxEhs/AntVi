package algorithms;

import java.util.List;

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
public class Test extends Model {

	private final static int PHEROMONE_ONE = 0;
	private final static int PHEROMONE_TWO = 1;

	public Test(Controller controller, Grid grid) {
		super(controller, grid);
	}

	@Override
	public void generateSolutions() {

		for (Ant ant : getAnts()) {

			List<GridNode> lookingAt = ant.getLookingAt();
			int randomIndex = Ant.random.nextInt(7);
			double randomChance = Ant.random.nextDouble();

			// Randomly move based on a percent chance
			if (randomChance <= getRandomTurnChance()) {
				ant.turnTowards(randomIndex);
				continue;
			}

			// Ant is in a dead-end; do a 180° flip
			if (lookingAt.isEmpty()) {
				ant.turnAround();
				continue;
			}

			GridNode bestChoice = null;
			if (ant.isCarryingFood()) {

				// Move to node with highest pheromone one
				bestChoice = ant.getNodeByChance(PHEROMONE_ONE, lookingAt, ant.isCarryingFood());

			} else {

				// Move to node with highest pheromone two
				bestChoice = ant.getNodeByChance(PHEROMONE_TWO, lookingAt, ant.isCarryingFood());

			}
			ant.moveTo(bestChoice);
		}
	}

	@Override
	public void daemonActions() {
		for (Ant ant : getAnts()) {
			if (ant.isCarryingFood()) {

				// Deposit pheromone two on current node
				getGrid().getNode(ant.getPosition()).increasePheromoneBy(PHEROMONE_TWO, getPheromoneStrength());

				// If the nest is reached, set carrying food to false
				if (getGrid().getNode(ant.getPosition()) instanceof Nest) {
					ant.setCarryingFood(false);
					ant.turnAround();
					increaseFoodGathered();
				}

			} else {
				// Deposit pheromone one
				getGrid().getNode(ant.getPosition()).increasePheromoneBy(PHEROMONE_ONE, getPheromoneStrength());

				// If food is reached, set carrying food to true
				if (getGrid().getNode(ant.getPosition()) instanceof FoodSource) {
					ant.setCarryingFood(true);
					ant.turnAround();
				}
			}
		}
	}

	@Override
	public void pheromoneUpdate() {
		for (int x = 0; x < getGrid().getCellCount(); x++) {
			for (int y = 0; y < getGrid().getCellCount(); y++) {
				// Iterating through all GridNodes and decreasing all pheromones
				GridNode tempNode = getGrid().getNode(x, y);
				for (int i = 0; i < tempNode.getPheromones().length; i++) {
					tempNode.decreasePheromoneBy(i, getEvaporationSpeed());
				}
			}
		}
	}

}
