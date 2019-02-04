package GameUtils;

import game2D.Animation;
import game2D.Sprite;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

enum PlayerState {
    STANDING, RUNNING_LEFT, RUNNING_RIGHT, FALLING, JUMPING
}

public class Player extends Sprite {

    private static final float JUMP_SPEED = 10.75f;
    private static final float WALK_SPEED = 2.3f;
    private static final float X_JUMP_BOOST = 0.0f;
    private static final float FALLING_CONTROL_SPEED = 0.1f;
    private static final float GRAVITY = 0.5f;

    private boolean leftRunDown = false;
    private boolean rightRunDown = false;

    private static Collection<Tile> tiles;

    private PlayerState state = PlayerState.FALLING;

    public Player(Collection<Tile> tiles) {
        super(new Animation());
        this.tiles = tiles;
        getAnimation().loadAnimationFromSheet("images/dino/dino_walk_normal.png",8,1,100);
    }

    public void update(long elapsedTime, ArrayList<Integer> keyPresses, ArrayList<Integer> keyReleases) {
        update(elapsedTime);

        for (int keyPress : keyPresses) {
            if (keyPress == KeyEvent.VK_D) {
                rightRunDown = true;
                leftRunDown = false;
            }


            if (keyPress == KeyEvent.VK_A) {
                leftRunDown = true;
                rightRunDown = false;
            }


            if (keyPress == KeyEvent.VK_SPACE) {
                if (state == PlayerState.STANDING || state == PlayerState.RUNNING_LEFT || state == PlayerState.RUNNING_RIGHT) {
                    dy = -JUMP_SPEED;
                    if (dx < 0)
                        dx -= (Math.abs(dx) * X_JUMP_BOOST);
                    else
                        dx += (Math.abs(dx) * X_JUMP_BOOST);

                    state = PlayerState.FALLING;
                }
            }
        }

        if (rightRunDown)
            if (state == PlayerState.STANDING || state == PlayerState.RUNNING_LEFT)
                state = PlayerState.RUNNING_RIGHT;
            else if (state == PlayerState.FALLING || state == PlayerState.JUMPING)
                dx += FALLING_CONTROL_SPEED;

        if(leftRunDown)
            if (state == PlayerState.STANDING || state == PlayerState.RUNNING_RIGHT)
                state = PlayerState.RUNNING_LEFT;
            else if (state == PlayerState.FALLING || state == PlayerState.JUMPING)
                dx -= FALLING_CONTROL_SPEED;


        for (int keyRelease : keyReleases) {
            if (keyRelease == KeyEvent.VK_D && state == PlayerState.RUNNING_RIGHT) {
                state = PlayerState.STANDING;
                rightRunDown = false;
                leftRunDown = false;
            }

            if (keyRelease == KeyEvent.VK_A && state == PlayerState.RUNNING_LEFT) {
                state = PlayerState.STANDING;
                leftRunDown = false;
                rightRunDown = false;
            }
        }

        switch (state) {
            case FALLING: //apply gravity
                dy += GRAVITY;
                break;
            case STANDING:
                dy = 0;
                dx = 0;

                //check for a tile below, if not start falling
                if (colliding(0, 2) == null)
                    state = PlayerState.FALLING;

                break;
            case RUNNING_LEFT:
                dx = -WALK_SPEED;

                //check for a tile below, if not start falling
                if (colliding(0, 2) == null)
                    state = PlayerState.FALLING;

                break;
            case RUNNING_RIGHT:
                dx = WALK_SPEED;

                //check for a tile below, if not start falling
                if (colliding(0, 2) == null)
                    state = PlayerState.FALLING;

                break;
        }
    }

    @Override
    public void update(long elapsedTime) {
        super.update(elapsedTime);
        Tile collidedTile;
        float xIncrease =  dx;// * elapsedTime;
        float yIncrease = dy;// * elapsedTime;

        if (xIncrease == 0 && yIncrease == 0)
            return;

        //System.out.println(String.valueOf(state));
        //System.out.println("xi:" + xIncrease);
        //System.out.println("yi:" + yIncrease);

//        float newX = x + xIncrease;
//        float newY = y + yIncrease;

//        x += dx * elapsedTime;
//        y += dy * elapsedTime;

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
                state = PlayerState.STANDING;
            } else if (y > tileY) { //bottom collision
                //System.out.println("bottom collision!");
                y = tileY + getHeight();
				dy = 0;
                if (state == PlayerState.JUMPING) {
					state = PlayerState.FALLING;

				}

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
