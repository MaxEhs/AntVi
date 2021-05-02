package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import grid.FoodSource;
import grid.GridNode;

/**
 * The AntVi SettingsWindow class. This class belongs to the GUI and contains
 * all interactive elements that have to do with the Simulation.
 * 
 * @author Max Ehringhausen
 *
 */
public class SettingsWindow {

	private Controller controller;

	private JFrame frame;
	private JLabel nestPositionLabel;
	private JLabel foodGatheredLabel;
	private JLabel modelTicksLabel;
	private JPanel mainPanel;
	private JSlider modelSpeedSlider;
	private JSlider cellCountSlider;
	private JSlider pheromoneStrengthSlider;
	private JSlider pheromoneEvaporationSlider;
	private JSlider pheromoneFallOffSlider;
	private JSlider moveRandomizationSlider;
	private JSlider maximumPheromoneSlider;
	private JSpinner antCountInput;
	private JButton playPauseButton;
	private String title;
	private int width;
	private int height;

	public SettingsWindow(Controller controller, String title, int width, int height) {

		this.controller = controller;
		this.title = title;
		this.width = width;
		this.height = height;

		createSettingsWindow();
	}

	/**
	 * Used for initializing the settings window
	 */
	private void createSettingsWindow() {

		// Main JFrame
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setIconImage(new ImageIcon("src/cog.png").getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		mainPanel = new JPanel();
		frame.add(mainPanel);

		// How-to Label
		JLabel howTo = new JLabel("<html><body>Welcome to AntVi!<br>You can interact with the Grid as follows:<br>"
				+ "Highlight the Grid window and<br>> press N to place or remove a Nest<br>"
				+ "> press F to place or remove a FoodSource<br>> click to place or remove a wall</html></body>",
				SwingConstants.CENTER);
		howTo.setBorder(BorderFactory.createTitledBorder("How To:"));
		howTo.setPreferredSize(new Dimension(width - 20, 170));
		mainPanel.add(howTo);

		// Grid cell count slider
		cellCountSlider = new JSlider(10, 100, 20);
		cellCountSlider.setBorder(BorderFactory.createTitledBorder("Grid Size: 20"));
		cellCountSlider.setPreferredSize(new Dimension(width - 30, 50));
		cellCountSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {

				cellCountSlider.setBorder(
						BorderFactory.createTitledBorder(String.format("Grid Size: %s", cellCountSlider.getValue())));

				if (!cellCountSlider.getValueIsAdjusting()) {
					controller.getGrid().setCellCount(cellCountSlider.getValue());
				}
			}
		});
		mainPanel.add(cellCountSlider);

