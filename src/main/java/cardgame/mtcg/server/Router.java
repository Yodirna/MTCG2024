package cardgame.mtcg.server;

import cardgame.mtcg.models.*;
import cardgame.mtcg.utils.TokenGenerator;
import cardgame.mtcg.models.User;

import java.util.HashMap;
import java.util.Map;

public class Router {
    // In-memory storage for users
    private static Map<String, User> users = new HashMap<>();

    public static String route(String request) {
        String[] lines = request.split("\r\n");
        String[] requestLine = lines[0].split(" ");

        String method = requestLine[0];
        String path = requestLine[1];

        String body = "";
        // Get the body of the request if it's a POST or PUT
        if (method.equals("POST") || method.equals("PUT")) {
            int emptyLineIndex = -1;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].isEmpty()) {
                    emptyLineIndex = i;
                    break;
                }
            }
            if (emptyLineIndex != -1 && emptyLineIndex < lines.length - 1) {
                // Read the rest of the body
                StringBuilder bodyBuilder = new StringBuilder();
                for (int i = emptyLineIndex + 1; i < lines.length; i++) {
                    bodyBuilder.append(lines[i]);
                    if (i < lines.length - 1) {
                        bodyBuilder.append("\r\n");
                    }
                }
                body = bodyBuilder.toString();
            }
        }

        // Routing
        if (method.equals("POST") && path.equals("/users")) {
            return handleRegister(body);
        } else if (method.equals("POST") && path.equals("/sessions")) {
            return handleLogin(body);
        } else {
            return httpResponse(404, "Not Found", "{\"message\":\"Endpoint not found\"}");
        }
    }

    private static String handleRegister(String body) {
        // Parse the JSON body into UserCredentials
        Map<String, String> params = parseBody(body);
        String username = params.get("Username");
        String password = params.get("Password");

        if (username == null || password == null) {
            return httpResponse(400, "Bad Request", "{\"message\":\"Username and Password required\"}");
        }

        if (users.containsKey(username)) {
            return httpResponse(409, "Conflict", "{\"message\":\"User with same username already registered\"}");
        } else {
            User user = new User(username, password);
            users.put(username, user);
            return httpResponse(201, "Created", "{\"message\":\"User successfully created\"}");
        }
    }

    private static String handleLogin(String body) {
        Map<String, String> params = parseBody(body);
        String username = params.get("Username");
        String password = params.get("Password");

        User user = users.get(username);

        if (user != null && user.getPassword().equals(password)) {
            // Generate token
            String token = username + "-mtcgToken";
            user.setAuthToken(token);

            // Return the token in the response body
            String responseBody = "{\"token\":\"" + token + "\"}";
            return httpResponse(200, "OK", responseBody);
        } else {
            return httpResponse(401, "Unauthorized", "{\"message\":\"Invalid username/password provided\"}");
        }
    }

    private static Map<String, String> parseBody(String body) {
        // Simple JSON parsing without using external libraries
        Map<String, String> params = new HashMap<>();
        body = body.trim();
        body = body.replace("{", "").replace("}", "").replace("\"", "");
        String[] pairs = body.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                params.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return params;
    }

    private static String httpResponse(int statusCode, String statusText, String body) {
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        return response;
    }
}
