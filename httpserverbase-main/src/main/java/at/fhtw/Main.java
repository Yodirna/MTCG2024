package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.mtcg.controllers.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserController());
        router.addService("/sessions", new SessionController());
        router.addService("/packages", new PackageController());
        router.addService("/transactions", new TransactionController());
        router.addService("/cards", new CardController());
        router.addService("/deck", new DeckController());
        router.addService("/stats", new StatsController());
        router.addService("/scoreboard", new ScoreboardController());
        router.addService("/battles", new FightController());
        router.addService("/tradings", new TradingController());
        router.addService("/rewards", new RewardController());



        return router;
    }
}
