package at.fhtw.mtcg.hash;

import com.sun.net.httpserver.HttpExchange;


public class BearerToken
{

    public static boolean validateToken(String token) {
        //für hard coded
        if (token.contains("kienboec") || token.contains("altenhof") || token.contains("admin")){
            return true;
        }
        return false;

    }
    public static boolean isAdmin(String token) {
        if ("admin-mtcgToken".equals(token)) {
            return true;
        }
        return false;

    }

    public static String getTokenFromRequestBody(HttpExchange exchange){
        String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");
        String token = authorizationHeader.substring("Bearer ".length());
        return token;
    }

}
