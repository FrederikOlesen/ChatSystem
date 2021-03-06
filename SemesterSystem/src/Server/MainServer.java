package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utils.Utils;
import java.util.HashMap;
import java.util.Map;

public class MainServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private static List<ClientHandler> clients = new ArrayList<>();
    static Map<String, ClientHandler> onlineUsers = new HashMap<>();

    public static void stopServer() {
        keepRunning = false;
    }

    public static void send(String userName, String msg) {
        String all = "*";
        String user = userName;
        System.out.println(user);
        if (onlineUsers.containsKey(user)) {
            ClientHandler ch = onlineUsers.get(user);
            ch.send(ch, msg);
        }
        if (userName.equals(all)) {
            ArrayList<ClientHandler> as = new ArrayList<>();
            as.addAll(onlineUsers.values());

            for (int i = 0; i < as.size(); i++) {
                ClientHandler ch = as.get(i);
                ch.send(ch, msg);

            }
        }
    }

    public static void removeHandler(ClientHandler ch) {
        clients.remove(ch);
    }

    public static void main(String[] args) {
        new MainServer().handleConnections();
    }

    public void addClient(String userName, ClientHandler ch) {
        onlineUsers.put(userName, ch);
        sendOnline();
        System.out.println(onlineUsers.size());

    }

    private void sendOnline() {
        String onlineMessage = "ONLINE#";

        for (String user : onlineUsers.keySet()) {
            onlineMessage += user + ",";

        }

        for (ClientHandler ch : onlineUsers.values()) {
            ch.sendOnline(onlineMessage);
        }

    }

    private void handleConnections() throws NumberFormatException {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");
        String logFile = properties.getProperty("logFile");
        Logger.getLogger(MainServer.class.getName()).log(Level.INFO, "Sever started");
        try {
            Utils.setLogFile(logFile, MainServer.class.getName());
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(MainServer.class.getName()).log(Level.INFO, "Connected to a client");
                ClientHandler clientHandler = new ClientHandler(socket, this);
                new Thread(clientHandler).start();
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Utils.closeLogger(MainServer.class.getName());
        }
    }
}
