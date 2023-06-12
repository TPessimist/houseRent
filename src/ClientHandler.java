import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

  private final Socket socket;
  private final Server server;
  private final BufferedReader reader;
  private final PrintWriter writer;

  public ClientHandler(Socket socket, Server server) throws IOException {
    this.socket = socket;
    this.server = server;
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    writer = new PrintWriter(socket.getOutputStream(), true);
  }

  public void sendMessage(String message) throws IOException {
    writer.println(message);
  }

  @Override
  public void run() {
    try {
      while (true) {
        String message = reader.readLine();
        if (message == null) {
          break;
        }
        System.out.println("收到客户端消息：" + message);
        String[] parts = message.split(":");
        if (parts[0].equals("SET_PRICE")) {
          server.broadcast("房东设置了新的房租价格：" + parts[1], this);
        } else if (parts[0].equals("DELAY_PAYMENT")) {
          server.broadcast("租客申请延期交钱：" + parts[1] + "天", this);
        } else {
          server.broadcast(message, this);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
        writer.close();
        socket.close();
        server.removeClient(this);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}