import GameUtils.*;
import com.sun.javaws.exceptions.InvalidArgumentException;
import game2D.GameCore;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Platformer extends GameCore {

	private static int SCREEN_WIDTH = 1200;
	private static int SCREEN_HEIGHT = 600;
	private TiledTileMap tileMap;
	private Image background;
	private Player player;
	private ArrayList<PortalProjectile> projectiles = new ArrayList<>();

	public static void main(String[] args) {
		Platformer platformer = new Platformer();
		platformer.init();
		platformer.run(false, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	private void init() {
		System.out.println(tileMap);
		background = new ImageIcon("images/background0.png").getImage();

		try {
			tileMap = new TiledTileMap("D:\\Year3\\Game Dev\\Assignment\\maps\\map2.json");
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}

		player = new Player(tileMap.getTiles());

	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		Point mousePos = getContentPane().getMousePosition();
		if (mousePos == null)
			mousePos = new Point(0,0);

		if (buttonsDown.contains("1")) { //left click
			float xDist = mousePos.x - player.getCenterX();
			float yDist = mousePos.y - player.getCenterY();
			double magnitude = Math.sqrt(Math.pow(mousePos.x - player.getCenterX(), 2) + Math.pow(mousePos.y - player.getCenterY(), 2));

			double normalX = xDist / magnitude;
			double normalY = yDist / magnitude;

			projectiles.add(new PortalProjectile(tileMap.getTiles(), player.getCenterX(), player.getCenterY() - 5, PortalColour.ORANGE, normalX, normalY));
		}

		player.update(elapsedTime, keysDown, buttonsDown, mousePos.x, mousePos.y);
		ArrayList<PortalProjectile> toRemove = new ArrayList<>();
		for (PortalProjectile projectile : projectiles)
			if (projectile.update())
				toRemove.add(projectile);

		projectiles.removeAll(toRemove);
		toRemove.clear();
	}

	@Override
	public void draw(Graphics2D g) {
		// Be careful about the order in which you draw objects - you
		// should draw the background first, then work your way 'forward'

		// First work out how much we need to shift the view
		// in order to see where the player is.
		int xo = player.getCenterX() - getWidth() /2;
		int yo = player.getCenterY() - getHeight() /2;

		g.drawImage(background, 0,0, null);

		// Apply offsets to tile map and draw  it
		tileMap.draw(g, xo, yo);
		player.draw(g, getWidth() /2, getHeight() /2);

		for (PortalProjectile projectile : projectiles) {
			projectile.draw(g);
		}

		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(player.getState()), 10,10);
	}
}
