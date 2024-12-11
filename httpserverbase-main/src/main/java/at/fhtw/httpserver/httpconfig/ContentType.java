package at.fhtw.httpserver.httpconfig;

public enum ContentType {
    PLAIN_TEXT("text/plain"),
    HTML("text/html"),
    JSON("application/json");

    public final String type;

    ContentType(String type) {
        this.type = type;
    }
}