		// Pheromone strength slider
		pheromoneStrengthSlider = new JSlider(0, 500, 60);
		pheromoneStrengthSlider.setBorder(BorderFactory.createTitledBorder("Pheromone Strength: 6,0"));
		pheromoneStrengthSlider.setPreferredSize(new Dimension(width - 30, 50));
		pheromoneStrengthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {

				double value = (double) pheromoneStrengthSlider.getValue() / 10D;
				pheromoneStrengthSlider
						.setBorder(BorderFactory.createTitledBorder(String.format("Pheromone Strength: %.1f", value)));

				if (!pheromoneStrengthSlider.getValueIsAdjusting()) {
					controller.getModel().setPheromoneStrength(value);
				}
			}
		});
		mainPanel.add(pheromoneStrengthSlider);

		// Pheromone evaporation speed slider
		pheromoneEvaporationSlider = new JSlider(0, 200, 40);
		pheromoneEvaporationSlider.setBorder(BorderFactory.createTitledBorder("Pheromone Evaporation Speed: 0,40"));
		pheromoneEvaporationSlider.setPreferredSize(new Dimension(width - 30, 50));
		pheromoneEvaporationSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {

				double value = (double) pheromoneEvaporationSlider.getValue() / 100D;
				pheromoneEvaporationSlider.setBorder(
						BorderFactory.createTitledBorder(String.format("Pheromone Evaporation Speed: %.2f", value)));

				if (!pheromoneEvaporationSlider.getValueIsAdjusting()) {
					controller.getModel().setEvaporationSpeed(value);
				}
			}
		});
		mainPanel.add(pheromoneEvaporationSlider);

		// Pheromone falloff slider
		pheromoneFallOffSlider = new JSlider(0, 100, 75);
		pheromoneFallOffSlider.setBorder(BorderFactory.createTitledBorder("Pheromone Falloff Ratio: 75 %"));
		pheromoneFallOffSlider.setPreferredSize(new Dimension(width - 30, 50));
		pheromoneFallOffSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {

				double value = (double) pheromoneFallOffSlider.getValue();
				pheromoneFallOffSlider.setBorder(
						BorderFactory.createTitledBorder(String.format("Pheromone Falloff Ratio: %.0f %%", value)));

				if (!pheromoneFallOffSlider.getValueIsAdjusting()) {
					controller.getModel().setPheromoneFallOff(value);
				}
			}
		});
		mainPanel.add(pheromoneFallOffSlider);

		// Movement randomization slider
		moveRandomizationSlider = new JSlider(0, 100, 8);
		moveRandomizationSlider.setBorder(BorderFactory.createTitledBorder("Random Movement Chance: 8 %"));
		moveRandomizationSlider.setPreferredSize(new Dimension(width - 30, 50));
		moveRandomizationSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {

				double value = (double) moveRandomizationSlider.getValue() / 100D;
				moveRandomizationSlider.setBorder(BorderFactory
						.createTitledBorder(String.format("Random Movement Chance: %.0f %%", value * 100)));

				if (!moveRandomizationSlider.getValueIsAdjusting()) {
					controller.getModel().setRandomTurnChance(value);
				}
			}
		});
		mainPanel.add(moveRandomizationSlider);

		// Maximum pheromone saturation slider
		maximumPheromoneSlider = new JSlider(1, 1000, 150);
		maximumPheromoneSlider.setBorder(BorderFactory.createTitledBorder("Maximum Pheromone per Cell: 150"));
		maximumPheromoneSlider.setPreferredSize(new Dimension(width - 30, 50));
		maximumPheromoneSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {

				int value = maximumPheromoneSlider.getValue();
				maximumPheromoneSlider.setBorder(
						BorderFactory.createTitledBorder(String.format("Maximum Pheromone per Cell: %d", value)));

				if (!maximumPheromoneSlider.getValueIsAdjusting()) {
					GridNode.MAX_PHEROMONE = value;
				}
			}
		});
		mainPanel.add(maximumPheromoneSlider);

		// Ant count spinner
		antCountInput = new JSpinner(new SpinnerNumberModel(0, 0, 200, 1));
		antCountInput.setPreferredSize(new Dimension(width / 2 - 20, 50));
		antCountInput.setBorder(BorderFactory.createTitledBorder("Ant Count:"));
		antCountInput.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				controller.getModel().setAntCount((int) antCountInput.getValue());
			}
		});
		mainPanel.add(antCountInput);

		// Nest position label
		nestPositionLabel = new JLabel("0 / 0", SwingConstants.CENTER);
		nestPositionLabel.setBorder(BorderFactory.createTitledBorder("Nest Position:"));
		nestPositionLabel.setPreferredSize(new Dimension(width / 2 - 20, 50));
		mainPanel.add(nestPositionLabel);

		// Food gathered label
		foodGatheredLabel = new JLabel("0", SwingConstants.CENTER);
		foodGatheredLabel.setBorder(BorderFactory.createTitledBorder("Food Gathered:"));
		foodGatheredLabel.setPreferredSize(new Dimension(width / 2 - 20, 50));
		mainPanel.add(foodGatheredLabel);

		// Model ticks label
		modelTicksLabel = new JLabel("0", SwingConstants.CENTER);
		modelTicksLabel.setBorder(BorderFactory.createTitledBorder("Model Ticks:"));
		modelTicksLabel.setPreferredSize(new Dimension(width / 2 - 20, 50));
		mainPanel.add(modelTicksLabel);

		// Play/Pause Button
		playPauseButton = new JButton("Play Simulation");
		playPauseButton.setPreferredSize(new Dimension(width / 3 - 10, 60));
		playPauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (controller.isModelRunning()) {
					controller.setModelRunning(false);
					playPauseButton.setText("Play");
				} else {
					controller.setModelRunning(true);
					playPauseButton.setText("Pause");
				}

			}
		});
		mainPanel.add(playPauseButton);

		// Reset Button
		JButton resetButton = new JButton("Reset");
		resetButton.setPreferredSize(new Dimension(width / 3 - 10, 60));
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Reset A* paths
				controller.getPathfinding().findAllNeighbours();

				// Reset Simulation
				controller.setModelRunning(false);
				playPauseButton.setText("Play Simulation");
				controller.getModel().setFoodGathered(0);
				controller.setModelTicks(0);
				modelTicksLabel.setText("0");

				// Reset Ants
				controller.getModel().setAntCount(0);
				antCountInput.setValue(0);

				// Reset pheromones
				for (int x = 0; x < controller.getGrid().getCellCount(); x++) {
					for (int y = 0; y < controller.getGrid().getCellCount(); y++) {
						// Iterating through all GridNodes and decreasing all pheromones to 0
						GridNode tempNode = controller.getGrid().getNode(x, y);
						for (int i = 0; i < tempNode.getPheromones().length; i++) {
							tempNode.decreasePheromoneBy(i, Integer.MAX_VALUE);
						}
					}
				}
			}
		});
		mainPanel.add(resetButton);

		// Clear Grid Button
		JButton clearGridButton = new JButton("Clear Grid");
		clearGridButton.setPreferredSize(new Dimension(width / 3 - 10, 60));
		clearGridButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Re-make Grid of the same size as before
				controller.getGrid().setCellCount(controller.getGrid().getCellCount());
				nestPositionLabel.setText("0 / 0");
			}
		});
		mainPanel.add(clearGridButton);

		// Simulation speed slider
		modelSpeedSlider = new JSlider(1, 30, 30);
		modelSpeedSlider.setBorder(BorderFactory.createTitledBorder("Simulation Ticks/s: 30"));
		modelSpeedSlider.setPreferredSize(new Dimension(width - 30, 50));
		modelSpeedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {

				modelSpeedSlider.setBorder(BorderFactory
						.createTitledBorder(String.format("Simulation Ticks/s: %s", modelSpeedSlider.getValue())));

				if (!modelSpeedSlider.getValueIsAdjusting()) {
					controller.setModelSpeed(modelSpeedSlider.getValue());
				}
			}
		});
		mainPanel.add(modelSpeedSlider);

		// Show shortest path(s) Button
		JButton displayShortestPathButton = new JButton("Show Shortest Path(s)");
		displayShortestPathButton.setPreferredSize(new Dimension(width / 2 - 20, 60));
		displayShortestPathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Reset previous paths
				controller.getPathfinding().findAllNeighbours();

				int startX = controller.getGrid().getNestPositions().get(0).x; // TODO
				int startY = controller.getGrid().getNestPositions().get(0).y;
				int cellCount = controller.getGrid().getCellCount();
				GridNode[][] nodes = controller.getGrid().getNodes();

				for (int x = 0; x < cellCount; x++) {
					for (int y = 0; y < cellCount; y++) {

						if (nodes[x][y] instanceof FoodSource) {

							int targetX = x;
							int targetY = y;
							controller.getPathfinding().findPath(startX, startY, targetX, targetY);
						}
					}
				}
			}
		});
		mainPanel.add(displayShortestPathButton);

		// Hide shortest path(s) Button
		JButton hideShortestPathButton = new JButton("Hide Shortest Path(s)");
		hideShortestPathButton.setPreferredSize(new Dimension(width / 2 - 20, 60));
		hideShortestPathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Reset paths
				controller.getPathfinding().findAllNeighbours();

			}
		});
		mainPanel.add(hideShortestPathButton);

	}

	public JFrame getFrame() {
		return frame;
	}

	public JLabel getNestPositionLabel() {
		return nestPositionLabel;
	}

	public JSpinner getAntCountInput() {
		return antCountInput;
	}

	public JLabel getFoodGatheredLabel() {
		return foodGatheredLabel;
	}

	public JLabel getModelTicksLabel() {
		return modelTicksLabel;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JSlider getPheromoneStrengthSlider() {
		return pheromoneStrengthSlider;
	}

}
