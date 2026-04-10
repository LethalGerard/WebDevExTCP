import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    int port = 8888;
    Map<String, PrintWriter> clients = new ConcurrentHashMap<>();
    ServerSocket serverSocket;


    public Server() {

        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String userName = in.readLine();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.put(userName, out);
                ClientHandler clientHandler = new ClientHandler(in, userName, out);

                broadCastMsg("Connected: " + userName);

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
        for (PrintWriter pw : clients.values()) {
            pw.println(msg);
        }
    }

    class ClientHandler implements Runnable {
        BufferedReader in;
        String userName;
        PrintWriter out;

        public ClientHandler(BufferedReader in, String userName, PrintWriter out) {
            this.in = in;
            this.userName = userName;
            this.out = out;

            for (String name : clients.keySet()) {
                out.println("Connected: " + name);
            }
        }

        @Override
        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    broadCastMsg(userName + ": " + inputLine);
                }
                broadCastMsg("Disconnected: " + userName);
                clients.remove(userName);
            } catch (IOException ex) {
                broadCastMsg("Disconnected: " + userName);
                clients.remove(userName);
                throw new RuntimeException(ex);
            }
        }
    }
    void main() {
        Server s = new Server();
    }
}
