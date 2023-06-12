import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8000;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;

    public Client() throws IOException {
        socket = new Socket(HOST, PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        createUI();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String message = reader.readLine();
                        messageArea.append(message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void createUI() {
        frame = new JFrame("收租系统");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        inputField = new JTextField();
        JButton sendButton = new JButton("发送");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                writer.println(message);
                inputField.setText("");
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }
}
