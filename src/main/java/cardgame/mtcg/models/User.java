package cardgame.mtcg.models;


public class User {
    private String username;
    private String password;
    private String authToken;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.authToken = null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }
}

