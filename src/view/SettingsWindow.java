package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

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

/**
 * The AntVi SettingsWindow class. This class belongs to the GUI and contains
 * all interactive elements that have to do with the Simulation.
 * 
 * @author Max Ehringhausen
 *
 */
public class SettingsWindow {

	private Controller controller;
	private List<PropertyChangeListener> listeners = new ArrayList<>();

	private JFrame frame;
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

	/*
	 * Used for managing PropertyChangeEvents
	 */
	private void notifyListeners(Object object, String property, Object oldValue, Object newValue) {
		for (PropertyChangeListener pcl : listeners) {
			pcl.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
		}
	}

	public void addChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Initializing the settings window components + event handling
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

		JPanel mainPanel = new JPanel();
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
		JSlider cellCountSlider = new JSlider(10, 100, 30);
		cellCountSlider.setBorder(BorderFactory.createTitledBorder("Grid Size: 30"));
		cellCountSlider.setPreferredSize(new Dimension(width - 30, 50));
		cellCountSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				cellCountSlider.setBorder(
						BorderFactory.createTitledBorder(String.format("Grid Size: %s", cellCountSlider.getValue())));

				if (!cellCountSlider.getValueIsAdjusting()) {
					notifyListeners(this, "CellCountChanged", null, cellCountSlider.getValue());
				}
			}
		});
		mainPanel.add(cellCountSlider);

		// Pheromone strength slider
		JSlider pheromoneStrengthSlider = new JSlider(0, 500, 80);
		pheromoneStrengthSlider.setBorder(BorderFactory.createTitledBorder("Pheromone Strength: 8,0"));
		pheromoneStrengthSlider.setPreferredSize(new Dimension(width - 30, 50));
		pheromoneStrengthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				double value = (double) pheromoneStrengthSlider.getValue() / 10D;
				pheromoneStrengthSlider
						.setBorder(BorderFactory.createTitledBorder(String.format("Pheromone Strength: %.1f", value)));

				if (!pheromoneStrengthSlider.getValueIsAdjusting()) {
					notifyListeners(this, "PheromoneStrengthChanged", null, value);
				}
			}
		});
		mainPanel.add(pheromoneStrengthSlider);

		// Pheromone evaporation speed slider
		JSlider pheromoneEvaporationSlider = new JSlider(0, 400, 95);
		pheromoneEvaporationSlider.setBorder(BorderFactory.createTitledBorder("Pheromone Evaporation Speed: 0,95"));
		pheromoneEvaporationSlider.setPreferredSize(new Dimension(width - 30, 50));
		pheromoneEvaporationSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				double value = (double) pheromoneEvaporationSlider.getValue() / 100D;
				pheromoneEvaporationSlider.setBorder(
						BorderFactory.createTitledBorder(String.format("Pheromone Evaporation Speed: %.2f", value)));

				if (!pheromoneEvaporationSlider.getValueIsAdjusting()) {
					notifyListeners(this, "PheromoneEvaporationChanged", null, value);
				}
			}
		});
		mainPanel.add(pheromoneEvaporationSlider);

		// Pheromone falloff slider
		JSlider pheromoneFallOffSlider = new JSlider(0, 100, 65);
		pheromoneFallOffSlider.setBorder(BorderFactory.createTitledBorder("Pheromone Falloff Ratio: 65 %"));
		pheromoneFallOffSlider.setPreferredSize(new Dimension(width - 30, 50));
		pheromoneFallOffSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				double value = (double) pheromoneFallOffSlider.getValue();
				if (value <= 0) {
					pheromoneFallOffSlider.setBorder(BorderFactory.createTitledBorder("Pheromone Falloff Ratio: OFF"));
				} else {
					pheromoneFallOffSlider.setBorder(
							BorderFactory.createTitledBorder(String.format("Pheromone Falloff Ratio: %.0f %%", value)));
				}

				if (!pheromoneFallOffSlider.getValueIsAdjusting()) {
					notifyListeners(this, "PheromoneFallOffChanged", null, value);
				}
			}
		});
		mainPanel.add(pheromoneFallOffSlider);

		// Movement randomization slider
		JSlider moveRandomizationSlider = new JSlider(0, 100, 1);
		moveRandomizationSlider.setBorder(BorderFactory.createTitledBorder("Random Movement Chance: 1 %"));
		moveRandomizationSlider.setPreferredSize(new Dimension(width - 30, 50));
		moveRandomizationSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				double value = (double) moveRandomizationSlider.getValue() / 100D;
				moveRandomizationSlider.setBorder(BorderFactory
						.createTitledBorder(String.format("Random Movement Chance: %.0f %%", value * 100)));

				if (!moveRandomizationSlider.getValueIsAdjusting()) {
					notifyListeners(this, "RandomMoveChanceChanged", null, value);
				}
			}
		});
		mainPanel.add(moveRandomizationSlider);

		// Maximum pheromone saturation slider
		JSlider maximumPheromoneSlider = new JSlider(1, 1000, 500);
		maximumPheromoneSlider.setBorder(BorderFactory.createTitledBorder("Maximum Pheromone per Cell: 500"));
		maximumPheromoneSlider.setPreferredSize(new Dimension(width - 30, 50));
		maximumPheromoneSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				int value = maximumPheromoneSlider.getValue();
				maximumPheromoneSlider.setBorder(
						BorderFactory.createTitledBorder(String.format("Maximum Pheromone per Cell: %d", value)));

				if (!maximumPheromoneSlider.getValueIsAdjusting()) {
					notifyListeners(this, "MaximumSaturationChanged", null, value);
				}
			}
		});
		mainPanel.add(maximumPheromoneSlider);

		// Ant count spinner
		JSpinner antCountInput = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
		antCountInput.setPreferredSize(new Dimension(width / 3 - 10, 50));
		antCountInput.setBorder(BorderFactory.createTitledBorder("Ant Count:"));

		antCountInput.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				notifyListeners(this, "AntCountChanged", null, antCountInput.getValue());
			}
		});

		controller.getModel().addChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("ModelChangedAntCount".equals(e.getPropertyName())) {
					// The ant count changed, probably because the Grid was scaled down
					antCountInput.setValue(e.getNewValue());
				}
			}
		});

		mainPanel.add(antCountInput);

		// Model ticks label
		JLabel modelTicksLabel = new JLabel("0", SwingConstants.CENTER);
		modelTicksLabel.setBorder(BorderFactory.createTitledBorder("Model Ticks:"));
		modelTicksLabel.setPreferredSize(new Dimension(width / 3 - 10, 50));
		controller.getModel().addChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent e) {
				if ("ModelTicks".equals(e.getPropertyName())) {
					modelTicksLabel.setText(String.format("%s", e.getNewValue()));
				}
			}
		});
		mainPanel.add(modelTicksLabel);

		// Food gathered label
		JLabel foodGatheredLabel = new JLabel("0", SwingConstants.CENTER);
		foodGatheredLabel.setBorder(BorderFactory.createTitledBorder("Food Gathered:"));
		foodGatheredLabel.setPreferredSize(new Dimension(width / 3 - 10, 50));
		controller.getModel().addChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent e) {
				if ("FoodGathered".equals(e.getPropertyName())) {
					foodGatheredLabel.setText(String.format("%s", e.getNewValue()));
				}
			}
		});
		mainPanel.add(foodGatheredLabel);

		// Play/Pause Button
		JButton playPauseButton = new JButton("Play");
		playPauseButton.setPreferredSize(new Dimension(width / 3 - 10, 60));
		playPauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if ("Play".equals(playPauseButton.getText())) {
					notifyListeners(this, "ModelRunningChanged", null, true);
					playPauseButton.setText("Pause");
				} else {
					notifyListeners(this, "ModelRunningChanged", null, false);
					playPauseButton.setText("Play");
				}

			}
		});
		mainPanel.add(playPauseButton);

		// Reset Button
		JButton resetButton = new JButton("Reset Model");
		resetButton.setPreferredSize(new Dimension(width / 3 - 10, 60));
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				notifyListeners(this, "ResetModel", null, true);
				playPauseButton.setText("Play");
				antCountInput.setValue(0);
			}
		});
		mainPanel.add(resetButton);

		// Clear Grid Button
		JButton clearGridButton = new JButton("Clear Grid");
		clearGridButton.setPreferredSize(new Dimension(width / 3 - 10, 60));
		clearGridButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Re-make Grid of the same size as before
				notifyListeners(this, "ClearGrid", null, true);
			}
		});
		mainPanel.add(clearGridButton);

		// Simulation speed slider
		JSlider modelSpeedSlider = new JSlider(1, 60, 30);
		modelSpeedSlider.setBorder(BorderFactory.createTitledBorder("Simulation Ticks/s: 30"));
		modelSpeedSlider.setPreferredSize(new Dimension(width - 30, 50));
		modelSpeedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				modelSpeedSlider.setBorder(BorderFactory
						.createTitledBorder(String.format("Simulation Ticks/s: %s", modelSpeedSlider.getValue())));

				if (!modelSpeedSlider.getValueIsAdjusting()) {
					notifyListeners(this, "ModelSpeedChanged", null, modelSpeedSlider.getValue());
				}
			}
		});
		mainPanel.add(modelSpeedSlider);

		// Show/Hide shortest path(s) Button
		JButton displayShortestPathButton = new JButton("Show Shortest Path(s)");
		displayShortestPathButton.setPreferredSize(new Dimension(width / 2 - 20, 60));
		displayShortestPathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if ("Show Shortest Path(s)".equals(displayShortestPathButton.getText())) {

					notifyListeners(this, "ShowShortestPaths", null, true);
					displayShortestPathButton.setText("Hide Shortest Path(s)");

				} else {

					notifyListeners(this, "ShowShortestPaths", null, false);
					displayShortestPathButton.setText("Show Shortest Path(s)");
				}
			}
		});
		mainPanel.add(displayShortestPathButton);

		// Toggle pheromone dissipation Button
		JButton toggleDissipationButton = new JButton("Enable Dissipation");
		toggleDissipationButton.setPreferredSize(new Dimension(width / 2 - 20, 60));
		toggleDissipationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if ("Enable Dissipation".equals(toggleDissipationButton.getText())) {

					// Enable dissipation
					notifyListeners(this, "UsingDissipationChanged", null, true);
					toggleDissipationButton.setText("Disable Dissipation");

				} else {

					// Disable dissipation
					notifyListeners(this, "UsingDissipationChanged", null, false);
					toggleDissipationButton.setText("Enable Dissipation");
				}
			}
		});
		mainPanel.add(toggleDissipationButton);

	}

	public JFrame getFrame() {
		return frame;
	}
}
