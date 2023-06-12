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

public class TenantClient {

  private static final String HOST = "localhost";
  private static final int PORT = 8000;

  private final Socket socket;
  private final BufferedReader reader;
  private final PrintWriter writer;
  private JFrame frame;
  private JTextField delayField;
  private JTextArea tenantMessageArea;
  private JFrame messageFrame;

  private JTextField inputField;

  public TenantClient() throws IOException {
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
            System.out.println(message);
            switch (parts[0]) {
              //房东设置新房价
              case "SET_PRICE": {
                JOptionPane.showMessageDialog(frame, message);
              }
              //房东发送消息
              case "landerMsg": {
                System.out.println("租客收到来自租客的消息:" + message);
                String messageBody = parts[1];
                String[] contents = messageBody.split("body");
                for (String content : contents) {
                  tenantMessageArea.append(content + "\n");
                }
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
    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
    SimpleDateFormat detailFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    frame = new JFrame("租客客户端");
    messageFrame = new JFrame("租客聊天框");
    frame.setSize(500, 200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    messageFrame.setSize(500, 500);
    messageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    tenantMessageArea = new JTextArea();
    tenantMessageArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(tenantMessageArea);
    inputField = new JTextField();
    JButton sendButton = new JButton("发送");
    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String message = inputField.getText();
        String date = detailFormat.format(new Date()) + "body";
        message = date + "租客：" + message;
        List<String> contents = Arrays.asList(message.split("body"));
        tenantMessageArea.append(contents.get(0) + "\n" + contents.get(1) + "\n");
        writer.println("tenantMsgACTION" + message);
        inputField.setText("");
      }
    });
    JLabel priceLabel = new JLabel("当前房租价格：");
    JLabel currentPriceLabel = new JLabel("100");
    JLabel deadlineLabel = new JLabel("房租到期日：");
    JLabel currentDeadlineLabel = new JLabel(format.format(new Date()));
    JLabel delayLabel = new JLabel("申请延期交钱（天数）：");
    delayField = new JTextField(10);
    JButton delayButton = new JButton("申请延期交钱");
    delayButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String delayDays = delayField.getText();
        if (delayDays.isEmpty()) {
          JOptionPane.showMessageDialog(frame, "请填写延期天数");
        } else {
          writer.println("DELAY_PAYMENT:" + delayDays);
        }
        delayField.setText("");
      }
    });

    JPanel pricePanel = new JPanel(new GridLayout(2, 2));
    pricePanel.add(priceLabel);
    pricePanel.add(currentPriceLabel);
    pricePanel.add(deadlineLabel);
    pricePanel.add(currentDeadlineLabel);

    JPanel delayPanel = new JPanel(new GridLayout(2, 2));
    delayPanel.add(delayLabel);
    delayPanel.add(delayField);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(delayButton);

    frame.add(pricePanel, BorderLayout.NORTH);
    frame.add(delayPanel, BorderLayout.CENTER);
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
    new TenantClient();
  }


}