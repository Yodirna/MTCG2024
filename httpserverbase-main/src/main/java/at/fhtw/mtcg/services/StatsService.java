package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.controllers.StatsController;

public class StatsService implements RestController {
    private final StatsController statsController;

    public StatsService() {
        this.statsController = new StatsController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET ) {
            return this.statsController.handleGetReq(request);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
