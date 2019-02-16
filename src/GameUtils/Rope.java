package GameUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class Rope {

	private final static float speed = 10f;

	private float startX;
	private float startY;
	private float targetX;
	private float targetY;

	private Point hitPoint = null;

	public Rope(Collection<Tile> tiles, float startX, float startY, float targetX, float targetY) {
		this.startX = startX;
		this.startY = startY;
		this.targetX = targetX;
		this.targetY = targetY;

		double lowestDistance = Double.MAX_VALUE;
		//check if the rope has hit a tile
		for (Tile tile : tiles) {
			float rx = tile.getX();
			float ry = tile.getY();
			float rw = tile.getWidth();
			float rh = tile.getHeight();

			ArrayList<Point> points = new ArrayList<>();
			points.add(collidingLines(startX,startY,targetX,targetY, rx,ry,rx, ry+rh)); //left
			points.add(collidingLines(startX,startY,targetX,targetY, rx+rw,ry, rx+rw,ry+rh)); //right
			points.add(collidingLines(startX,startY,targetX,targetY, rx,ry, rx+rw,ry)); //top
			points.add(collidingLines(startX,startY,targetX,targetY, rx,ry+rh, rx+rw,ry+rh)); //bottom

			for (Point point : points) {
				if (point != null) {
					double distance = lineDistance(point.x, point.y, startX, startY);
					if (distance < lowestDistance) {
						hitPoint = point;
						lowestDistance = distance;
					}
				}
			}
		}
	}

	double lineDistance(float x1, float y1, float x2, float y2) {
		return Math.sqrt(Math.pow((x2 - x1),2) + Math.pow((y2 - y1),2));
	}

	static Point rotatePoint(float centerX, float centerY, double angle, float point2x, float point2y) {
		double newX = centerX + (point2x-centerX)*Math.cos(Math.toRadians(angle)) - (point2y-centerY)*Math.sin(Math.toRadians(angle));
		double newY = centerY + (point2x-centerX)*Math.sin(Math.toRadians(angle)) + (point2y-centerY)*Math.cos(Math.toRadians(angle));

		return new Point((float)newX, (float)newY);
	}

	/**
	 *
	 * @param x1 first lines origin x
	 * @param y1 first lines origin y
	 * @param x2 first lines target x
	 * @param y2 first lines target y
	 * @param x3 second lines origin x
	 * @param y3 second lines origin y
	 * @param x4 second lines target x
	 * @param y4 second lines target y
	 * @return the point in which the lines intersect or null if not
	 *
	 * From:
	 * http://www.jeffreythompson.org/collision-detection/line-rect.php
	 */
	Point collidingLines(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		// calculate the direction of the lines
		float uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
		float uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

		// if uA and uB are between 0-1, lines are colliding
		if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
			float intersectionX = x1 + (uA * (x2-x1));
			float intersectionY = y1 + (uA * (y2-y1));

			return new Point(intersectionX, intersectionY);
		}

		return null;
	}

	public void draw(Graphics2D g) {
		if (hitPoint != null) {
			g.setColor(Color.MAGENTA);
			g.drawRoundRect(Math.round(hitPoint.x) - 5, Math.round(hitPoint.y) - 5, 10,10, 10, 10);
			g.drawLine(Math.round(startX), Math.round(startY), Math.round(hitPoint.x), Math.round(hitPoint.y));

			g.setColor(new Color(156, 115, 115));
			Point playerPoint = new Point(startX, startY);
			playerPoint = rotatePoint(targetX, targetY, 1, playerPoint.x, playerPoint.y);
			g.drawRoundRect(Math.round(playerPoint.x) - 5, Math.round(playerPoint.y) - 5, 10,10, 10, 10);
		} else {
			g.setColor(Color.RED);
			g.drawLine(Math.round(startX), Math.round(startY), Math.round(targetX), Math.round(targetY));
		}
	}

	public Point getHit() {
		return hitPoint;
	}

	public Point swing(Player player, float speed) {
		return rotatePoint(targetX, targetY, speed, player.getX(), player.getY());
	}
}
