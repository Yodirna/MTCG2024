package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.hash.BearerToken;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.RewardRepository;

public class RewardService {
    private final RewardRepository rewardRepository;
    public RewardService() {
        rewardRepository = new RewardRepository(new UnitOfWork());
    }

    public Response handleGetReq(Request request) {
        // Get the authorization token from the request header
        String token = request.getAuthorizationToken();

        // Validate the token
        if (!BearerToken.validateToken(token)) {
            String response = "Invalid Token";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }

        if (token.contains("altenhof")){
            int mmr = rewardRepository.getmmr("altenhof");

            if (mmr > 250){
                int CurrentCoins =rewardRepository.getUserCoins("altenhof");
                boolean rc = rewardRepository.getReward(CurrentCoins, "altenhof");

                if (rc){
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "10 coins erhalten");
                }else{
                    return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "ein Unbekannter Fehler ist aufgetaucht");
                }
            }

            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User hat weniger als 250 MMR Punkte");




        } else if (token.contains("kienboec")) {
            int mmr = rewardRepository.getmmr("kienboec");

            if (mmr > 250){
                int CurrentCoins = rewardRepository.getUserCoins("kienboec");
                boolean rc = rewardRepository.getReward(CurrentCoins, "kienboec");
                if (rc){
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "10 coins erhalten");
                }else{
                    return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "ein Unbekannter Fehler ist aufgetaucht");
                }
            }

            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User hat weniger als 250 MMR Punkte");

        }else{
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "etwas ist schiefgelaufe");
        }
    }
}
