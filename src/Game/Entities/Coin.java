package Game.Entities;

import Game.Animation;
import Game.Sprite;

import java.util.Arrays;

/**
 * The coin class represents coins that the player can pickup.
 * @author Connor Stewart
 */
public class Coin extends Sprite {

    /**
     * Creates a new coin at the specified location.
     * @param x the starting x coordinate of the coin
     * @param y the starting y coordinate of the coin
     */
    public Coin(int x, int y) {
        super(null, x, y,0,0,15,15);
        Animation spin = new Animation(true);
        spin.loadAnimationFromImages(Arrays.asList(
                "images/coin/gold_coin_round_blank_1.png",
                "images/coin/gold_coin_round_blank_2.png",
                "images/coin/gold_coin_round_blank_3.png",
                "images/coin/gold_coin_round_blank_4.png",
                "images/coin/gold_coin_round_blank_5.png",
                "images/coin/gold_coin_round_blank_6.png"), 100, 15, 15);
        setAnimation(spin);
    }
}
