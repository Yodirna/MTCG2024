package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.SessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SessionService {

    private SessionRepository sessionRepository;

    public SessionService() {
        sessionRepository = new SessionRepository(new UnitOfWork());
    }

    public Response handlePostRequest(Request request){
        try {

            String bodyString = request.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode requestBody = mapper.readTree(bodyString);
            // Get the username and password from the request body
            String username = requestBody.get("Username").asText();
            String password = requestBody.get("Password").asText();

            boolean authenticateUser = sessionRepository.authenticateUser(username, password);
            String response;

            if (authenticateUser){

                response = "User login successful for: " + username + "\n";
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
            }else{
                response = "Username or Password wrong";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            String response = "Invalid JSON data provided";
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, response);

        }
    }
}