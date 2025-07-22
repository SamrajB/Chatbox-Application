import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Java Chat Box");
    private JTextArea messageArea = new JTextArea(20, 50);
    private JTextField inputField = new JTextField(40);
    private JButton sendButton = new JButton("Send");
    private String username;

    public Client(String serverAddress, String username) throws IOException {
        this.username = username;

        // Connect to the server
        Socket socket = new Socket(serverAddress, 1234);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // GUI setup
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Welcome message
        messageArea.append("Connected as " + username + "\n");

        // Action: Enter key
        inputField.addActionListener(e -> sendMessage());
        // Action: Send button
        sendButton.addActionListener(e -> sendMessage());

        // Message receiving thread
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    messageArea.append(line + "\n");
                }
            } catch (IOException e) {
                messageArea.append("Connection lost.\n");
            }
        }).start();
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            out.println(username + ": " + text);
            inputField.setText("");
        }
    }

    public static void main(String[] args) throws Exception {
        String serverAddress = JOptionPane.showInputDialog(null, "Enter Server IP Address:", "Welcome to Chat", JOptionPane.QUESTION_MESSAGE);
        if (serverAddress == null || serverAddress.trim().isEmpty()) return;

        String username = JOptionPane.showInputDialog(null, "Enter your name:", "Username", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) return;

        new Client(serverAddress.trim(), username.trim());
    }
}