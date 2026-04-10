import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {
    ChatWindowGUI gui;
    String userName;
    Socket clientSocket;
    PrintWriter out;

    public Client(String userName) {
        this.userName = userName;
        String hostName = "127.0.0.1";
        int port = 8888;

        gui = new ChatWindowGUI(userName, this);

        try {
            clientSocket = new Socket(hostName, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(userName);
            Thread clientThread = new Thread(this);
            clientThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader((clientSocket.getInputStream())));
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("Connected:")) {
                    gui.addMemberToList(inputLine.substring(11));
                } else if (inputLine.startsWith("Disconnected:")) {
                    gui.removeUserFromList(inputLine.substring(13));
                } else {
                    gui.addMsgToChat(inputLine);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

void main() {
    Client c = new Client(userName);
}
