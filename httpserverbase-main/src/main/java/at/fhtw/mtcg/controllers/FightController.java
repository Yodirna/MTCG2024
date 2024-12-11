package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.services.FightService;


public class FightController implements RestController {
    private final FightService battleService;
    public FightController(){this.battleService = new FightService();}

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            battleService.handlePostReq(request);
            return battleService.getResponseForRequest(request);
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Ungültige Anfrage");
    }
}
