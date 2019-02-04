import GameUtils.Player;
import GameUtils.TileMap;
import game2D.GameCore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Platformer extends GameCore {

	private static int SCREEN_WIDTH = 1000;
	private static int SCREEN_HEIGHT = 1000;
	private TileMap tileMap = new TileMap("newMap.txt");
	private Image background;
	private Player player = new Player(tileMap.getTiles());

	public static void main(String[] args) {
		Platformer platformer = new Platformer();
		platformer.init();
		platformer.run(false, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	private void init() {
		System.out.println(tileMap);

		background = new ImageIcon("images/background0.png").getImage();

		player.setX(40);
		player.setY(60);
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		player.update(elapsedTime, keyPresses, keyReleases, keysDown);
		keyPresses.clear();
		keyReleases.clear();
	}

	@Override
	public void draw(Graphics2D g) {
		// Be careful about the order in which you draw objects - you
		// should draw the background first, then work your way 'forward'

		// First work out how much we need to shift the view
		// in order to see where the player is.
		int xo = 0;
		int yo = 0;

		g.drawImage(background, 0,0, null);

		// Apply offsets to tile map and draw  it
		tileMap.draw(g,xo,yo);
		player.draw(g);

		g.drawString(String.valueOf(player.getState()), 10,10);
	}
}
