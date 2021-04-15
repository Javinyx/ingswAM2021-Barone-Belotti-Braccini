package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MarbleAbility implements LeaderCard {
    private int id;
    private final int victoryPoints;
    private final List<ConcreteProductionCard> cost;
    private boolean status;
    private Resource replacingResource;

    public MarbleAbility(int id, int victoryPoints, List<ConcreteProductionCard> cost, Resource replacingResource) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.cost = new ArrayList<>(cost);
        status = false;
        this.replacingResource = replacingResource;
    }

    public Resource getReplacement(){
        return replacingResource;
    }

    @Override
    public boolean isActive() {
        return status;
    }

    @Override
    public void setStatus(boolean activate) {
        status = activate;
    }

    @Override
    public int getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public List<Buyable> getCost() {
        return new ArrayList<>(cost);
    }

    public boolean applyEffect(ProPlayer player) {

        for (Resource r : player.getResAcquired()) {
            if (r == Resource.BLANK) {
                player.getResAcquired().remove(r);
                player.getResAcquired().add(replacingResource);
                return true;
            }
        }
        return false;
    }
    @Override
    public String toString(){
        List<ProductionCard> generalCost = cost.stream().map(x -> (ProductionCard)x).collect(Collectors.toList());
        return "MarbleAbility(Victory Points: " + victoryPoints +("\nActivation Cost: " + generalCost==null ? "null" : generalCost) +
                "\nReplacing Resource: " + replacingResource + ")";
    }
}
