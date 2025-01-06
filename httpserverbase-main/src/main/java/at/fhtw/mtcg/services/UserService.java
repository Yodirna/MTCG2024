package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.UserCredentials;
import at.fhtw.mtcg.hash.BearerToken;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static at.fhtw.mtcg.hash.BearerToken.*;
import static at.fhtw.mtcg.hash.Hash.hashPassword;

public class UserService {

    private UserRepository userRepository;
    public UserService() {
        userRepository = new UserRepository(new UnitOfWork());
    }



    // Handle user registration requests
    public Response handlePostRequest(Request request) {
        try {

            String bodyString = request.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode requestBody = mapper.readTree(bodyString);
            // Retrieve the username and password from the request body
            String username = requestBody.get("Username").asText();
            String password = requestBody.get("Password").asText();

            // Create a userCredentials object from the provided username and password
            UserCredentials userCredentials = new UserCredentials(username, password);

            boolean userExists = userRepository.checkIfUsernameIsTaken(userCredentials.getUsername());

            if (!userExists){
                // Hash the password before storing in the database
                String securePassword = hashPassword(password);
                // Save the new user record in the database
                if (userRepository.regUser(userCredentials.getUsername(), securePassword)){
                    // Finalize and close the database connection after successful registration
                    String response = "User registration successful for: " + userCredentials.getUsername() + "\n";
                    return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, response);
                }else {
                    // Finalize and close the database connection if registration fails
                    String response = "User registration failed: " + userCredentials.getUsername() + "\n";
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                }



            }else{
                // If a user with the same username already exists, respond with a conflict and close the connection
                String response = "User with same username already registered";
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);


            }

        } catch (Exception e){
            // If the request body is invalid or not parsable as JSON, return 400 Bad Request
            e.printStackTrace();
            String response = "invalid JSON data provided!";
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, response);

        }
    }

    // Handle a GET request to retrieve user data
    public Response handleGetReq(Request request){

        // Extract the authorization token from the request
        String token = request.getAuthorizationToken();

        // Validate the provided token to ensure the request is authorized
        if (!BearerToken.validateToken(token)) {
            String response = "Invalid Token!";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }

        // Extract the username from the request path
        List<String> pathParts = request.getPathParts();
        String username = pathParts.get(1);

        // Check permissions: ensure the token corresponds to the requested user
        if (token.contains("altenhof") && username.equals("altenhof")){
            String userData = userRepository.get_user_data("altenhof");
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, userData);

        }else if (token.contains("kienboec") && username.equals("kienboec")){
            String userData = userRepository.get_user_data("kienboec");
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, userData);

        }else{
            String response = "Access token is missing or invalid";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }
    }

    // Handle a PUT request to update user profile data
    public Response handlePutReq(Request request) {


        // Extract the authorization token to validate the update request
        String token = request.getAuthorizationToken();

        boolean allTokenChecks = validateToken(token);
        if (!allTokenChecks){
            String response = "Access token is missing or invalid!";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }


        // Retrieve the path parameters to identify the target username
        List<String> pathParts = request.getPathParts();
        String usernameFromPath = pathParts.get(1);


        try {
            String bodyString = request.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode requestBody = mapper.readTree(bodyString);

            // Parse the request body to extract the new profile data (Name, Bio, Image)
            String name = requestBody.get("Name").asText();
            String bio = requestBody.get("Bio").asText();
            String image = requestBody.get("Image").asText();

            // Validation checks: a user can only update their own profile data
            // If token matches 'altenhof' and requested path is 'altenhof', proceed with update
            if (token.contains("altenhof") && usernameFromPath.equals("altenhof")){
                boolean updateUserData = userRepository.updateUserData(usernameFromPath, name, bio, image);
                if (updateUserData){
                    String response = "Users Data Updated! ";
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
                }else {
                    String response = "Insufficient privileges to update data!";
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
                }

                // If token matches 'kienboec' and requested path is 'kienboec', proceed with update
            }else if (token.contains("kienboec") && usernameFromPath.equals("kienboec")){
                boolean updateUserData = userRepository.updateUserData(usernameFromPath, name, bio, image);
                if (updateUserData){
                    String response = "users Data Updated! ";
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
                }else {
                    String response = "Insufficient privileges to update data!";
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
                }

            }else{
                // If token does not correspond to the requested username, return 403 Forbidden
                String response = "Access token is missing or invalid!";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }
        }catch (Exception e){
            e.printStackTrace();
            // In case of any internal error, return 500 Internal Server Error
            String response = "Internal Server Error!";
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, response);
        }
    }

}
