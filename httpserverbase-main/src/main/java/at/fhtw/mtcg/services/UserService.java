package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.controllers.UserController;


public class UserService implements RestController {
    private final UserController userController;

    public UserService() {
        this.userController = new UserController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            return this.userController.handleGetReq(request);
        } else if (request.getMethod() == Method.POST) {
            return this.userController.handlePostRequest(request);
        } else if (request.getMethod() == Method.PUT) {
            return this.userController.handlePutReq(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }


}
