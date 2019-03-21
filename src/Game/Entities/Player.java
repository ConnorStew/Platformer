package Game.Entities;

import Game.Camera;
import Game.Physics.Point;
import Game.Animation;
import Game.Sound.Sound;
import Game.Sprite;
import Game.UI.Level;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;

/**
 * This class represents the character the user controls.
 * @author Connor Stewart
 */
public class Player extends Sprite {

	/** The different states the player can be in. */
	enum PlayerState { STANDING, WALKING, FALLING, JUMPING }

    /** The dy value to set when the player jumps. */
    private static final float JUMP_SPEED = 8.75f;

    /** The speed that the player walks at. */
    private static final float WALK_SPEED = 3.3f;

    /** The amount of time in milliseconds that the player is immune to damage. */
    private static final float GRACE_PERIOD_TIME = 100;

    /** How many milliseconds the player can fall before not being allowed to jump anymore. */
	private static final float FALLING_ALLOWANCE = 20;

    /** The state the player is currently in. */
    private PlayerState state = PlayerState.FALLING;

	/** The level this player is in. */
	private Level level;

    /** The timer for the grace period. */
    private float gracePeriodTimer = 0;

    /** The time since the player has stood on the ground. */
	private long timeSinceOnGround = 0;

	/** Whether the player has recently taken damage. */
	private boolean inGracePeriod = false;

	/** The players currently amount of life points. */
	private int life = 50;

	/** The amount of coins the player has collected. */
	private int coins = 0;

	//sounds
	private Sound hitSound;
	private Sound jumpSound;
	private Sound coinSound;
	private Sound winSound;
	private Sound loseSound;

	//animations
	private Animation runAnim = new Animation(true);
	private Animation idleAnim = new Animation(true);
	private Animation jumpAnim = new Animation(false);
	private Animation fallAnim = new Animation(true);

	/**
	 * Constructs a player which interacts with a given set of tiles.
	 * @param tiles the tiles that the player will interact with
	 * @param x the players starting x coordinate
	 * @param y the players starting y coordinate
	 */
    public Player(Collection<Tile> tiles, Level level, int x, int y) {
    	super(tiles,x, y,-5,-17,15,32);

    	this.level = level;

		idleAnim.loadAnimationFromSheet("images/char/idle.png",3,1,300);
		runAnim.loadAnimationFromSheet("images/char/run.png",6,1,100);
		jumpAnim.loadAnimationFromSheet("images/char/jump.png",4,1,50);
		fallAnim.loadAnimationFromSheet("images/char/fall.png",2,1,100);

		hitSound = new Sound("sounds\\hit.wav", Sound.Filter.None);
		hitSound.adjustVolume(-10);

		jumpSound = new Sound("sounds\\jump.wav", Sound.Filter.None);
		jumpSound.adjustVolume(-10);

		coinSound = new Sound("sounds\\coin.wav", Sound.Filter.None);
		coinSound.adjustVolume(-10);

		winSound = new Sound("sounds\\win.wav", Sound.Filter.IncreaseOverTime);

		loseSound = new Sound("sounds\\lose.wav", Sound.Filter.None);
		loseSound.adjustVolume(-20);

		setAnimation(fallAnim);
    }

	/**
	 * Updates the player based on keys that are being pressed.
	 * @param deltaTime the time since the last update
	 * @param keysDown a list of keys currently being pressed
	 */
	public void update(long deltaTime, ArrayList<String> keysDown) {
        update(deltaTime);
        timeSinceOnGround += deltaTime;

        if (inGracePeriod) {
			gracePeriodTimer += deltaTime;

			if (gracePeriodTimer > GRACE_PERIOD_TIME) {
				inGracePeriod = false;
				gracePeriodTimer = 0;
			}
		}

        dx = 0;

        try {
			for (String keyText : keysDown) {
				switch (keyText) {
					case "Space":
						if (state == PlayerState.FALLING && timeSinceOnGround < FALLING_ALLOWANCE || state != PlayerState.FALLING && state != PlayerState.JUMPING) {
							dy = -JUMP_SPEED;
							changeState(PlayerState.JUMPING);
							jumpAnim.restart();
							jumpSound.play();
						}
						break;
					case "D":
						dx = WALK_SPEED;
						drawFlipped = false;
						if (state != PlayerState.FALLING && state != PlayerState.JUMPING)
							changeState(PlayerState.WALKING);

						break;
					case "A":
						dx = -WALK_SPEED;
						drawFlipped = true;

						if (state != PlayerState.FALLING && state != PlayerState.JUMPING)
							changeState(PlayerState.WALKING);

						break;
				}
			}
		} catch (ConcurrentModificationException ignored) {
        	//ignore
		}

		if (state != PlayerState.FALLING && state != PlayerState.JUMPING && !keysDown.contains("D") && !keysDown.contains("A"))
			changeState(PlayerState.STANDING);

		if (state == PlayerState.JUMPING && dy > 0)
			changeState(PlayerState.FALLING);

		switch (state) {
			case JUMPING:
				setAnimation(jumpAnim);
				break;
			case FALLING: //apply gravity
				setAnimation(fallAnim);
				break;
			case STANDING:
				dy = GRAVITY_INCREASE;
				setAnimation(idleAnim);
				break;
			case WALKING:
                dy = GRAVITY_INCREASE;
				setAnimation(runAnim);
				break;
		}

		float yBefore = y;
		movePlayer(dx, dy);
		float yAfter = y;

		if (yAfter > yBefore)
			changeState(PlayerState.FALLING);
    }

