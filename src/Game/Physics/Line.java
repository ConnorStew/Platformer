package Game.Physics;

import Game.Entities.Tile;

import java.util.ArrayList;

/**
 * This class is used to represent a line between two points.
 * @author Connor Stewart
 */
public class Line {

	/** The points that make up this line. */
	private Point p1, p2;

	/**
	 * Creates a line using two points.
	 * @param p1 the first point
	 * @param p2 the second point
	 */
	public Line(Point p1, Point p2){
		this.p1 = p1;
		this.p2 = p2;
	}

	public Line(int x1, int y1, int x2, int y2) {
		this.p1 = new Point(x1,y1);
		this.p2 = new Point(x2,y2);
	}

	public Line(float x1, float y1, float x2, float y2) {
		this.p1 = new Point(x1,y1);
		this.p2 = new Point(x2,y2);
	}

	/**
	 * Checks if this line and another line intersect. <br>
	 * <br>Maths:
	 * <br>y1 = m1x1+c1
	 * <br>y2 = m2x2+c2
	 * <br>y = m1x+c1
	 * <br>y = m2x+c2
	 * <br>m1x+c1 = m2x+c2
	 * <br>(m1-m2)x = c2 – c1
	 * x = (c2-c1)/(m1-m2)
	 * @param toCheck the line to check against this
	 * @return null if the lines do not intersect, a Point instance of the intersection point if they do
	 */
	public Point intersects(Line toCheck) {
		Point intersection = null;

		double r_x = p2.x - p1.x; //the x increase from this lines first point to its second
		double r_y = p2.y - p1.y; //the y increase from this lines first point to its second

		double s_x = toCheck.p2.x - toCheck.p1.x; //the x increase from the toCheck line's first point to its second
		double s_y = toCheck.p2.y - toCheck.p1.y; //the y increase from the toCheck line's first point to its second

		double denominator = r_x*s_y - r_y*s_x;

		double u = ((toCheck.p1.x - p1.x)*r_y - (toCheck.p1.y - p1.y)*r_x) / denominator;
		double t = ((toCheck.p1.x -  p1.x)*s_y - (toCheck.p1.y - p1.y)*s_x) / denominator;

		if(t >= 0 && t <= 1 && u >= 0 && u <= 1){
			intersection = new Point((float)(p1.x + t*r_x), (float) (toCheck.p1.y + u*s_y));
		}

		return intersection;
	}

	/**
	 * Checks if this line intersects the given tile.
	 * @param tile the tile to check
	 * @return whether this line intersects the tile
	 */
	public boolean intersects(Tile tile) {
		ArrayList<Line> lines = new ArrayList<>();
		lines.add(new Line(new Point(tile.getX(), tile.getY()), new Point(tile.getX(), tile.getY() + tile.getHeight()))); //l1
		lines.add(new Line(new Point(tile.getX(), tile.getY()), new Point(tile.getX() + tile.getWidth(), tile.getY()))); //l2
		lines.add(new Line(new Point(tile.getX() + tile.getWidth(), tile.getY()), new Point(tile.getX() + tile.getWidth(), tile.getY() + tile.getHeight()))); //l3
		lines.add(new Line(new Point(tile.getX(),tile.getY() + tile.getHeight()), new Point(tile.getX() + tile.getWidth(), tile.getY() + tile.getHeight()))); //l4

		boolean intersected = false;
		for (Line line : lines)
			if (this.intersects(line) != null)
				intersected = true;

		return intersected;
	}

	/**
	 * @return the first point
     */
	public Point getP1() {
		return p1;
	}

	/**
	 * @return the second point
	 */
	public Point getP2() {
		return p2;
	}
}
