package cardgame.mtcg.server;

import java.io.IOException;

public interface Service {
    Response handleRequest(Request request) throws IOException;
}
