package GameUtils;

import game2D.Animation;
import game2D.Sprite;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

enum PlayerState {
    STANDING, WALKING, FALLING, JUMPING;

    public boolean isInAir() {
        return (this == FALLING || this == JUMPING);
    }
}

public class Player extends Sprite {

    /** The dy value to set when the player jumps. */
    private static final float JUMP_SPEED = 8.75f;

    /** The speed that the player walks at. */
    private static final float WALK_SPEED = 4.3f;

    /** The gravity which affects the player. */
    private static final float GRAVITY_INCREASE = 0.5f;
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

    private int xOffSet = 15;
    private int yOffSet = 5;
    private float rectX = 20;
    private float rectY = 20;
    private float rectWidth = 15;
    private float rectHeight = 32;

	/**
	 * Constructs a player which interacts with a given set of tiles.
	 * @param tiles the tiles that the player will interact with
	 */
    public Player(Collection<Tile> tiles) {
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
		    state = PlayerState.FALLING;
    }

	@Override
	public void draw(Graphics2D g) {
    	if (!facingRight)
    		drawFlipped(g);
    	else
    		super.draw(g);

//    	g.setColor(Color.RED);
//    	g.drawRect(Math.round(rectX), Math.round(rectY), Math.round(rectWidth), Math.round(rectHeight));
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

			if (rectX < tileX) { //right collision
				//System.out.println("right collision!");
                rectX = collidedTile.getX() - rectWidth;
			} else if (rectX > tileX) { //left collision
				//System.out.println("left collision!");
                rectX = tileX + tileWidth;
			}
		} else {
			//System.out.println("Setting x to " + xIncrease);
            rectX = rectX + xIncrease;
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
                rectY = tileY - rectHeight;
				if (dx != 0)
					state = PlayerState.WALKING;
				else
					state = PlayerState.STANDING;
			} else if (rectY > tileY) { //bottom collision
				//System.out.println("bottom collision!");
                rectY = tileY + tileHeight;
				dy = 0;
				state = PlayerState.FALLING;
			}
		} else {
			System.out.println("Setting y to " + yIncrease);
            rectY = rectY + yIncrease;
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
}
