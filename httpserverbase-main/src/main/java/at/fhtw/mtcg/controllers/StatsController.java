package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.services.StatsService;

public class StatsController implements RestController {
    private final StatsService statsService;

    public StatsController() {
        this.statsService = new StatsService();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET ) {
            return this.statsService.handleGetReq(request);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
