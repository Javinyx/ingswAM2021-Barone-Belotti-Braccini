package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StorageAbilityTest {

    MultiPlayerGame game;
    ProPlayer bubu;
    ProPlayer javin;
    Controller controller = new Controller();

    @BeforeEach
    public void testSetUp(){
        game = new MultiPlayerGame(controller);
        bubu = new ProPlayer("Bubu", 1, game);
        javin = new ProPlayer("Javin", 2, game);
    }

    @Test
    void isActive() {
    }

    @Test
    void setStatus() {
    }

    @Test
    void getVictoryPoints() {
    }

    @Test
    void getCost() {
    }

    @Test
    void applyEffect() {
        while (!((LeaderCard)(game).getLeaderDeckNew().getFirst()).getNameNew().equals("StorageAbility"))
            game.getLeaderDeckNew().getFirst();

    }

    @Test
    void getStorageType() {
    }

    @Test
    void remove() {
    }

    @Test
    void add() {
    }

    @Test
    void size() {
    }

    @Test
    void testToString() {
    }
}