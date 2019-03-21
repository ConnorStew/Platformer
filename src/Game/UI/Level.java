package Game.UI;

import Game.Camera;
import Game.Entities.Player;
import Game.Entities.Slime;
import Game.Entities.Tile;
import Game.Map;
import com.sun.javaws.exceptions.InvalidArgumentException;
import Game.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * This class represents a single instance of a game level.
 * @author Connor Stewart
 */
public class Level extends JFrame implements KeyListener {

	/** The display name of this level. */
	private final String name;

	/** The location of this levels tile map. */
	private final String tileMapLocation;

	/** The map containing tiles and sprites. */
	private Map tileMap;

	/** The player controlled by the user. */
	private Player player;

	/** THe camera used to draw the scene. */
	private Camera cam;

	/** The current frames per second. */
	private int fps;

	/** A list of keys currently being pressed. */
	private ArrayList<String> keysDown = new ArrayList<>();

	/** A list of sprites that have been marked for removal. */
	private ArrayList<Sprite> removalQueue = new ArrayList<>();

	/**
	 * Creates a new level with a given name and sets the location of its tilemap.
	 * @param name the levels name
	 * @param tileMapLocation the path of the tilemap the level will use
	 */
	public Level(String name, String tileMapLocation) {
		this.name = name;
		this.tileMapLocation = tileMapLocation;
	}

	/**
	 * Initialises the game and loads the map.
	 * @param fullscreen whether to display the level in fullscreen
	 */
	void load(boolean fullscreen) {
		try {
			tileMap = new Map(tileMapLocation, this);
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}

		cam = new Camera(this,600,338, tileMap.getWidth(), tileMap.getHeight(), fullscreen);
		cam.setBackground(new ImageIcon("images/background.png").getImage());

		player = tileMap.getPlayer();

		if (fullscreen) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setUndecorated(true);
		} else {
			setSize(1000, 500);
		}

		setVisible(true);
		addKeyListener(this);

		setFont(new Font("Dialog", Font.PLAIN, 30));

		cam.follow(player);
		fixedTimeLoop();
	}

    /**
     * Updates the games sprites.
     * @param elapsedTime the time since the last update
     */
	private void update(long elapsedTime) {
		player.update(elapsedTime, keysDown);

		tileMap.getSprites().removeAll(removalQueue);
		removalQueue.clear();

		for (Sprite sprite : tileMap.getSprites()) {
			sprite.update(elapsedTime);

			if (player != null)
				if (sprite.collides(player))
					if (player.collided(sprite))
						removalQueue.add(sprite);
		}
	}

    /**
     * Draws the games sprites.
     * @param alpha the time between game updates, normalised (0 to 1)
     * @param elapsedTime the time since the last draw
     */
	private void draw(float alpha, long elapsedTime) {
		for (Tile tile : tileMap.getTiles())
			tile.draw(cam, false);

        for (Sprite sprite : tileMap.getSprites()) {
        	sprite.updateAnim(elapsedTime);
        	sprite.draw(cam, alpha, false);
		}

        player.updateAnim(elapsedTime);
		player.draw(cam, alpha, false);

		cam.getGraphics().setColor(Color.BLACK);
		cam.getGraphics().drawString("FPS: " + fps, 10, 20);
		cam.getGraphics().setColor(Color.RED);
		cam.getGraphics().drawString("Life: " + player.getLife(), 10, 40);
		cam.getGraphics().setColor(Color.ORANGE);
		cam.getGraphics().drawString("Coins: " + player.getCoins(), 10, 60);

		cam.flush();
	}

    /**
     * A fixed timestep game loop.
     */
	private void fixedTimeLoop() {
		//time stuff
		final float FRAMETIME = 1000f / 60f; //should update about 60 times per second
		final int MAX_SKIP = 5; //can skip drawing five frames before forcing an update

		int lastSecond = 0; //the last full second
		int frameCount = 0; //the total amount of frames drawn in the last full second

		long gameTimeStart = System.currentTimeMillis(); //the time when the game starts
		long frameTimeStart = System.currentTimeMillis(); //the time since the last set of updates
		long drawTimeStart = System.currentTimeMillis(); //the time since the last draw
		float accumulator = 0; //time passed since the last update in millis
		int loopCount;

		while (this.isVisible()) {
			float alpha = 0;
			loopCount = 0;

			long deltaTime = System.currentTimeMillis() - frameTimeStart; //time passed for last loop
			frameTimeStart = System.currentTimeMillis();
			accumulator += deltaTime; //add time passed last loop

            //## update ##
			while (accumulator >= FRAMETIME) { //update if enough time has passed
				if (loopCount >= MAX_SKIP) { //safety so not stuck updating forever on slower machines.
					alpha = accumulator / FRAMETIME; //the alpha is how far between updates we are when we're forced to draw, between 0 and 1.
					accumulator = 0;
					break;
				}

				accumulator -= FRAMETIME;
				update(deltaTime);
				loopCount++;
			}

			//## draw ##
			long elapsedTime = System.currentTimeMillis() - drawTimeStart; //time since last draw
			drawTimeStart = System.currentTimeMillis();
			draw(alpha, elapsedTime);

			//## calculate fps ##
			frameCount++;
			long timePassed = System.currentTimeMillis() - gameTimeStart; //time since the game started in milliseconds
			int timePassesSeconds = (int) (timePassed / 1000); //time since the game started in seconds
			if (timePassesSeconds > lastSecond) { //if a full second has passed
				fps = frameCount; //update frames drawn last second
				lastSecond = timePassesSeconds;
				frameCount = 0;
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!keysDown.contains(KeyEvent.getKeyText(e.getKeyCode()))) {
			keysDown.add(KeyEvent.getKeyText(e.getKeyCode()));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			this.dispose();

		String toRemove = KeyEvent.getKeyText(e.getKeyCode());
		ArrayList<String> removeVals = new ArrayList<>();
		for (String keyDown : keysDown) {
			if (keyDown.equals(toRemove)) {
				removeVals.add(keyDown);
			}
		}

		keysDown.removeAll(removeVals);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	/**
	 * Closes the level with a win message.
	 */
	public void win() {
		JOptionPane.showConfirmDialog(this, "You win!", "Winner winner!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}

	/**
	 * Closes the level with a loss message.
	 */
	public void lose() {
		JOptionPane.showConfirmDialog(this, "You lost!", "Game Over!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		dispose();
	}

	/**
	 * @return the display name of this level
	 */
	String getLevelName() {
		return name;
	}
}
