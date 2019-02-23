package GameUtils;

import game2D.Sprite;

import java.awt.*;
import java.util.Collection;

public class PortalProjectile extends Sprite {

    private static final float GRAVITY_MAX = 9.8f;
    private static final float GRAVITY_INCREASE = 0.5f;
    private PortalColour pc;
    private Color drawColour;

    public PortalProjectile(Collection<Tile> tiles, float x, float y, PortalColour pc, double xMovement, double yMovement) {
        super(tiles);
        this.x = x;
        this.y = y;
        double speed = 25;
        this.dx = (float) (xMovement * speed);
        this.dy = (float) (yMovement * speed);
        this.pc = pc;

        if (pc.equals(PortalColour.ORANGE))
            drawColour = Color.ORANGE;

        if (pc.equals(PortalColour.BLUE))
            drawColour = Color.BLUE;
    }

    public boolean update() {
//        boolean delete = false;
//
//        Tile collidedTile = moveSpriteAndCheckForTiles(dx, dy);
//
//        if (collidedTile != null) {
//            if (pc == PortalColour.ORANGE)
//                collidedTile.setDrawColour(Color.ORANGE);
//                //collidedTile.setDrawColour(new Color(1f, 0.5f, 0.01f, 0.1f));
//
//            delete = true;
//        }
//
//        if (dy < GRAVITY_MAX)
//            dy = (dy + GRAVITY_INCREASE > GRAVITY_MAX) ? GRAVITY_MAX : (dy += GRAVITY_INCREASE);
//
//        return delete;
        return false;
    }

    public void draw(Graphics2D g) {
        g.setColor(drawColour);
        g.fillRoundRect((int)x,(int)y,10,10, 10, 10);
    }
}
