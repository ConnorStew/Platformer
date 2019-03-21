package Game.Entities;

import Game.Animation;
import Game.Sprite;

import java.util.Collections;

/**
 * A signpost is used to mark the end of a level.
 * @author Connor Stewart
 */
public class Signpost extends Sprite {

    /**
     * Creates a signpost at the given x and y coordinates.
     * @param x the starting x coordinate of the signpost
     * @param y the starting y coordinate of the signpost
     */
    public Signpost(int x, int y) {
        super(null, x, y,0,0,20,30);
        Animation sprite = new Animation(true);
        sprite.loadAnimationFromImages(Collections.singletonList("images/signpost.png"), 0, 20, 30);
        setAnimation(sprite);
    }
}
