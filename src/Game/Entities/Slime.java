package Game.Entities;

import Game.Animation;
import Game.Sprite;

import java.util.Collection;
import java.util.Random;

/**
 * This is a simply enemy that will move/slide randomly along the same line of tiles.<br>
 * The slime cannot make any movement that would cause it to fall.<br>
 * The slime will target the player if it can see it.
 * @author Connor Stewart
 */
public class Slime extends Sprite {

	/** Used for randomising movement. */
	private Random random = new Random();

	/** The starting move speed when a slime slides. */
	private static final float MOVE_SPEED = 5.5f;

	/** A slimes friction while sliding. */
	private static final float FRICTION = 0.2f;

	/**
	 * Creates a new slime at the given world position.
	 * @param tiles tiles the slime must interact with
	 * @param x the slimes starting x coordinate
	 * @param y the slimes starting y coordinate
	 */
	public Slime(Collection<Tile> tiles, int x, int y) {
		super(tiles,x,y,-20,-10,10,12);
		Animation idle = new Animation(true);
		idle.loadAnimationFromSheet("images/slime/greenIdle.png", 10, 1, 100);
		setAnimation(idle);
	}

	@Override
	public void update(float elapsedTime) {
		super.update(elapsedTime);

		//apply friction
		if (dx != 0)
			if (dx > 0)
				dx = (dx - FRICTION < 0) ? 0 : (dx -= FRICTION);
			else
				dx = (dx + FRICTION > 0) ? 0 : (dx += FRICTION);

		if (dx == 0) {
			boolean moveRight = random.nextBoolean(); //random between moving left or right
			if (player != null && canSee(player)) //if the player is in the slimes vision
				if (player.getY() + height <= y + height) //if the players position is higher or the same compared to the slime
					moveRight = player.getX() > x;

			if (moveRight)
				dx = MOVE_SPEED * random.nextFloat();
			else
				dx = -(MOVE_SPEED * random.nextFloat());
		}

		//apply gravity whenever
		moveSprite(0, dy);

		//only move the slime if there will be a tile under it
		float checkX = x + dx;
		float checkY = y + dy + rectHeight + 1; //check one pixel under the left hand side of the sprite
		for (Tile tile: tiles) {
			if (checkX > tile.getX() && checkX < tile.getX() + tile.getWidth()) {
				if (checkY > tile.getY() && checkY < tile.getY() + tile.getHeight()) {
					moveSprite(dx, 0);
					break;
				}
			}
		}
	}
}