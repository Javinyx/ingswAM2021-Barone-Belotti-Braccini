package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.EndTurnMessage;
import it.polimi.ingsw.messages.concreteMessages.PlayersPositionMessage;
import it.polimi.ingsw.messages.concreteMessages.TurnNumberMessage;
import it.polimi.ingsw.messages.concreteMessages.VaticanReportMessage;

import java.io.ObjectInputStream;

/**This class allows the Client to read/send messages from/to the server. */
public class MessageReceiver implements Runnable{
    protected final ClientController controller;
    protected static final Gson gson = new Gson();
    private final ObjectInputStream socketIn;

    private volatile Object pongLock = new Object();

    public MessageReceiver(ObjectInputStream socketIn, ClientController controller){
        this.socketIn = socketIn;
        this.controller = controller;
    }

    // MESSAGE RECEIVER ------------------------------------------------------------------------------------------------

    /**
     * Receives messages from server and calls the right method on controller.
     */
    @Override
    public void run() {

        Thread pong = getPingPongSystem();
        pong.start();

        try {
            while (controller.isActive()) {
                String inputObject = (String)socketIn.readObject();
                controller.setWaitingServerUpdate(false);
                MessageEnvelope envelope = gson.fromJson(inputObject, MessageEnvelope.class);
                if(envelope.getMessageID().equals(MessageID.PING)){
                    synchronized (pongLock){
                        try {
                            pongLock.notifyAll();
                        }catch(Exception e){
                            System.out.println("qui");
                        }
                    }
                }else {
                    controller.setWaitingServerUpdate(false);
                    if (controller.isRegistrationPhase())
                        readRegistrationMessage(envelope);
                    else
                        readGameMessage(envelope);
                }
            }
        } catch (Exception e){
            controller.connectionError();
        }

    }


    public void readRegistrationMessage(MessageEnvelope envelope){
        controller.setLastRegistrationMessage(envelope.getMessageID());

        //System.out.println("REGISTRATION: " + envelope.getMessageID());

        switch(envelope.getMessageID()){
            case ACK -> controller.continueTurn(Boolean.parseBoolean(envelope.getPayload()));
            case ASK_NICK -> controller.askNickname();
            case PLAYER_NUM -> controller.askNumberOfPlayers();
            case CONFIRM_REGISTRATION -> controller.confirmRegistration(envelope.getPayload());

            case TURN_NUMBER -> controller.setTotalPlayers(gson.fromJson(envelope.getPayload(), TurnNumberMessage.class));

            case CHOOSE_LEADER_CARDS -> controller.setLeaderAvailable(envelope.getPayload());
            case TOO_MANY_PLAYERS -> controller.displayMessage(envelope.getPayload());
            case CHOOSE_RESOURCE -> controller.chooseResourceAction(Integer.parseInt(envelope.getPayload()));

            case UPDATE -> controller.updateAction(envelope.deserializeUpdateMessage());
            case CONFIRM_END_TURN -> controller.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));
            case PLAYERS_POSITION -> controller.updatePositionAction(gson.fromJson(envelope.getPayload(), PlayersPositionMessage.class));

            case START_INITIAL_GAME -> controller.startInitialGame();

            default -> System.err.println("MessageID not recognised Registration");
        }


    }

    public void readGameMessage(MessageEnvelope envelope) {
        controller.setLastGameMessage(envelope.getMessageID());

        System.out.println("GAME: " + envelope.getMessageID());

        switch(envelope.getMessageID()){

            case START_INITIAL_GAME -> controller.startInitialGame();
            case CONFIRM_REGISTRATION -> controller.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));

            case ACK -> controller.continueTurn(Boolean.parseBoolean(envelope.getPayload()));
            case CARD_NOT_AVAILABLE -> controller.cardNotAvailable();
            case BAD_PRODUCTION_REQUEST -> controller.badProductionRequest();
            case BAD_PAYMENT_REQUEST -> controller.badPaymentRequest();
            case BAD_DIMENSION_REQUEST -> controller.badDimensionRequest();
            case WRONG_STACK_CHOICE -> controller.wrongStackRequest();
            case WRONG_LEVEL_REQUEST -> controller.wrongLevelRequest();
            case BAD_STORAGE_REQUEST -> controller.badStorageRequest();
            case LEADER_NOT_ACTIVABLE -> controller.leaderNotActivable();
            case BAD_REARRANGE_REQUEST -> controller.badRearrangeRequest();

            case CHOOSE_LEADER_CARDS -> controller.chooseLeadersAction();
            case STORE_RESOURCES -> controller.chooseStorageAfterMarketAction(envelope.getPayload());

            case UPDATE -> controller.updateAction(envelope.deserializeUpdateMessage());
            case CONFIRM_END_TURN -> controller.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));

            case LORENZO_POSITION -> controller.upLorenzoToken(envelope.getPayload());
            case VATICAN_REPORT -> controller.infoVaticanReport(gson.fromJson(envelope.getPayload(), VaticanReportMessage.class));
            case PLAYERS_POSITION -> controller.updatePositionAction(gson.fromJson(envelope.getPayload(), PlayersPositionMessage.class));

            case ACTIVATE_LEADER -> controller.activateLeader(Integer.parseInt(envelope.getPayload()));


            default -> System.err.println("MessageID not recognised Game");
        }

    }

    /**Return the thread that will send a PONG message to server through {@link MessageToServerHandler} class.
     * <p>In order to send the pong, it must be awakened via {@code pongLock.notify()}. So when {@link MessageReceiver}
     *'s thread gets a PING, it should invoke this thread.</p>*/
    public Thread getPingPongSystem(){
        Gson gson = new Gson();
        MessageToServerHandler msgHandler = controller.getMessageToServerHandler();
        Thread pong = new Thread(() -> {
                while (true) {
                    synchronized (pongLock){
                    try {
                        pongLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MessageEnvelope pongEnvelope = new MessageEnvelope(MessageID.PONG, "");
                    msgHandler.sendMessageToServer(gson.toJson(pongEnvelope, MessageEnvelope.class));

                }
            }
        });

        return pong;
    }

}
