import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LandlordClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8000;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private JFrame frame;
    private JTextField priceField;

    public LandlordClient() throws IOException {
        socket = new Socket(HOST, PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        createUI();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String message =reader.readLine();
                        JOptionPane.showMessageDialog(frame, message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void createUI() {
        frame = new JFrame("房东客户端");
        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel priceLabel = new JLabel("当前房租价格：");
        JLabel currentPriceLabel = new JLabel("未设置");
        JLabel newPriceLabel = new JLabel("新的房租价格：");
        priceField = new JTextField(10);
        JButton setPriceButton = new JButton("设置房租价格");
        setPriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newPrice = priceField.getText();
                if (newPrice.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "房租价格不能为空");
                } else {
                    writer.println("SET_PRICE:" + newPrice);
                }
                priceField.setText("");
            }
        });

        JPanel pricePanel = new JPanel(new GridLayout(2, 2));
        pricePanel.add(priceLabel);
        pricePanel.add(currentPriceLabel);
        pricePanel.add(newPriceLabel);
        pricePanel.add(priceField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(setPriceButton);

        frame.add(pricePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new LandlordClient();
    }
}
