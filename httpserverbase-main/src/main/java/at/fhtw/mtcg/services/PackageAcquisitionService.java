package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.PackageAcquisitionRepository;

import static at.fhtw.mtcg.security.Token.validateToken;

public class PackageAcquisitionService {

    private PackageAcquisitionRepository packageAcquisitionRepository;
    public PackageAcquisitionService() {
        packageAcquisitionRepository = new PackageAcquisitionRepository(new UnitOfWork());
    }

    public Response handlePostRequest(Request request) {
        try {
            // Retrieve the authorization token from the request header
            String token = request.getAuthorizationToken();

            boolean allTokenChecks = validateToken(token);
            if (!allTokenChecks){
                // If the token is invalid, return a 403 Forbidden response
                String response = "Access token is missing or invalid";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

            // If the token belongs to "altenhof", proceed to purchase a package as "altenhof"
            if (token.contains("altenhof")) {
                int code = packageAcquisitionRepository.acquirePackage("altenhof");
                String response;
                if (code == 1){
                    // Successfully purchased a package
                    response = "A package has been successfully bought";
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);

                }else if (code == 2){
                    // No available packages for purchase at the moment
                    response = "No card package available for buying";
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);

                } else if (code == 3) {
                    // Insufficient funds to complete the purchase
                    response = "Not enough money for buying a card package";
                    return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);

                }else {
                    // An unexpected error occurred during the transaction
                    response = "An unexpected error has occurred";
                    return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, response);
                }

                // If the token belongs to "kienboec", proceed to purchase a package as "kienboec"
            } else if (token.contains("kienboec")) {
                int code = packageAcquisitionRepository.acquirePackage("kienboec");
                String response;
                if (code == 1){
                    // Successfully purchased a package
                    response = "A package has been successfully bought";
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);

                }else if (code == 2){
                    // No available packages for purchase at the moment
                    response = "No card package available for buying";
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);

                } else if (code == 3) {
                    // Insufficient funds to complete the purchase
                    response = "Not enough money for buying a card package";
                    return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);

                }else {
                    // An unexpected error occurred during the transaction
                    response = "An unexpected error has occurred";
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                }

                // If the token does not belong to either authorized user, respond with Unauthorized
            } else {
                String response = "Access token is missing or invalid";
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // If an exception occurs (e.g., invalid request format), respond with Bad Request
            String response = "An unexpected error has occurred";
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, response);
        }
    }


}
