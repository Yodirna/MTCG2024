package cardgame.mtcg.services;

import cardgame.mtcg.models.User;
import cardgame.mtcg.server.Request;
import cardgame.mtcg.server.Response;
import cardgame.mtcg.server.Service;
import cardgame.mtcg.utils.TokenGenerator;

import java.util.Map;
import java.util.HashMap;


public class SessionService implements Service {
    // Access the shared user storage from UserService
    private static Map<String, User> users = UserService.getUsers();

    @Override
    public Response handleRequest(Request request) {
        String method = request.getMethod();
        if (method.equals("POST")) {
            return handleLogin(request);
        }
        return new Response(404, "Not Found", "{\"message\":\"Endpoint not found\"}");
    }

    private Response handleLogin(Request request) {
        Map<String, String> params = parseBody(request.getBody());
        String username = params.get("Username");
        String password = params.get("Password");

        User user = users.get(username);

        if (user != null && user.getPassword().equals(password)) {
            // Generate token using TokenGenerator
            String token = TokenGenerator.generateToken();
            user.setAuthToken(token);

            // Return the token in the response body
            String responseBody = "{\"token\":\"" + token + "\"}";
            return new Response(200, "OK", responseBody);
        } else {
            return new Response(401, "Unauthorized", "{\"message\":\"Invalid username/password provided\"}");
        }
    }

    private Map<String, String> parseBody(String body) {
        Map<String, String> params = new HashMap<>();
        body = body.trim();
        body = body.replace("{", "").replace("}", "").replace("\"", "");
        String[] pairs = body.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return params;
    }
}

