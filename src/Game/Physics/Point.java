package Game.Physics;

/**
 * Simple point class to hold a float x/y.
 * @author Connor Stewart
 */
public class Point {

	/** This points x coordinate. */
	public float x;

	/** This points y coordinate. */
	public float y;

	/**
	 * Creates a new point with a given x and y coordinate set.
	 * @param x the points x coordinate
	 * @param y the points y coordinate
	 */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[x: " + x + ", y:" + y + "]";
	}
}
