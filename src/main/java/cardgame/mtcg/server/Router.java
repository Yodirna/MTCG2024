package cardgame.mtcg.server;

import cardgame.mtcg.models.*;
import cardgame.mtcg.utils.TokenGenerator;
import cardgame.mtcg.models.User;

import java.util.HashMap;
import java.util.Map;

import cardgame.mtcg.services.UserService;
import cardgame.mtcg.services.SessionService;


public class Router {
    public static Response route(Request request) {
        String serviceRoute = request.getServiceRoute();
        Service service = null;

        if (serviceRoute == null) {
            return new Response(400, "Bad Request", "{\"message\":\"Bad Request\"}");
        }

        switch (serviceRoute) {
            case "/users":
                service = new UserService();
                break;
            case "/sessions":
                service = new SessionService();
                break;
            default:
                return new Response(404, "Not Found", "{\"message\":\"Endpoint not found\"}");
        }

        try {
            return service.handleRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(500, "Internal Server Error", "{\"message\":\"Internal Server Error\"}");
        }
    }
}

