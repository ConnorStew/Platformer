import game2D.GameCore;
import GameUtils.TileMap;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Platformer extends GameCore {

	private static int SCREEN_WIDTH = 1000;
	private static int SCREEN_HEIGHT = 1000;
	private TileMap tileMap = new TileMap("newMap.txt");

	public static void main(String[] args) {
		Platformer platformer = new Platformer();
		platformer.init();
		platformer.run(false, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	private void init() {
		System.out.println(tileMap);
	}



	@Override
	public void draw(Graphics2D g) {
		// Be careful about the order in which you draw objects - you
		// should draw the background first, then work your way 'forward'

		// First work out how much we need to shift the view
		// in order to see where the player is.
		int xo = 0;
		int yo = 0;

		// Apply offsets to tile map and draw  it
		tileMap.draw(g,xo,yo);
	}

}