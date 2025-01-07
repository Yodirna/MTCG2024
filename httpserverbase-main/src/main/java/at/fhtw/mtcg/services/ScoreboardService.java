package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.controllers.ScoreboardController;

public class ScoreboardService implements RestController {
    private final ScoreboardController scoreboardController;

    public ScoreboardService() {
        this.scoreboardController = new ScoreboardController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET ) {
            return this.scoreboardController.handleGetReq(request);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}