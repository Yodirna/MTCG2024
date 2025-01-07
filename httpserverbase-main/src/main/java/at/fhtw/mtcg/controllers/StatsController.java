package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.UserStats;
import at.fhtw.mtcg.security.Token;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.StatsRepository;

public class StatsController {
    private final StatsRepository statsRepository;

    public StatsController() {
        this.statsRepository = new StatsRepository(new UnitOfWork());
    }

    public Response handleGetReq(Request request) {
        // Extract and validate the token from the header
        String token = request.acquireAuthorizationToken();

        if (!Token.validateToken(token)) {
            return createPlainTextResponse(HttpStatus.FORBIDDEN, "Invalid Token");
        }

        // Determine the user based on the token and fetch their stats
        String username = getUsernameFromToken(token);
        if (username == null) {
            return createPlainTextResponse(HttpStatus.NOT_FOUND, "An unexpected error has occurred!");
        }

        UserStats userStats = statsRepository.getStats(username);
        if (userStats == null) {
            return createPlainTextResponse(HttpStatus.NOT_FOUND, "User stats not found!");
        }

        String jsonResponse = createJsonFromUserStats(userStats);
        return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
    }

    // Helper method to determine username from the token
    private String getUsernameFromToken(String token) {
        if (token.contains("altenhof")) {
            return "altenhof";
        } else if (token.contains("kienboec")) {
            return "kienboec";
        }
        return null;
    }

    // Helper method to create JSON from UserStats
    private String createJsonFromUserStats(UserStats userStats) {
        return "{"
                + "\"name\": \"" + userStats.getName() + "\", "
                + "\"coins\": " + userStats.getCoins() + ", "
                + "\"mmr\": " + userStats.getMmr() + ", "
                + "\"losses\": " + userStats.getLosses() + ", "
                + "\"wins\": " + userStats.getWins()
                + "}";
    }

    // Helper method to create a plain text HTTP response
    private Response createPlainTextResponse(HttpStatus status, String message) {
        return new Response(status, ContentType.PLAIN_TEXT, message);
    }
}
