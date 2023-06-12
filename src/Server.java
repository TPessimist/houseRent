import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        System.out.println("服务器已启动，等待客户端连接...");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("客户端已连接：" + socket.getInetAddress().getHostAddress());
            ClientHandler client = new ClientHandler(socket, this);
            clients.add(client);
            client.start();
        }
    }

    public void broadcast(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(8000);
        server.start();
    }
}