package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.controllers.LobbyController;


public class LobbyService implements RestController {
    private final LobbyController lobbyController;
    public LobbyService(){this.lobbyController = new LobbyController();}

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            lobbyController.handlePostReq(request);
            return lobbyController.getResponseForRequest(request);
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad request");
    }
}
