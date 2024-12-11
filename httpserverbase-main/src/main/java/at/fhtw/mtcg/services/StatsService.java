package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.UserStats;
import at.fhtw.mtcg.hash.BearerToken;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.StatsRepository;

public class StatsService {
    private StatsRepository statsRepository;
    public StatsService() {
        statsRepository = new StatsRepository(new UnitOfWork());
    }




    public Response handleGetReq(Request request) {
        // Get Token from header
        String token = request.getAuthorizationToken();

        // Validate the token
        if (!BearerToken.validateToken(token)) {
            String response = "Invalid Token";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }

        // Get the stats for the user and return a HTTP response
        if (token.contains("altenhof")){
            UserStats userStats = statsRepository.getStats("altenhof");
            String response = createJsonFromUserStatsModel(userStats);
            return new Response(HttpStatus.OK, ContentType.JSON, response);

        }else if (token.contains("kienboec")){
            UserStats userStats = statsRepository.getStats("kienboec");
            String response = createJsonFromUserStatsModel(userStats);
            return new Response(HttpStatus.OK, ContentType.JSON, response);

        }else{

            String response = "Ein unerwarteter Fehler ist aufgetaucht";
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, response);

        }
    }

    // Create a JSON string from a UserStats object
    private String createJsonFromUserStatsModel(UserStats userStats){
        String json = "{ "
                + "\"name\": \"" + userStats.getName() + "\", "
                + "\"coins\": " + userStats.getCoins() + ", "
                + "\"mmr\": " + userStats.getMmr() + ", "
                + "\"losses\": " + userStats.getLosses() + ", "
                + "\"wins\": " + userStats.getWins()
                + " }";
        return json;
    }
}
