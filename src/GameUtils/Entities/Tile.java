package GameUtils.Entities;

import GameUtils.Camera;
import GameUtils.Physics.Line;
import GameUtils.Physics.Point;
import game2D.Sprite;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * This class represents a single tile in the tile map.
 * @author Connor Stewart
 */
public class Tile {

	/** The tiles world x coordinate. */
	private final float worldX;

	/** The tiles world y coordinate. */
	private final float worldY;

	/** The tiles image. */
	private Image tileImage;

	/** The width of the tile. */
	private final float tileWidth;

	/** The height of the tile. */
	private final float tileHeight;

	/** The tiles x coordinate. */
	private final int tileX;

	/** The tiles y coordinate. */
	private final int tileY;

	/** Array of lines used to test line of sight for slimes. */
	private ArrayList<Line> lines;

	public Tile(Image tileImage, float tileWidth, float tileHeight, int x, int y) {
		this.tileImage = tileImage;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.worldX = x * tileWidth;
		this.worldY = y * tileHeight;
		this.tileX = x;
		this.tileY = y;

		lines = new ArrayList<>(Arrays.asList(
			new Line(new Point(worldX, worldY), new Point(worldX, worldY + tileHeight)),
			new Line(new Point(worldX, worldY), new Point(worldX + tileWidth, worldY)),
			new Line(new Point(worldX + tileWidth, worldY), new Point(worldX + tileWidth, worldY + tileHeight)),
			new Line(new Point(worldX, worldY + tileHeight), new Point(worldX + tileWidth, worldY + tileHeight))
		));
	}

	/**
	 * Draws this tile.
	 * @param cam the camera to draw using
	 * @param drawLines whether to draw the collision lines of this tile
	 */
	public void draw(Camera cam, boolean drawLines) {
		Graphics2D g = cam.getGraphics();
		Point camCords = cam.toCameraCoordinates(new Point(worldX, worldY));
		g.drawImage(tileImage, (int)camCords.x,  (int)camCords.y, (int)tileWidth, (int)tileHeight, null);

		if (drawLines) {
			for (Line line : lines) {
				g.setColor(Color.MAGENTA);
				Point p1 = cam.toCameraCoordinates(line.getP1());
				Point p2 = cam.toCameraCoordinates(line.getP2());

				g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
			}
		}
	}

	public int getWidth() {
		return (int) tileWidth;
	}

	public int getHeight() {
		return (int) tileHeight;
	}

	public float getX() {
		return worldX;
	}

	public float getY() {
		return worldY;
	}

	@Override
	public String toString() {
		return (tileX + ", " + tileY);
	}
}
