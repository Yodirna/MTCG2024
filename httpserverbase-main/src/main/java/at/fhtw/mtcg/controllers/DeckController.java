package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.security.Token;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.DeckRepository;
import at.fhtw.mtcg.database.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Vector;

public class DeckController {
    private final DeckRepository deckRepository;

    public DeckController() {
        this.deckRepository = new DeckRepository(new UnitOfWork());
    }

    public Response handleGetReq(Request request) {
        String token = request.acquireAuthorizationToken();

        if (!Token.validateToken(token)) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Invalid Token");
        }

        String username = extractUsernameFromToken(token);
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized Token");
        }

        List<Card> deck = deckRepository.getDeck(username);
        if (deck.isEmpty()) {
            return new Response(HttpStatus.NO_CONTENT, ContentType.PLAIN_TEXT, "No Deck Found");
        }

        String response = formatDeck(deck, request.getFormat());
        ContentType contentType = "plain".equals(request.getFormat()) ? ContentType.PLAIN_TEXT : ContentType.JSON;
        return new Response(HttpStatus.OK, contentType, response);
    }

    public Response handlePutReq(Request request) {
        try {
            Vector<String> cardsToConfigure = extractCardIds(request.getBody());
            if (cardsToConfigure.size() != 4) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT,
                        "The provided deck must include exactly 4 cards");
            }

            String token = request.acquireAuthorizationToken();
            if (!Token.validateToken(token)) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Invalid Token");
            }

            String username = extractUsernameFromToken(token);
            if (username == null) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Unauthorized Token");
            }

            if (!userOwnsAllCards(username, cardsToConfigure) || hasDuplicateCardsInDeck(username, cardsToConfigure)) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT,
                        "Some cards are either not owned by the user or already in the deck.");
            }

            return deckRepository.configureDeck(username, cardsToConfigure)
                    ? new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Cards successfully added to deck")
                    : new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Failed to configure deck");

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Unexpected Error");
        }
    }

    private String extractUsernameFromToken(String token) {
        if (token.contains("altenhof")) return "altenhof";
        if (token.contains("kienboec")) return "kienboec";
        return null;
    }

    private Vector<String> extractCardIds(String requestBody) throws Exception {
        JsonNode body = new ObjectMapper().readTree(requestBody);
        Vector<String> cardIds = new Vector<>();
        if (body.isArray()) {
            body.forEach(node -> {
                if (node.isTextual()) cardIds.add(node.asText());
            });
        }
        return cardIds;
    }

    private String formatDeck(List<Card> deck, String format) {
        if ("plain".equals(format)) {
            StringBuilder response = new StringBuilder();
            deck.forEach(card -> response.append(String.format("ID: %s, Name: %s, Damage: %d, Element: %s%n",
                    card.getID(), card.getName(), card.getDamage(), card.getElement())));
            return response.toString();
        }
        return deckRepository.createJsonForAllCards(deck).toString();
    }

    private boolean userOwnsAllCards(String username, Vector<String> cardsToConfigure) {
        int userID = new UserRepository(new UnitOfWork()).getUserID(username);
        return deckRepository.checkProvidedCardsWithAcquiredCardsInDB(userID, cardsToConfigure);
    }

    private boolean hasDuplicateCardsInDeck(String username, Vector<String> cardsToConfigure) {
        int userID = new UserRepository(new UnitOfWork()).getUserID(username);
        List<String> deckCardIds = deckRepository.getDeckCardsIds(userID);
        return cardsToConfigure.stream().anyMatch(deckCardIds::contains);
    }
}
