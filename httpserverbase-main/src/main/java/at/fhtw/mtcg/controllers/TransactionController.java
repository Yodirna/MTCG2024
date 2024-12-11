package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.services.TransactionService;

public class TransactionController implements RestController {
    private final TransactionService transactionService;

    public TransactionController() {
        this.transactionService = new TransactionService();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getPathParts().get(1).equals("packages")){
            if (request.getMethod() == Method.POST) {
                return this.transactionService.handlePostRequest(request);
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
