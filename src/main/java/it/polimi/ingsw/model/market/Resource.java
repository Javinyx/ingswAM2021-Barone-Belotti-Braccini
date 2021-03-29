package it.polimi.ingsw.model.market;

import java.util.Random;

public enum Resource implements Buyable{
    BLANK,
    SHIELD,
    STONE,
    COINS,
    SERVANT,
    FAITH;

    /**
     * Pick a random value of the Resource.
     * @return a random Resource.
     */
    public static Resource getRandomResource() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}
