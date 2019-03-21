package Game.UI;

import Game.Sound.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class create a simple level selection window to show different levels.
 * @author Connor Stewart
 */
public class LevelSelect extends JFrame {

    /** The available levels. */
    private ArrayList<Level> levels;

    /** Whether the levels should be loaded in fullscreen. */
    private boolean fullscreen = false;

    /** Sound that plays when a button is pressed. */
    private Sound buttonSound;

    public LevelSelect() {
        levels = new ArrayList<>(Arrays.asList(
                new Level("Level 1", "maps\\Level1.json"),
                new Level("Level 2", "maps\\Level2.json"),
                new Level("Level 3", "maps\\Level3.json")
        ));

        buttonSound = new Sound("sounds\\button.wav", Sound.Filter.None);
        buttonSound.adjustVolume(-20);

        initJFrame();
    }

    /**
     * Creates and sets up the JFrame.
     */
    private void initJFrame() {
        setSize(200, 200);
        setLayout(new BorderLayout());
        setResizable(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = getContentPane();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridheight = GridBagConstraints.RELATIVE;

        GridBagLayout gbl = new GridBagLayout();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(gbl);
        gbl.setConstraints(buttonPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JLabel title = new JLabel("Level Select");
        title.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(title, BorderLayout.NORTH);

        for (Level level : levels) {
            String name = level.getLevelName();
            JButton button = createButton(name, gbl, gbc);

            //whenever this button is clicked its assigned level is launched
            button.addActionListener(e -> {
                buttonSound.play();
                new Thread(() -> level.load(fullscreen)).start();
            });

            buttonPanel.add(button);
        }

        setVisible(true);
    }

    /**
     * Creates a new button for use with the GridBagLayout.
     * @param text the text on the button
     * @param layout the layout to assign the button to
     * @param c the constraints of the layout
     * @return the button that was created
     */
    private JButton createButton(String text, GridBagLayout layout, GridBagConstraints c) {
        JButton button = new JButton(text);
        layout.setConstraints(button, c);
        return button;
    }
}
