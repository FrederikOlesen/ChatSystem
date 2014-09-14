/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Server.ClientHandler;
import Utils.ProtocolStrings;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frederikolesen
 */
public class ChatClient extends Thread implements ChatList {

    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;

    List<ChatList> listeners = new ArrayList();

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour

        start();
    }

    public void registerEchoListener(ChatList l) {
        listeners.add(l);
    }

    ;
  
  public void unRegisterEchoListener(ChatList l) {
        listeners.remove(l);
    }

    ;
  
  private void notifyListeners(String msg) {
        for (ChatList l : listeners) {
            l.onlineArrived(msg);
        }
    }

    public boolean validateUser(String userName) {
        return listeners.equals(userName);
    }

    public void send(String recieve, String msg) {
        String msgFormat = "SEND#" + recieve + "#" + msg;
        output.println(msgFormat);
    }

    public void sendUserName(String userName) {
        String msgFormat = "CONNECT#" + userName;
        output.println(msgFormat);
    }

    public void closeSocket(String close) {
        String msgFormat = close + "#";
        output.println(msgFormat);
    }

    public void close() throws IOException {
        output.println(ProtocolStrings.STOP);
    }

    public String sendOnline() {
        String online = input.nextLine();
        System.out.println(online);
        return online;
    }

    public static String setIP(String IP) {
        return IP;
    }

    public static int setPort(int port) {
        return port;
    }

    public static void main(String[] args) {
        int port = setPort(0);
        String ip = setIP("");
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        try {
            ChatClient tester = new ChatClient();
            tester.registerEchoListener(new ChatList() {
                @Override
                public void onlineArrived(String data) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void messageArrived(String userName, String data) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            tester.connect(ip, port);
            System.out.println("Sending 'Hello world'");
            //tester.send("Hello World");
            System.out.println("Waiting for a reply");
            tester.close();
            //System.in.read();      
        } catch (UnknownHostException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String msg = input.nextLine();
        while (!msg.equals(ProtocolStrings.STOP)) {
            notifyListeners(msg);
            msg = input.nextLine();
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onlineArrived(String data) {
        System.out.println("Online arrived: " + data);
    }

    @Override
    public void messageArrived(String userName, String data) {
        System.out.println("Message arrived" + userName + data);
    }

}
