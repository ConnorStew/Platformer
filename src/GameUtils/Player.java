package GameUtils;

import game2D.Animation;
import game2D.Sprite;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

enum PlayerState {
    STANDING, WALKING, FALLING, JUMPING;
}

public class Player extends Sprite {

    /** The dy value to set when the player jumps. */
    private static final float JUMP_SPEED = 17.75f;

    /** The speed that the player walks at. */
    private static final float WALK_SPEED = 4.3f;

    /** The gravity which affects the player. */
    private static final float GRAVITY_INCREASE = 1f;
	private static final float GRAVITY_MAX = 9.8f;

    /** The state the player is currently in. */
    private PlayerState state = PlayerState.FALLING;

    /** The tiles this player must interact with. */
	private Collection<Tile> tiles;

	private Animation runAnim = new Animation();
	private Animation idleAnim = new Animation();
	private Animation jumpAnim = new Animation();
	private Animation fallAnim = new Animation();
	private boolean facingRight = true;

	/**
	 * Constructs a player which interacts with a given set of tiles.
	 * @param tiles the tiles that the player will interact with
	 */
    public Player(Collection<Tile> tiles) {
        super(new Animation());
        this.tiles = tiles;

		idleAnim.loadAnimationFromSheet("images/char/idle.png",3,1,250);
		runAnim.loadAnimationFromSheet("images/char/run.png",6,1,50);
		jumpAnim.loadAnimationFromSheet("images/char/jump.png",4,1,50);
		fallAnim.loadAnimationFromSheet("images/char/fall.png",2,1,50);

		setAnimation(fallAnim);
    }

	public void update(long elapsedTime, ArrayList<Integer> keyPresses, ArrayList<Integer> keyReleases, ArrayList<String> keysDown) {
        update(elapsedTime);

        if (dy < GRAVITY_MAX)
			dy += GRAVITY_INCREASE;

        for (String keyText : keysDown) {
        	switch (keyText) {
				case "Space":
					if (state != PlayerState.FALLING && state != PlayerState.JUMPING) {
						dy = -JUMP_SPEED;
						state = PlayerState.JUMPING;
						System.out.println("jumping");
					}
					break;
				case "D":
					dx = WALK_SPEED;
					facingRight = true;

					if (state != PlayerState.FALLING && state != PlayerState.JUMPING)
						state = PlayerState.WALKING;

					break;
				case "A":
					dx = -WALK_SPEED;
					facingRight = false;

					if (state != PlayerState.FALLING && state != PlayerState.JUMPING)
						state = PlayerState.WALKING;

					break;
			}
        }

		if (state != PlayerState.FALLING && state != PlayerState.JUMPING && !keysDown.contains("D") && !keysDown.contains("A"))
			state = PlayerState.STANDING;

		if (state == PlayerState.JUMPING && dy > 0)
			state = PlayerState.FALLING;

		switch (state) {
			case JUMPING:
				setAnimation(jumpAnim);
				break;
			case FALLING: //apply gravity
				setAnimation(fallAnim);
				break;
			case STANDING:
				dx = 0;

				setAnimation(idleAnim);
				//check for a tile below, if not start falling
				if (colliding(0, 1) == null)
					state = PlayerState.FALLING;

				break;
			case WALKING:
				setRotation(90);
				setAnimation(runAnim);
				//check for a tile below, if not start falling
				if (colliding(0, 1) == null)
					state = PlayerState.FALLING;
				break;
		}

		movePlayer(dx, dy);
    }

	@Override
	public void draw(Graphics2D g) {
    	if (!facingRight)
    		drawFlipped(g);
    	else
    		super.draw(g);
	}

	private void movePlayer(float xIncrease, float yIncrease) {
		Tile collidedTile;
		if (xIncrease == 0 && yIncrease == 0)
			return;

		collidedTile = colliding(xIncrease, 0);
		//if moving the x axis caused a collision
		if (collidedTile != null) {
			//System.out.println("x colliding tile : " + collidedTile);
			//System.out.println(collidedTile.getHeight());
			float tileX = collidedTile.getX();
			float tileWidth = collidedTile.getWidth();

			if (x < tileX) { //right collision
				//System.out.println("right collision!");
				x = collidedTile.getX() - getWidth();
			} else if (x > tileX) { //left collision
				//System.out.println("left collision!");
				x = tileX + tileWidth;
			}
		} else {
			//System.out.println("Setting x to " + xIncrease);
			x += xIncrease;
		}

		collidedTile = colliding(0, yIncrease);
		//if moving the y axis caused a collision
		if (collidedTile != null) {
			//System.out.println("y colliding tile : " + collidedTile);
			float tileY = collidedTile.getY();
			float tileHeight = collidedTile.getHeight();

			//move along y axis
			if (y < tileY) { //top collision
				//System.out.println("top collision!");
				y = tileY - getHeight();
				if (dx != 0)
					state = PlayerState.WALKING;
				else
					state = PlayerState.STANDING;
			} else if (y > tileY) { //bottom collision
				//System.out.println("bottom collision!");
				y = tileY + tileHeight;
				dy = 0;
				state = PlayerState.FALLING;
			}
		} else {
			//System.out.println("Setting y to " + yIncrease);
			y += yIncrease;
		}
	}

    private Tile colliding(float xIncrease, float yIncrease) {
        float textX = x + xIncrease;
        float testY = y + yIncrease;

        for (Tile tile : tiles) {
            if (tile.imageChar == '.')
                continue;

            if (tile.getX() < textX + getWidth() &&
                    tile.getX() + tile.getWidth() > textX &&
                    tile.getY() < testY + getHeight() &&
                    tile.getHeight() + tile.getY() > testY) {

//                System.out.println(tile.getX() + " < " + textX + " + " + getWidth() + " && \n"
//                        + tile.getX() + " + " + tile.getWidth()+ " > " + textX + " && \n"
//                        + tile.getY() + " < " + testY + " + " + getHeight() + " && \n"
//                        + tile.getHeight() + " + " + tile.getY() + " > " + testY);
                return tile;
            }
        }

//        for (Tile tile : tiles) {
//            if (tile.imageChar == '.')
//                continue;
//
//            if (tile.getX() < getX() + getWidth() && tile.getX() + tile.getWidth() > getX() && tile.getY() < getY() + getHeight() && tile.getHeight() + getY() > getY()) {
//                //System.out.println("Collision with " + tile);
//                if (state == PlayerState.FALLING || state == PlayerState.JUMPING)
//                    state = PlayerState.STANDING;
//            }
//        }
        return null;
    }

    public PlayerState getState() {
        return state;
    }
}
