package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.security.Token;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.CardRepository;

import java.util.List;

/**
 * CardService handles the business logic for card-related operations.
 */
public class CardsController {
    private final CardRepository cardRepository;

    /**
     * Initializes the CardService and its dependencies.
     */
    public CardsController() {
        this.cardRepository = new CardRepository(new UnitOfWork());
    }

    /**
     * Handles GET requests to retrieve card information for a user.
     */
    public Response handleGetReq(Request request) {
        // Retrieve the authorization token from the request header
        String token = request.getAuthorizationToken();

        // Validate the token
        if (token == null || !Token.validateToken(token)) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Invalid Token");
        }

        // Determine user identity based on token content and retrieve their cards
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "No / Invalid Token");
        }

        // Fetch all cards for the identified user
        List<String> response = cardRepository.getAllCards(username);

        // Return 204 if the user has no cards, otherwise return the card list
        if (response.isEmpty()) {
            return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, "[]");
        } else {
            return new Response(HttpStatus.OK, ContentType.JSON, response.toString());
        }
    }

    /**
     * Extracts the username from the token based on predefined conditions.
     *
     */
    private String extractUsernameFromToken(String token) {
        if (token.contains("altenhof")) {
            return "altenhof";
        } else if (token.contains("kienboec")) {
            return "kienboec";
        }
        return null;
    }
}
