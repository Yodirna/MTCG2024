package cardgame.mtcg.utils;

import lombok.Data;
import java.util.UUID;

public class TokenGenerator {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
