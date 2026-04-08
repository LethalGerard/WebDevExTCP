import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    int port = 8888;
    List<Socket> clients = new ArrayList<>();
    ServerSocket serverSocket;


    public Server() {

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket clientSocket;
                clients.add(clientSocket = serverSocket.accept());
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                Thread handlerThread = new Thread(clientHandler);
                handlerThread.start();

                IO.println("New client connected");
            }

        } catch (BindException e) {
            IO.println("Port already in use");
        } catch (IOException ex) {
            IO.println("Could not start server");
        }
    }

    public void broadCastMsg(String msg) {
        for (Socket socket : clients) {
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                pw.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ClientHandler implements Runnable {
        Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader((clientSocket.getInputStream())));
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    broadCastMsg(inputLine);
                }
                clients.remove(clientSocket);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
