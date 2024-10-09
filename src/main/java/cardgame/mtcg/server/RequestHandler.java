package cardgame.mtcg.server;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private Socket clientSocket;

    public RequestHandler(Socket socket) {
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
            String line;
            StringBuilder requestBuilder = new StringBuilder();
            while (!(line = reader.readLine()).isEmpty()) {
                requestBuilder.append(line + "\r\n");
            }

            String request = requestBuilder.toString();
            System.out.println("Received request:\n" + request);

            // Handle the request
            String response = Router.route(request);

            // Send the HTTP response
            writer.write(response);
            writer.flush();

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
