import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

  private final Socket socket;
  private final BufferedReader reader;
  private final PrintWriter writer;
  private JFrame frame;

  private JFrame messageFrame;
  private JTextField priceField;

  private JTextArea landLordMessageArea;

  private JTextField inputField;

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
            String message = reader.readLine();
            String[] parts = message.split("ACTION");
            switch (parts[0]) {
              //租客延期
              case "DELAY_PAYMENT": {
                JOptionPane.showMessageDialog(frame, message);
              }
              //租客发送消息
              case "tenantMsg": {
                System.out.println("房东收到来自租客的消息:" + message);
                String messageBody = parts[1];
                String[] contents = messageBody.split("body");
                for (String content : contents) {
                  landLordMessageArea.append(content + "\n");
                }
              }
              //房东发送消息
              case "LANDER_MSG": {

              }
            }

          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void createUI() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    frame = new JFrame("房东客户端");
    messageFrame = new JFrame("房东聊天框");
    frame.setSize(500, 500);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    messageFrame.setSize(500, 500);
    messageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JLabel priceLabel = new JLabel("当前房租价格：");
    JLabel currentPriceLabel = new JLabel("100");
    JLabel newPriceLabel = new JLabel("新的房租价格：");
    priceField = new JTextField("", 1);
    JButton setPriceButton = new JButton("设置房租价格");
    landLordMessageArea = new JTextArea();
    landLordMessageArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(landLordMessageArea);
    inputField = new JTextField();
    JButton sendButton = new JButton("发送");
    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String message = inputField.getText();
        String date = format.format(new Date()) + "body";
        message = date + "房东:" + message;
        String[] contents = message.split("body");
        for (String content : contents) {
          landLordMessageArea.append(content + "\n");
        }
        writer.println("landerMsgACTION" + message);
        inputField.setText("");
      }
    });
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
//    frame.setVisible(true);

    JPanel inputPanel = new JPanel(new BorderLayout());
    inputPanel.add(inputField, BorderLayout.CENTER);
    inputPanel.add(sendButton, BorderLayout.EAST);
    messageFrame.add(scrollPane, BorderLayout.CENTER);
    messageFrame.add(inputPanel, BorderLayout.SOUTH);
    messageFrame.setVisible(true);

  }

  public static void main(String[] args) throws IOException {
    new LandlordClient();
  }
}
