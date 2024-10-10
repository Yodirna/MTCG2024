package cardgame.mtcg.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream input  = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream()
        ) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));

            // Read the HTTP request
            Request request = new Request();

            // Read request line
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            String[] requestParts = requestLine.split(" ");
            request.setMethod(requestParts[0]);
            request.setUrlContent(requestParts[1]);

            // Read headers
            HeaderMap headerMap = new HeaderMap();
            String headerLine;
            while (!(headerLine = reader.readLine()).isEmpty()) {
                headerMap.ingest(headerLine);
            }
            request.setHeaderMap(headerMap);

            // Read body if Content-Length > 0
            int contentLength = headerMap.getContentLength();
            String body = "";
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                reader.read(bodyChars, 0, contentLength);
                body = new String(bodyChars);
            }
            request.setBody(body);

            // Handle the request
            Response response = Router.route(request);

            // Send the HTTP response
            writer.write(response.getResponseString());
            writer.flush();

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
