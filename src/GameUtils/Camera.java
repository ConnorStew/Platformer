package GameUtils;

import GameUtils.Physics.Point;
import game2D.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class controls drawing everything relative to a camera region.
 * @author Connor Stewart
 */
public class Camera {

    /** The maximum y of the camera. */
    private final int maxY;

    /** The maximum x of the camera. */
    private final int maxX;

    /** Whether the camera is displaying in fullscreen. */
    private final boolean fullscreen;

    /** The cameras x coordinate in the game world. */
    private float x = 0;

    /** The cameras y coordinate in the game world. */
    private float y = 0;

    /** The width of the cameras vision in the game world. */
    private float width;

    /** The height of the cameras vision in the game world. */
    private float height;

    /** The window to draw to. */
    private JFrame window;

    /** The cameras graphics. */
    private Graphics2D g;

    /** The image to draw to the window. */
    private BufferedImage buffer;

    /** A sprite for the camera to follow. */
    private Sprite toFollow;

    /** The background image to draw. */
    private Image background;

    /**
     * Initialises the camera and creates a new buffer.
     * @param window the window to draw to
     * @param width the width of the camera region
     * @param height the height of the camera region
     * @param maxX the maximum world x of the camera
     * @param maxY the maximum world y of the camera
     * @param fullscreen whether the camera should draw fullscreen
     */
    public Camera(JFrame window, int width, int height, int maxX, int maxY, boolean fullscreen) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.window = window;
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;

        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g = buffer.createGraphics();
        g.setClip(0, 0, width, height);
    }

    /**
     * Sets the x coordinate.<br>
     * Validates that its within the maxX.
     * @param newX the new x value
     */
    private void setX(float newX) {
        if (newX < 0) {
            x = 0;
        } else if (newX + width > maxX) {
            x = maxX - width;
        } else {
            x = newX;
        }
    }

    /**
     * Sets the y coordinate.<br>
     * Validates that its within the maxY.
     * @param newY the new y value
     */
    private void setY(float newY) {
        if (newY < 0) {
            y = 0;
        } else if (newY + height > maxY) {
            y = maxY - height;
        } else {
            y = newY;
        }
    }

    /**
     * Interpolates between the lastPosition and the newPosition using the given alpha.
     * @param lastPosition the last position the point was at
     * @param newPosition the new position the point should be at
     * @param alpha the amount (between 0 and 1) to interpolate between the positions.
     * @return a position between the two given positions based on the alpha
     */
    public Point interpolatePosition(Point lastPosition, Point newPosition, float alpha) {
    	float lastX = lastPosition.x;
    	float lastY = lastPosition.y;
    	float newX = newPosition.x;
    	float newY = newPosition.y;

    	float xDist = newX - lastX;
    	float xInterpol = xDist * alpha;
        float interpolatedX = lastX + xInterpol;
        float yDist = newY - lastY;
        float yInterpol = yDist * alpha;
        float interpolatedY = lastY + yInterpol;

		return new Point(interpolatedX, interpolatedY);
	}

    /**
     * Gives a world point relative to the camera.
     * @param point the point to convert
     * @return the point relative to the camera
     */
	public Point toCameraCoordinates(Point point) {
    	return new Point(point.x - x, point.y - y);
	}

    /**
     * Sets the sprite to follow.
     * @param toFollow the sprite to follow
     */
    public void follow(Sprite toFollow) {
        this.toFollow = toFollow;
    }

    /**
     * Draws the buffer to screen.
     */
    public void flush() {
        if (toFollow != null) {
            setX((int)(toFollow.getX()) - width / 2);
            setY((int)(toFollow.getY()) - height / 2);
        }

        if (fullscreen)
            window.getGraphics().drawImage(buffer,0,0, window.getWidth(), window.getHeight(), null);
        else
            window.getGraphics().drawImage(buffer,8,31, window.getWidth() - 16, window.getHeight() - 37, null);

        buffer = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
        g = buffer.createGraphics();
        g.setClip(0, 0, (int)width, (int)height);

        if (background != null)
            g.drawImage(background, (int) ((x / 2) - width), 0, (int) width * 2, (int) height, null);
    }

    /**
     * Gets the graphics object to draw to.
     * @return the graphics object
     */
    public Graphics2D getGraphics() {
        return g;
    }

    /**
     * Sets the background to draw on.
     * @param background the background image
     */
    public void setBackground(Image background) {
        this.background = background;
    }
}
