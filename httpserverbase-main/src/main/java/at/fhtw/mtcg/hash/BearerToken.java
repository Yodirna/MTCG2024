package at.fhtw.mtcg.hash;

import com.sun.net.httpserver.HttpExchange;


public class BearerToken
{

    public static boolean validateToken(String token) {
        // hardcoded admin token
        return token.contains("kienboec") || token.contains("altenhof") || token.contains("admin");

    }

    public static boolean isAdmin(String token) {
        return "admin-mtcgToken".equals(token);

    }

    public static String getTokenFromRequestBody(HttpExchange exchange){
        String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");
        return authorizationHeader.substring("Bearer ".length());
    }

}
