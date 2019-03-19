import GameUtils.*;
import GameUtils.Entities.Player;
import GameUtils.Entities.Tile;
import com.sun.javaws.exceptions.InvalidArgumentException;
import game2D.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * This is the base class used to initialises and control the game.
 * @author Connor Stewart
 */
public class Platformer extends JFrame implements KeyListener {

    /** The map containing tiles and sprites. */
	private Map tileMap;

	/** The player controlled by the user. */
	private Player player;

	/** THe camera used to draw the scene. */
	private Camera cam;

	/** The current frames per second. */
	private int fps;

	/** Whether the game should be drawn in fullscreen. */
	private final boolean fullscreen = false;

	/** A list of keys currently being pressed. */
	private ArrayList<String> keysDown = new ArrayList<String>();

	public static void main(String[] args) {
		new Platformer().init();
	}

    /**
     * Initialises the game and loads the map.
     */
	private void init() {
		try {
			tileMap = new Map("maps\\map2.json");
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}

		cam = new Camera(this,600,338, tileMap.getWidth(), tileMap.getHeight(), fullscreen);
		cam.setBackground(new ImageIcon("images/background0.png").getImage());

		player = tileMap.getPlayer();

		if (fullscreen) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setUndecorated(true);
		} else {
			setSize(1000, 500);
		}

		setVisible(true);
        addKeyListener(this);

        setFont(new Font("Dialog", Font.PLAIN, 20));

		cam.follow(player);
		fixedTimeLoop();
	}

    /**
     * Updates the games sprites.
     * @param elapsedTime the time since the last update
     */
	private void update(long elapsedTime) {
		player.update(elapsedTime, keysDown);

		for (Sprite sprite : tileMap.getSprites())
		    sprite.update(elapsedTime);
	}

    /**
     * Draws the games sprites.
     * @param alpha the time between game updates, normalised (0 to 1)
     * @param elapsedTime the time since the last draw
     */
	private void draw(float alpha, long elapsedTime) {
		for (Tile tile : tileMap.getTiles())
			tile.draw(cam, false);

        for (Sprite sprite : tileMap.getSprites())
            sprite.draw(cam, alpha, false);

        player.updateAnim(elapsedTime);
		player.draw(cam, alpha, false);

		cam.getGraphics().setColor(Color.WHITE);
		cam.getGraphics().drawString("FPS: " + fps, 10, 20);
		cam.getGraphics().setColor(Color.RED);
		cam.getGraphics().drawString("Life: " + player.getLife(), 10, 40);

		cam.flush();
	}

    /**
     * A fixed timestep game loop.<br><br>
     *
     * Used these sites to research this loop:
     * <ul>
     *     <li>https://gafferongames.com/post/fix_your_timestep/</li>
     *     <li>https://gamedev.stackexchange.com/questions/132283/implementing-fix-your-time-step</li>
     * </ul>
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

		System.exit(0);
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
			System.exit(0);

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
}
