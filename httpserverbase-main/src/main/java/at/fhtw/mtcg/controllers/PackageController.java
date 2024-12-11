package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.services.PackageService;

public class PackageController implements RestController {
    private final PackageService packagesService;

    public PackageController() {
        this.packagesService = new PackageService();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST)  {
            return this.packagesService.handlePostRequest(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}