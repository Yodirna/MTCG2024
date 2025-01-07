package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.controllers.RewardsController;

public class RewardsService implements RestController {
    private final RewardsController rewardsController;

    public RewardsService() {
        this.rewardsController = new RewardsController();
    }


    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return this.rewardsController.handleGetReq(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
