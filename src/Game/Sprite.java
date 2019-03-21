package Game;

import Game.Physics.Line;
import Game.Entities.Player;
import Game.Entities.Tile;
import Game.Physics.Point;

import java.awt.*;
import java.util.Collection;

/**
 * This is the base class for all sprites in the game.
 * @author Connor Stewart
 * @author David Cairns
 */
public class Sprite {

	/** The tiles this sprite must interact with. */
    protected final Collection<Tile> tiles;

    /** The sprites animation. */
    private Animation anim;

	/** The increase in dy per update. */
	protected static final float GRAVITY_INCREASE = 0.5f;

	/** The maximum value of dy. */
	private static final float GRAVITY_MAX = 9.8f;

	//collision mask properties

	/** This sprites collision rectangles x coordinate. */
	protected float x;

	/** This sprites collision rectangles y coordinate. */
	protected float y;

	/** The x offset of the collision rectangle. */
	protected final int xOffSet;

	/** The y offset of the collision rectangle. */
	protected final int yOffSet;

	/** The width of the rectangle. */
	protected final float rectWidth;

	/** The height of the rectangle. */
	protected final float rectHeight;

	/** The x coordinate of the sprite last update. */
	protected float lastX = 0;

	/** The y coordinate of the sprite last update. */
	protected float lastY = 0;

	/** Whether to draw the sprite flipped. */
	protected boolean drawFlipped = false;

	/**
	 * The player controlled by the user.<br>
	 * This property is used to notify the player that a sprite has collided with it.
	 */
	protected Player player;

    // Velocity (pixels per millisecond)
    protected float dx;
    protected float dy;

    // Dimensions of the sprite
    protected float height;
    protected float width;

	public Sprite(Collection<Tile> tiles, int x, int y, int yOffSet, int xOffSet, float rectWidth, float rectHeight) {
    	this.yOffSet = yOffSet;
    	this.xOffSet = xOffSet;
    	this.rectWidth = rectWidth;
    	this.rectHeight = rectHeight;
        this.tiles = tiles;
        this.anim = new Animation(false);

        this.x = x;
        this.y = y;
    }

    protected void setAnimation(Animation a) {
    	anim = a;
    }

	/**
	 * Applies gravity to the sprite.
	 * @param elapsedTime the time elapsed since the last draw
	 */
	public void update(float elapsedTime) {
        width = anim.getImage().getWidth(null);
        height = anim.getImage().getHeight(null);

		if (dy < GRAVITY_MAX)
			dy = (dy + GRAVITY_INCREASE > GRAVITY_MAX) ? GRAVITY_MAX : (dy += GRAVITY_INCREASE);
    }

	/**
	 * Moves the sprite and its collision rectangle.
	 * @param xChange the change in x
	 * @param yChange the change in y
	 */
	protected void moveSprite(float xChange, float yChange) {
		Tile collidedTile;
		if (xChange == 0 && yChange == 0)
			return;

		float newRectX = 0;
		float newRectY = 0;

		collidedTile = colliding(xChange, 0);
		//if moving the x axis caused a collision
		if (collidedTile != null) {
			float tileX = collidedTile.getX();
			float tileWidth = collidedTile.getWidth();

			if (x < tileX) { //right collision
				newRectX = collidedTile.getX() - rectWidth;
			} else if (x > tileX) { //left collision
				newRectX = tileX + tileWidth;
			}
		} else {
			newRectX = x + xChange;
		}

		collidedTile = colliding(0, yChange);
		//if moving the y axis caused a collision
		if (collidedTile != null) {
			float tileY = collidedTile.getY();
			float tileHeight = collidedTile.getHeight();

			//move along y axis
			if (y < tileY) { //top collision
				newRectY = tileY - rectHeight;
			} else if (y > tileY) { //bottom collision
				newRectY = tileY + tileHeight;
				dy = 0;
			}
		} else {
			newRectY = y + yChange;
		}

		if (newRectX != 0) {
			x = newRectX;
		}

		if (newRectY != 0) {
			y = newRectY;
		}
	}

	/**
	 * Checks if this sprite would be colliding with a tile, given the x/y change.
	 * @param xChange the x change
	 * @param yChange the y change
	 * @return a tile that was collided with or null if no collision was found
	 */
	protected Tile colliding(float xChange, float yChange) {
		float textX = x + xChange;
		float testY = y + yChange;

		for (Tile tile : tiles) {
			if (tile.getX() < textX + rectWidth &&
					tile.getX() + tile.getWidth() > textX &&
					tile.getY() < testY + rectHeight &&
					tile.getHeight() + tile.getY() > testY) {
				return tile;
			}
		}
		return null;
	}

	/**
	 * Checks if this sprite can see another.<br>
	 * This uses lines in the tiles and a line between both sprites to see if they are in line of sight.
	 * @param other the other sprite
	 * @return whether this sprite has line of sight of the other sprite
	 */
	protected boolean canSee(Sprite other) {
		Line visionLine = new Line(x + width / 2, x + height / 2, other.x + other.width / 2, other.y + other.width / 2);

		boolean canSee = true;

		for(Tile tile : tiles) {
			if (visionLine.intersects(tile)) {
				canSee = false;
				break;
			}
		}

    	return canSee;
	}

	/**
	 * Updates this sprites animation.
	 * @param elapsedTime the time since the sprite was last drawn
	 */
    public void updateAnim(long elapsedTime) {
        anim.update(elapsedTime);
    }

	/**
	 * Checks if this sprite is colliding with another.
	 * @param other the other sprite
	 * @return whether the two sprites are colliding
	 */
	public boolean collides(Sprite other) {
		return (other.x < x + rectWidth &&
				other.x + other.rectWidth > x &&
				other.y < y + rectHeight &&
				other.rectHeight + other.y > y);
	}

	/**
	 * Draws this sprite.
	 * @param cam the camera to draw the sprite relative to
	 * @param alpha the amount (between 0 and 1) to interpolate between the positions.
	 * @param drawCollisionRectangles whether to draw this sprites collision rectangles
	 */
	public void draw(Camera cam, float alpha, boolean drawCollisionRectangles) {
		Image image = getImage();
		Graphics2D g = cam.getGraphics();

		int drawWidth = Math.round(image.getWidth(null));
		int drawHeight = Math.round(image.getHeight(null));

		Point interpolatedPoint = cam.interpolatePosition(new Game.Physics.Point(lastX, lastY), new Point(x, y), alpha);
		Point cameraPoint = cam.toCameraCoordinates(interpolatedPoint);

		saveY();
		saveX();

		int drawY = (int) cameraPoint.y;
		int drawX = (int) cameraPoint.x;

		if (drawFlipped)
			g.drawImage(image, drawX + drawWidth + xOffSet, drawY + yOffSet, -drawWidth, drawHeight, null);
		else
			g.drawImage(image, drawX + xOffSet, drawY + yOffSet, drawWidth, drawHeight, null);

		if (drawCollisionRectangles) {
			g.setColor(Color.RED);
			Point rectPoint = new Point(x, y);
			Point camRectPoint = cam.toCameraCoordinates(rectPoint);
			g.drawRect((int)camRectPoint.x, (int)camRectPoint.y, (int)rectWidth, (int)rectHeight);
		}
	}

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

	/**
	 * @return this sprites current image
	 */
	protected Image getImage() {
        return anim.getImage();
    }

    protected void saveX() {
        lastX = x;
    }

    protected void saveY() {
        lastY = y;
    }

	void setPlayer(Player player) {
		this.player = player;
	}
}
