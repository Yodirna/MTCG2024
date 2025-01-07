package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.models.GameLogic;
import at.fhtw.mtcg.models.User;
import at.fhtw.mtcg.security.Token;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.LobbyRepository;
import at.fhtw.mtcg.database.repository.DeckRepository;

import java.util.*;

import static at.fhtw.mtcg.cards.CardRegistry.createRealCard;


public class LobbyController {
    private LobbyRepository lobbyRepository;
    private static final List<User> lobby = Collections.synchronizedList(new ArrayList<>());
    private static final Object lock = new Object();
    public static final List<Request> requests = Collections.synchronizedList(new ArrayList<>());
    private final Map<Request, Response> pendingResponses = new HashMap<>();

    public LobbyController() {
        lobbyRepository = new LobbyRepository(new UnitOfWork());

    }

    public synchronized void handlePostReq(Request request) {
        String token = request.acquireAuthorizationToken();
        if (!Token.validateToken(token)) {
            pendingResponses.put(request, new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Invalid Token!"));
            return;
        }

        User currentUser = token.contains("altenhof") ? new User("altenhof", "iwas") : new User("kienboec", "iwas");
        lobby.add(currentUser);
        requests.add(request);

        if (lobby.size() == 2) {
            User player1 = lobby.remove(0);
            User player2 = lobby.remove(0);
            Request request1 = requests.remove(0);
            Request request2 = requests.remove(0);

            String battleLogs = startBattle(player1, player2);

            pendingResponses.put(request1, new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, battleLogs));
            pendingResponses.put(request2, new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, battleLogs));
            // Notify all waiting threads that the response is available
            notifyAll();
        }
    }

    public synchronized Response getResponseForRequest(Request request) {
        while (!pendingResponses.containsKey(request)) {
            try {
                wait(); // Wait until the response is available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "An error occurred!");
            }
        }
        return pendingResponses.remove(request);
    }

    public User createUserModel(String username, User player){
        DeckRepository deckRepository = new DeckRepository(new UnitOfWork());
        List<Card> deck = deckRepository.getDeck(username);
        // Save the cards in the player model
        for (Card card: deck) {
            Card realCard = createRealCard(card);
            player.addCardToStack(realCard);
        }
        return player;
    }


    public String startBattle(User player1, User player2) {
        try{
            GameLogic gameLogic = new GameLogic();

            // Save all the data of the player in the model, so card stack etc.
            player1 = createUserModel("kienboec", player1);
            player2 = createUserModel("altenhof", player2);

            // 4 rounds of fighting, each card 1 round
            for (int i = 0; i<4; i++) {
                gameLogic.addLogEntry("Round " + i + ": ");
                gameLogic.fight(player1.getStack().get(i), player2.getStack().get(i));

            }


            // The player hp = the sum of all deck cards after they have fought each other.
            // All cards have 100 hp
            int player1HP = 0;
            for (Card card : player1.getStack()){
                player1HP += card.getHp();
            }

            int player2HP = 0;
            for (Card card : player2.getStack()){
                player2HP += card.getHp();
            }

            List<String> battleLogs = gameLogic.getBattleLog();
            StringBuilder sb = new StringBuilder();
            for(String logEntry : battleLogs) {
                sb.append(logEntry);
                sb.append("\n");
            }
            String logsFormatted = sb.toString();

            if (player1HP > player2HP){
                String response = player1.getUsername() + " wins!\n";
                doPostGameUpdates(player1, player2);
                return logsFormatted + response;

            }else if (player1HP == player2HP){
                return logsFormatted;

            } else{
                String response = player2.getUsername() + " wins!\n";
                doPostGameUpdates(player2, player1);
                return logsFormatted + response;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    return "";}

    public boolean doPostGameUpdates(User winner, User loser){
        UnitOfWork unitOfWork = new UnitOfWork();
        try {
            LobbyRepository lobbyRepository = new LobbyRepository(unitOfWork);
            lobbyRepository.updateWinnerStats(winner.getUsername(), unitOfWork);
            lobbyRepository.updateLoserStats(loser.getUsername(), unitOfWork);
            lobbyRepository.winnerTakesLoserCards(winner.getUsername(), loser.getUsername(), unitOfWork);
            unitOfWork.commitTransaction();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return false;
        }


    }
}
