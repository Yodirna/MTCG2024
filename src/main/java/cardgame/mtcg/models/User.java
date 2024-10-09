package cardgame.mtcg.models;

import cardgame.mtcg.models.Deck;
import cardgame.mtcg.models.Stack;

import java.util.UUID;

public class User {
    private String username;
    private String password;
    private String authToken;
    private int coins;
    private Stack stack;
    private Deck deck;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.authToken = null;
        this.coins = 20; // Initially 20 coins
        this.stack = new Stack();
        this.deck = new Deck();
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

    public int getCoins() {
        return coins;
    }

    public void deductCoins(int amount) {
        this.coins -= amount;
    }

    public Stack getStack() {
        return stack;
    }

    public Deck getDeck() {
        return deck;
    }
}
