package GameUtils;

import game2D.Sprite;

import java.awt.*;
import java.util.Collection;

public class Projectile extends Sprite {

    private static final float GRAVITY_MAX = 9.8f;
    private static final float GRAVITY_INCREASE = 0.5f;
    private PortalColour pc;
    private Color drawColour;

    public Projectile(Collection<Tile> tiles, float x, float y, PortalColour pc, double xMovement, double yMovement) {
        super(tiles);
        this.x = x;
        this.y = y;
        height = 11;
        width = 11;
        double speed = 25;
        this.dx = (float) (xMovement * speed);
        this.dy = (float) (yMovement * speed);
        this.pc = pc;

        if (pc.equals(PortalColour.ORANGE))
            drawColour = Color.ORANGE;

        if (pc.equals(PortalColour.BLUE))
            drawColour = Color.BLUE;
    }

    public void update(long elapsedTime, Collection<Tile> tiles) {
        Direction collided = moveSprite(dx, dy);

        float friction = 0.1f;
        if (collided != null) {


            switch (collided) {
                case TOP:
                    dy = Math.abs(dy);
                    break;
                case BOTTOM:
                    dy = -dy;
                    break;
                case LEFT:
                    dx = -dx;
                    break;
                case RIGHT:
                    dx = -dx;
                    break;
            }

            //if (collided == Direction.RIGHT || collided == Direction.LEFT)
            //if (collided == Direction.TOP || collided == Direction.BOTTOM)

            if (dx < 0)
                dx = (dx + friction > 0) ? 0 : (dx + friction);
            else
                dx = (dx - friction < 0) ? 0 : (dx - friction);

            if (dy < 0)
                dy = (dy + friction > 0) ? 0 : (dy + friction);
            else
                dx = (dx - friction < 0) ? 0 : (dx - friction);
        }

        if (dy < GRAVITY_MAX)
            dy = (dy + GRAVITY_INCREASE > GRAVITY_MAX) ? GRAVITY_MAX : (dy += GRAVITY_INCREASE);
    }

    public void draw(Graphics2D g) {
        g.setColor(drawColour);
        g.fillRoundRect((int)x,(int)y,10,10, 10, 10);
    }
}
