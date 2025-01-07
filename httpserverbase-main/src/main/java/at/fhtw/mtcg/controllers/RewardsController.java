package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.security.Token;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.RewardRepository;

public class RewardsController {
    private final RewardRepository rewardRepository;

    public RewardsController() {
        this.rewardRepository = new RewardRepository(new UnitOfWork());
    }

    public Response handleGetReq(Request request) {
        // Get the authorization token from the request header
        String token = request.acquireAuthorizationToken();

        // Validate the token
        if (!Token.validateToken(token)) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Invalid Token");
        }

        // Extract username from token
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized Token");
        }

        // Handle rewards logic
        return processRewards(username);
    }

    private Response processRewards(String username) {
        int mmr = rewardRepository.getmmr(username);

        if (mmr > 250) {
            int currentCoins = rewardRepository.getUserCoins(username);
            boolean rewardGranted = rewardRepository.getReward(currentCoins, username);

            if (rewardGranted) {
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "received 30 coins!");
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "An unknown error occurred");
            }
        }

        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User has less than 250 MMR points");
    }

    private String extractUsernameFromToken(String token) {
        if (token.contains("altenhof")) {
            return "altenhof";
        } else if (token.contains("kienboec")) {
            return "kienboec";
        }
        return null; // Invalid token
    }
}
