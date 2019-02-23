package GameUtils;

import game2D.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {
    private float x = 0;
    private float y = 0;
    private float width;
    private float height;
    private JFrame window;
    private Graphics2D g;
    private BufferedImage buffer;
    private Sprite toFollow;

    public Camera(JFrame window, int width, int height) {
        this.window = window;
        this.width = width;
        this.height = height;

        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g = buffer.createGraphics();
        g.setClip(0, 0, width, height);
    }

    public void translateX(int change) {
        x = x + change;
    }

    public void translateY(int change) {
        y = y + change;
    }

    public void draw(Sprite sprite, float alpha) {
        float lastX = sprite.getLastX();
        float lastY = sprite.getLastY();

        sprite.saveY();
        sprite.saveX();

        float newX = sprite.getX();
        float newY = sprite.getY();

        float xDist = newX - lastX;
        float xInterpol = xDist * alpha;
        float drawX = lastX + xInterpol;

        float yDist = newY - lastY;
        float yInterpol = yDist * alpha;
        float drawY = lastY + yInterpol;

        g.drawImage(sprite.getImage(),Math.round(drawX - x),Math.round(drawY - y),null);
        //g.drawImage(sprite.getImage(),Math.round((sprite.getX() - x) * alpha),Math.round((sprite.getY() - y) * alpha),null);
    }

    public void draw(Player player, float alpha) {
        float lastX = player.getLastX();
        float lastY = player.getLastY();

        player.saveY();
        player.saveX();

        float newX = player.getX();
        float newY = player.getY();

        float xDist = newX - lastX;
        float xInterpol = xDist * alpha;
        float drawX = lastX + xInterpol;

        float yDist = newY - lastY;
        float yInterpol = yDist * alpha;
        float drawY = lastY + yInterpol;


        if (!player.isFacingRight()) {
            g.drawImage(player.getImage(), Math.round(drawX + player.getWidth() - x), Math.round(drawY - y), -player.getWidth(), player.getHeight(), null);
        } else {
            g.drawImage(player.getImage(), Math.round(drawX - x), Math.round(drawY - y), player.getWidth(), player.getHeight(), null);
        }
    }

    public void follow(Sprite toFollow) {
        this.toFollow = toFollow;
    }

    public void drawToScreen() {
        if (toFollow != null) {
            x = toFollow.getX() - width / 2;
            y = toFollow.getY() - height / 2;
        }

        window.getGraphics().drawImage(buffer,8,31, window.getWidth(), window.getHeight(), null);

        buffer = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
        g = buffer.createGraphics();
        //g.setClip(0, 0, width, height);
    }

    public Graphics2D getGraphics() {
        return g;
    }
}
