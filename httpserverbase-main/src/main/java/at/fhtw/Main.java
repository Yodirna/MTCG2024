package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.mtcg.services.*;

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

    // Service mappings
    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserService());
        router.addService("/sessions", new SessionsService());
        router.addService("/packages", new PackagesService());
        router.addService("/transactions", new PackageAcquisitionService());
        router.addService("/cards", new CardsService());
        router.addService("/deck", new DeckService());
        router.addService("/battles", new LobbyService());
        router.addService("/stats", new StatsService());
        router.addService("/scoreboard", new ScoreboardService());
        router.addService("/tradings", new TradingService());
        router.addService("/rewards", new RewardsService());

        return router;
    }
}
