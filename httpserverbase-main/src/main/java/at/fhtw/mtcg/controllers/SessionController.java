package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.SessionRepository;
import at.fhtw.mtcg.security.Token;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionController {

    private final SessionRepository sessionRepository;

    public SessionController() {
        sessionRepository = new SessionRepository(new UnitOfWork());
    }

    public Response handlePostRequest(Request request) {
        try {
            String bodyString = request.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode requestBody = mapper.readTree(bodyString);

            // Extract username and password
            String username = requestBody.get("Username").asText();
            String password = requestBody.get("Password").asText();

            // Authenticate user
            boolean authenticateUser = sessionRepository.authenticateUser(username, password);

            if (authenticateUser) {
                // Generate token for the authenticated user
                String token = Token.generateToken(username);

                // Return token in response
                String response = "User login successful for: " + username + "\nToken: " + token;
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
            } else {
                // Authentication failed
                String response = "Username or Password is incorrect";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String response = "Invalid JSON data provided";
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, response);
        }
    }
}
