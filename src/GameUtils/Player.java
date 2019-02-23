package GameUtils;

import game2D.Animation;
import game2D.Sprite;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;

enum PlayerState {
    STANDING, WALKING, FALLING, JUMPING;
}

public class Player extends Sprite {

    /** The dy value to set when the player jumps. */
    private static final float JUMP_SPEED = 8.75f;

    /** The speed that the player walks at. */
    private static final float WALK_SPEED = 3.3f;

    /** The increase in dy per update. */
    private static final float GRAVITY_INCREASE = 0.5f;

    /** The maximum value of dy. */
	private static final float GRAVITY_MAX = 9.8f;

    /** The state the player is currently in. */
    private PlayerState state = PlayerState.FALLING;

    /** The tiles this player must interact with. */
	private Collection<Tile> tiles;

	/** Whether the sprite is currently facing right. */
	private boolean facingRight = true;

	private int centerX = 0;
	private int centerY = 0;

	//animations
	private Animation runAnim = new Animation();
	private Animation idleAnim = new Animation();
	private Animation jumpAnim = new Animation();
	private Animation fallAnim = new Animation();

	//collision mask properties
    private int xOffSet = 15;
    private int yOffSet = 5;
    private float rectX = 20;
    private float rectY = 20;
    private float rectWidth = 15;
    private float rectHeight = 32;

    private PlayerState lastState = PlayerState.FALLING;
    private static final float FALLING_ALLOWANCE = 20;
    private long timeSinceOnGround = 0;
	private int mouseX = 0;
	private int mouseY = 0;

	/**
	 * Constructs a player which interacts with a given set of tiles.
	 * @param tiles the tiles that the player will interact with
	 */
    public Player(Collection<Tile> tiles) {
    	super(tiles);
        this.tiles = tiles;

		idleAnim.loadAnimationFromSheet("images/char/idle.png",3,1,250);
		runAnim.loadAnimationFromSheet("images/char/run.png",6,1,50);
		jumpAnim.loadAnimationFromSheet("images/char/jump.png",4,1,50);
		fallAnim.loadAnimationFromSheet("images/char/fall.png",2,1,50);

		setAnimation(fallAnim);
    }

	public void update(long elapsedTime, ArrayList<String> keysDown, ArrayList<String> buttonsDown, int mouseX, int mouseY) {
//		System.out.println("\n" + state);
//		System.out.println(dx + ", " + dy);

        update(elapsedTime);
        timeSinceOnGround += elapsedTime;
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        dx = 0;

		centerX = Math.round(x) + getWidth() / 2;
		centerY = Math.round(y) + getHeight() / 2;

        if (dy < GRAVITY_MAX)
			dy = (dy + GRAVITY_INCREASE > GRAVITY_MAX) ? GRAVITY_MAX : (dy += GRAVITY_INCREASE);

        for (String keyText : keysDown) {
        	switch (keyText) {
        		case "Space":
        			if (state == PlayerState.FALLING && timeSinceOnGround < FALLING_ALLOWANCE || state != PlayerState.FALLING && state != PlayerState.JUMPING) {
        				dy = -JUMP_SPEED;
        				changeState(PlayerState.JUMPING);
        			}
        			break;
        		case "D":
        			dx = WALK_SPEED;
					facingRight = true;
					if (state != PlayerState.FALLING && state != PlayerState.JUMPING)
						changeState(PlayerState.WALKING);

					break;
				case "A":
					dx = -WALK_SPEED;
					facingRight = false;

					if (state != PlayerState.FALLING && state != PlayerState.JUMPING)
						changeState(PlayerState.WALKING);

					break;
			}
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
	 * Changes the state of the player and records the last state.
	 * @param newState the new state of the player
	 */
	private void changeState(PlayerState newState) {
    	lastState = state;
    	state = newState;
	}

	@Override
	public void draw(Graphics2D g, int x, int y) {
		//being drawn by camera now for some reason
		System.out.println("Drawing at: x:" + x + ", y:" + y);
    	if (!facingRight)
			g.drawImage(getImage(), x + getWidth() / 2, y - getHeight() / 2, (int)-width, (int)height, null);
    	else
			g.drawImage(getImage(), x - getWidth() / 2, y - getHeight() / 2, (int)width, (int)height, null);
	}

	private void movePlayer(float xIncrease, float yIncrease) {
		Tile collidedTile;
		if (xIncrease == 0 && yIncrease == 0)
			return;

		float newRectX = 0;
		float newRectY = 0;

		collidedTile = colliding(xIncrease, 0);
		//if moving the x axis caused a collision
		if (collidedTile != null) {
			//System.out.println("x colliding tile : " + collidedTile);
			//System.out.println(collidedTile.getHeight());
			float tileX = collidedTile.getX();
			float tileWidth = collidedTile.getWidth();

			if (rectX < tileX) { //right collision
				//System.out.println("right collision!");
				newRectX = collidedTile.getX() - rectWidth;
			} else if (rectX > tileX) { //left collision
				//System.out.println("left collision!");
				newRectX = tileX + tileWidth;
			}
		} else {
			//System.out.println("Setting x(" + x + ") to " + xIncrease);
            newRectX = rectX + xIncrease;
		}

		collidedTile = colliding(0, yIncrease);
		//if moving the y axis caused a collision
		if (collidedTile != null) {
			//System.out.println("y colliding tile : " + collidedTile);
			float tileY = collidedTile.getY();
			float tileHeight = collidedTile.getHeight();

			//move along y axis
			if (rectY < tileY) { //top collision
				//System.out.println("top collision!");
				newRectY = tileY - rectHeight;
                timeSinceOnGround = 0;
				if (dx != 0)
					changeState(PlayerState.WALKING);
				else
					changeState(PlayerState.STANDING);
			} else if (rectY > tileY) { //bottom collision
				//System.out.println("bottom collision!");
				newRectY = tileY + tileHeight;
				dy = 0;
				changeState(PlayerState.FALLING);
			}
		} else {
			//System.out.println("Setting y to " + yIncrease);
            newRectY = rectY + yIncrease;
		}

		if (newRectX != 0) {
			rectX = newRectX;
			//System.out.println("Setting x(" +newRectX + ")");
		}

		if (newRectY != 0) {
			rectY = newRectY;
			//System.out.println("Setting y(" +newRectY + ")");
		}

		x = rectX - xOffSet;
		y = rectY - yOffSet;
	}

    private Tile colliding(float xIncrease, float yIncrease) {
        float textX = rectX + xIncrease;
        float testY = rectY + yIncrease;
        float playerWidth = rectWidth;
        float playerHeight = rectHeight;

        for (Tile tile : tiles) {
            if (tile.getX() < textX + playerWidth &&
                    tile.getX() + tile.getWidth() > textX &&
                    tile.getY() < testY + playerHeight &&
                    tile.getHeight() + tile.getY() > testY) {
                return tile;
            }
        }
        return null;
    }

    public PlayerState getState() {
        return state;
    }

	public int getCenterY() {
		return centerY;
	}

	public int getCenterX() {
		return centerX;
	}

	public boolean isFacingRight() {
		return facingRight;
	}
}
