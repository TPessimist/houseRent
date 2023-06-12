import java.text.SimpleDateFormat;
import java.util.Date;
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
            JOptionPane.showMessageDialog(frame, message);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void createUI() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
    frame = new JFrame("租客客户端");
    frame.setSize(500, 200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

    frame.setVisible(true);
  }

  public static void main(String[] args) throws IOException {
    new TenantClient();
  }
}