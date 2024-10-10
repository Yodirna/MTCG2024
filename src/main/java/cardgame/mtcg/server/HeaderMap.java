package cardgame.mtcg.server;

import java.util.HashMap;
import java.util.Map;

public class HeaderMap {
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String HEADER_NAME_VALUE_SEPARATOR = ":";
    private Map<String, String> headers = new HashMap<>();

    public void ingest(String headerLine) {
        final String[] split = headerLine.split(HEADER_NAME_VALUE_SEPARATOR, 2);
        if (split.length == 2) {
            headers.put(split[0].trim(), split[1].trim());
        }
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public int getContentLength() {
        final String header = headers.get(CONTENT_LENGTH_HEADER);
        if (header == null) {
            return 0;
        }
        return Integer.parseInt(header);
    }

    public void print() {
        System.out.println(headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

