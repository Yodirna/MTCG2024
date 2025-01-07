package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.controllers.PackageController;

public class PackagesService implements RestController {
    private final PackageController packagesService;

    public PackagesService() {
        this.packagesService = new PackageController();
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