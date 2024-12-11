package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.services.RewardService;

public class RewardController implements RestController {
    private final RewardService rewardService;

    public RewardController() {
        this.rewardService = new RewardService();
    }


    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return this.rewardService.handleGetReq(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
