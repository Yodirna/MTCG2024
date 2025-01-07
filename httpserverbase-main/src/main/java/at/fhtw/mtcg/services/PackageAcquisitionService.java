package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.controllers.PackageAcquisitionController;

public class PackageAcquisitionService implements RestController {
    private final PackageAcquisitionController packageAcquisitionController;

    public PackageAcquisitionService() {
        this.packageAcquisitionController = new PackageAcquisitionController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getPathParts().get(1).equals("packages")){
            if (request.getMethod() == Method.POST) {
                return this.packageAcquisitionController.handlePostRequest(request);
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
