package cardgame.mtcg.server;

public class Response {
    private int statusCode;
    private String statusText;
    private String contentType = "application/json";
    private String body = "";

    public Response(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public Response(int statusCode, String statusText, String body) {
        this(statusCode, statusText);
        this.body = body;
    }

    public String getResponseString() {
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        return response;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