	/**
	 * Changes the players state to a new one.
	 * This function is not overly useful at the moment, its original purpose was to keep track of the last state of the player
	 * but this functionality is no longer needed.
	 * @param newState the new state of the player
	 */
	private void changeState(PlayerState newState) {
		state = newState;
	}

	/**
	 * Moves the player by a given x and y change.<br>
	 * Also checks for collisions with tiles.
	 * @param xChange the change in x
	 * @param yChange the change in y
	 */
	private void movePlayer(float xChange, float yChange) {
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
                timeSinceOnGround = 0;
				if (dx != 0)
					changeState(PlayerState.WALKING);
				else
					changeState(PlayerState.STANDING);
			} else if (y > tileY) { //bottom collision
				newRectY = tileY + tileHeight;
				dy = 0;
				changeState(PlayerState.FALLING);
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

	@Override
	public void draw(Camera cam, float alpha, boolean drawCollisionRectangles) {
		Image image = getImage();
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		if (imageWidth > 0 && imageHeight > 0 && inGracePeriod)
			image = makeWhiteImage(image, imageWidth, imageHeight);

		Graphics2D g = cam.getGraphics();

		Point interpolatedPoint = cam.interpolatePosition(new Point(lastX, lastY), new Point(x, y), alpha);
		Point cameraPoint = cam.toCameraCoordinates(interpolatedPoint);

		saveY();
		saveX();

		int drawY = (int) cameraPoint.y;
		int drawX = (int) cameraPoint.x;

		if (drawFlipped)
			g.drawImage(image, drawX + imageWidth + xOffSet, drawY + yOffSet, -imageWidth, imageHeight, null);
		else
			g.drawImage(image, drawX + xOffSet, drawY + yOffSet, imageWidth, imageHeight, null);

 		if (drawCollisionRectangles) {
			g.setColor(Color.RED);
			Game.Physics.Point rectPoint = new Game.Physics.Point(x, y);
			Point camRectPoint = cam.toCameraCoordinates(rectPoint);
			g.drawRect((int)camRectPoint.x, (int)camRectPoint.y, (int)rectWidth, (int)rectHeight);
		}
	}

	/**
	 * Turns all the non-transparent pixels in an image white by looping through the individual pixels, changing them white but ignoring the transparency.
	 * @param image the image to use as a base
	 * @param imageWidth the images width
	 * @param imageHeight the images height
	 * @return a white version of the image
	 */
	private Image makeWhiteImage(Image image, int imageWidth, int imageHeight) {
		BufferedImage bimage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(image, 0, 0, null);
		bGr.dispose();

		int width = bimage.getWidth();
		int height = bimage.getHeight();
		WritableRaster raster = bimage.getRaster();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int[] pixels = raster.getPixel(x, y, (int[]) null);
				pixels[0] = 255;
				pixels[1] = 255;
				pixels[2] = 255;
				raster.setPixel(x, y, pixels);
			}
		}

		return bimage;
	}

	/**
	 * Called when the player has collided with an enemy.
	 * @param collidedWith the sprite the player collided with
	 * @return whether to delete the sprite that collided with the player
	 */
	public boolean collided(Sprite collidedWith) {
		if (collidedWith instanceof Slime) {
			//only take damage if not in the grace period
			if (!inGracePeriod) {
				life -= 10;

				if (life > 0) {
					inGracePeriod = true;
					hitSound.play();
				} else {
					loseSound.play();
					level.lose();
				}
			}

			return false;
		}

		if (collidedWith instanceof Coin) {
			coinSound.play();
			coins++;
			return true;
		}

		if (collidedWith instanceof Signpost) {
			winSound.play();
			level.win();
		}

		return false;
	}

	public int getLife() {
		return life;
	}

	public int getCoins() {
		return coins;
	}
}
