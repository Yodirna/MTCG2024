package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.hash.BearerToken;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.CardRepository;

import java.util.List;

public class CardService {
    private CardRepository cardRepository;
    public CardService() {
        cardRepository = new CardRepository(new UnitOfWork());
    }

    // Get Request
    public Response handleGetReq(Request request){


        // Get token from header
        String token = request.getAuthorizationToken();


        // Validate token
        if (token == null || !BearerToken.validateToken(token)) {
            String response = "Invalid Token";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }

        // Check if token contains altenhof
        if (token.contains("altenhof")){
            // Get all cards calls all the functions and creates a return
            List<String> response = cardRepository.getAllCards("altenhof");

            // response 204 if the user has no cards
            if (response.toString().equals("[]")){
                return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, "[]");
            }else{
                return new Response(HttpStatus.OK, ContentType.JSON, response.toString());
            }
        // Check if token contains kienboec
        }else if (token.contains("kienboec")){
            List<String>  response = cardRepository.getAllCards("kienboec");

            if (response.toString().equals("[]")){
                return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, "[]");
            }else{
                return new Response(HttpStatus.OK, ContentType.JSON, response.toString());
            }
        }else{
            String response = "No / Invalid Token";
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, response);
        }
    }
}
