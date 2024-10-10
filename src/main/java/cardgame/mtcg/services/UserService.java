package cardgame.mtcg.services;

import cardgame.mtcg.models.User;
import cardgame.mtcg.server.Service;
import cardgame.mtcg.server.Request;
import cardgame.mtcg.server.Response;
import java.util.HashMap;
import java.util.Map;

public class UserService implements Service {
    // Shared user storage
    private static Map<String, User> users = new HashMap<>();

    public static Map<String, User> getUsers() {
        return users;
    }

    @Override
    public Response handleRequest(Request request) {
        String method = request.getMethod();
        if (method.equals("POST")) {
            return handleRegister(request);
        }
        return new Response(404, "Not Found", "{\"message\":\"Endpoint not found\"}");
    }

    private Response handleRegister(Request request) {
        Map<String, String> params = parseBody(request.getBody());
        String username = params.get("Username");
        String password = params.get("Password");

        if (username == null || password == null) {
            return new Response(400, "Bad Request", "{\"message\":\"Username and Password required\"}");
        }

        if (users.containsKey(username)) {
            return new Response(409, "Conflict", "{\"message\":\"User already exists\"}");
        } else {
            User user = new User(username, password);
            users.put(username, user);
            return new Response(201, "Created", "{\"message\":\"User successfully created\"}");
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

