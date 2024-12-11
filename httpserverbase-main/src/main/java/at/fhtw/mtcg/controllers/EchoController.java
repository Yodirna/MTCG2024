package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;

public class EchoController implements RestController {
    @Override
    public Response handleRequest(Request request) {
        return new Response(HttpStatus.OK,
                            ContentType.PLAIN_TEXT,
                     "Echo-" + request.getBody());
    }
}
