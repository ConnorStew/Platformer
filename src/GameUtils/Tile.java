package GameUtils;

import game2D.Animation;
import game2D.Sprite;

import java.awt.*;

public class Tile extends Sprite {
	private Image tileImage;
	private final float tileWidth;
	private final float tileHeight;
	public final char imageChar;
	private final int tileX;
	private final int tileY;

	Tile(Image tileImage, float tileWidth, float tileHeight, char imageChar, int x, int y) {
		this.tileImage = tileImage;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.imageChar = imageChar;
		this.x = x * tileWidth;
		this.y = y * tileHeight;
		this.tileX = x;
		this.tileY = y;
	}

	public Image getImage() {
		return tileImage;
	}

	public int getWidth() {
		return (int) tileWidth;
	}

	public int getHeight() {
		return (int) tileHeight;
	}

	@Override
	public String toString() {
		return (tileX + ", " + tileY);
	}
}
