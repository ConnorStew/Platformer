package GameUtils;

import java.awt.*;

public class Tile {
	private Image tileImage;
	private final float tileWidth;
	private final float tileHeight;
	public final char imageChar;

	Tile(Image tileImage, float tileWidth, float tileHeight, char imageChar) {
		this.tileImage = tileImage;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.imageChar = imageChar;
	}

	public Image getImage() {
		return tileImage;
	}

	public float getWidth() {
		return tileWidth;
	}

	public float getHeight() {
		return tileHeight;
	}
}
