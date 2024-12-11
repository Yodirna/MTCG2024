package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.models.Game;
import at.fhtw.mtcg.models.User;
import at.fhtw.mtcg.hash.BearerToken;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.BattleRepository;
import at.fhtw.mtcg.database.repository.DeckRepository;

import java.util.*;

import static at.fhtw.mtcg.classes.CardToClassMapper.createRealCard;


public class FightService {
    private BattleRepository fightRepository;
    private static final List<User> lobby = Collections.synchronizedList(new ArrayList<>());
    private static final Object lock = new Object();
    public static final List<Request> requests = Collections.synchronizedList(new ArrayList<>());
    private final Map<Request, Response> pendingResponses = new HashMap<>();

    public FightService() {
        fightRepository = new BattleRepository(new UnitOfWork());

    }

    public synchronized void handlePostReq(Request request) {
        String token = request.getAuthorizationToken();
        if (!BearerToken.validateToken(token)) {
            pendingResponses.put(request, new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Invalid Token"));
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

            pendingResponses.put(request1, new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, String.join("\n", battleLogs)));
            pendingResponses.put(request2, new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, String.join("\n", battleLogs)));
            // Benachrichtige den Controller, dass die Antworten bereit sind
            notifyAll();
        }
    }

    public synchronized Response getResponseForRequest(Request request) {
        while (!pendingResponses.containsKey(request)) {
            try {
                wait(); // Warte, bis die Antwort verfügbar ist
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "An error occurred");
            }
        }
        return pendingResponses.remove(request);
    }

    public User createUserModel(String username, User player){
        DeckRepository deckRepository = new DeckRepository(new UnitOfWork());
        List<Card> deck = deckRepository.getDeck(username);
        // die karten im user model speichern
        for (Card card: deck) {
            Card realCard = createRealCard(card);
            player.addCardToStack(realCard);
        }
        return player;
    }


    public String startBattle(User player1, User player2) {
        try{
            Game game = new Game();

            // alle daten vom spieler im model speichern, also card stack etc.
            player1 = createUserModel("kienboec", player1);
            player2 = createUserModel("altenhof", player2);

            // 4 runden --> pro karte eine runde fight
            for (int i = 0; i<4; i++) {
                game.addLogEntry("Runde " + i + ": ");
                game.fight(player1.getStack().get(i), player2.getStack().get(i));

            }


            // die player hp = die summe alle deck karten nachdem sie miteinander gekämpft haben.
            // aller karten haben 100 hp
            int player1HP = 0;
            for (Card card : player1.getStack()){
                player1HP += card.getHp();
            }

            int player2HP = 0;
            for (Card card : player2.getStack()){
                player2HP += card.getHp();
            }

            List<String> battleLogs = game.getBattleLog();
            StringBuilder sb = new StringBuilder();
            for(String logEntry : battleLogs) {
                sb.append(logEntry);
                sb.append("\n");
            }
            String logsFormatted = sb.toString();

            if (player1HP > player2HP){
                String response = player1.getUsername() + " wins!\n";
                doPostGameUpdates(player1, player2);
                return response + logsFormatted;

            }else if (player1HP == player2HP){
                return logsFormatted;

            } else{
                String response = player2.getUsername() + " wins!\n";
                doPostGameUpdates(player2, player1);
                return response + logsFormatted;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    return "";}

    public boolean doPostGameUpdates(User winner, User loser){
        UnitOfWork unitOfWork = new UnitOfWork();
        try {
            BattleRepository battleRepository = new BattleRepository(unitOfWork);
            battleRepository.updateWinnerStats(winner.getUsername(), unitOfWork);
            battleRepository.updateLoserStats(loser.getUsername(), unitOfWork);
            battleRepository.winnerTakseLoserCards(winner.getUsername(), loser.getUsername(), unitOfWork);
            unitOfWork.commitTransaction();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return false;
        }


    }
}
