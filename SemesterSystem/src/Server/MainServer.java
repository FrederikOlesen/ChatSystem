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
    
    static Map<String,ClientHandler> onlineUsers = new HashMap<>();
    private static List<ClientHandler> onlineUsersCC = new ArrayList<>();

    public static void stopServer() {
        keepRunning = false;
    }
//
//    public static ArrayList printUsers() {
//
//        for (int i = 0; i < onlineUsers.size(); i++) {
//            System.out.println("Prints from Server printUser: " + onlineUsers.get(i));
//        }
//
//        return onlineUsers;

    

    public static void sendUsers() {
        for (ClientHandler c : onlineUsersCC) {
           // c.sendUsers(onlineUsersCC);
        }
    }

    public static void send(String msg) {
        for (ClientHandler c : clients) {
            c.send(msg);
//            String input = msg;
//            String output = input.substring(0, input.indexOf(':'));
//            if (onlineUsers.contains(output)) {
//
//            } else {
//                onlineUsers.add(output);
//            }
        }
        System.out.println("Server message was: " + msg);

        //printUsers();
    }

    public static void removeHandler(ClientHandler ch) {
        clients.remove(ch);
    }

    public static void main(String[] args) {
        new MainServer().handleConnections();
    }
    
    public void addClient(String userName, ClientHandler ch)
    {
        onlineUsers.put(userName, ch); 
        sendOnline();
    }
    
    private void sendOnline()
    {
        String onlineMessage = "ONLINE#";
        
        for(String user: onlineUsers.keySet())
        {
            onlineMessage += user + ","; 
            
        }
        
        for(ClientHandler ch : onlineUsers.values())
        {
            ch.send(onlineMessage);
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
                ClientHandler clientHandler = new ClientHandler(socket,this);
                new Thread(clientHandler).start();
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Utils.closeLogger(MainServer.class.getName());
        }
    }
}
