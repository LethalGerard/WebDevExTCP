import javax.swing.*;
import java.io.IOException;

public class Main {
    void main() {
        String userName = JOptionPane.showInputDialog(null, "Enter your name: ", "Username", JOptionPane.QUESTION_MESSAGE);
        Client c = new Client(userName);
    }
}