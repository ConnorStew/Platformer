package GameUtils;

import game2D.Animation;
import game2D.Sprite;

import java.awt.*;
import java.util.Collection;

public class Tile extends Sprite {
	private Image tileImage;
	private final float tileWidth;
	private final float tileHeight;
	private final int tileX;
	private final int tileY;
	private Color drawColour = null;

	Tile(Collection<Tile> tiles, Image tileImage, float tileWidth, float tileHeight, int x, int y) {
		super(tiles);
		this.tileImage = tileImage;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
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

	public void draw(Graphics2D g) {
		g.drawImage(getImage(),(int)x,(int)y,null);
		if (drawColour != null) {
			g.setColor(drawColour);
			g.drawRect((int)x,(int)y,(int)width,(int)height);
		}
	}

	@Override
	public String toString() {
		return (tileX + ", " + tileY);
	}

	public void setDrawColour(Color drawColour) {
		this.drawColour = drawColour;
	}
}
