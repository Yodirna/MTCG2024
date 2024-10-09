package cardgame.mtcg.server;


import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;

public class Server {
    private int port;
    private boolean isRunning = false;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        isRunning = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                // Handle client connection in a new thread
                new Thread(new RequestHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

