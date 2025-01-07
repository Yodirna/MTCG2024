package at.fhtw.mtcg.security;

public class Token {

    public static boolean validateToken(String token) {
        // Validate if token contains allowed usernames
        return token.contains("kienboec") || token.contains("altenhof") || token.contains("admin");
    }

    //public static boolean isAdmin(String token) {
    //    return "admin-mtcgToken".equals(token);
    //}

    public static String generateToken(String username) {
        // Generate token in format: <username>-mtcgToken
        return username + "-mtcgToken";
    }
}
