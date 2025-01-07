package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.UserStats;
import at.fhtw.mtcg.security.Token;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.ScoreboardRepository;

import java.util.Vector;

public class ScoreboardService {
    private ScoreboardRepository scoreboardRepository;
    public ScoreboardService() {
        scoreboardRepository = new ScoreboardRepository(new UnitOfWork());
    }




    public Response handleGetReq(Request request) {
        // Get the authorization token from the request header
        String token = request.getAuthorizationToken();

        // Validate the token
        if (!Token.validateToken(token)) {
            String response = "Invalid Token";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }


        if (token.contains("altenhof")){
            Vector<UserStats> allStats = scoreboardRepository.getStats();
            String response = createJsonFromUserStatsModel(allStats);
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
        }else if (token.contains("kienboec")){
            Vector<UserStats> allStats = scoreboardRepository.getStats();
            String response = createJsonFromUserStatsModel(allStats);
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
        }else{
            String response = "Ein unerwarteter Fehler ist aufgetaucht";
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, response);
        }

    }

    private String createJsonFromUserStatsModel(Vector<UserStats> allStats){
        String json = "{\n";
        for (UserStats userStats:allStats) {
            json = json +"[" + "\"name\": \"" + userStats.getName() + "\", "
                    + "\"coins\": " + userStats.getCoins() + ", "
                    + "\"mmr\": " + userStats.getMmr() + ", "
                    + "\"losses\": " + userStats.getLosses() + ", "
                    + "\"wins\": " + userStats.getWins() + "],\n";
        }
        json = json + " }";
        return json;
    }


}
