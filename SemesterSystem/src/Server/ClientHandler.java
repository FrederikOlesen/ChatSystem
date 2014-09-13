package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utils.ProtocolStrings;

/**
 *
 * @author frederikolesen
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final Scanner input;
    private final PrintWriter writer;
    MainServer ms;

    public ClientHandler(Socket socket, MainServer ms) throws IOException {
        this.socket = socket;
        this.ms = ms;
        input = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {

            String message = input.nextLine(); //IMPORTANT blocking call
            String[] protocolStrings = message.split("#");
            socket.getOutputStream().flush();
            Logger.getLogger(MainServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
            while (!message.equals(ProtocolStrings.STOP)) {
//                writer.println(message.toUpperCase());
                if (protocolStrings[0].equals("CONNECT")) {
                    ms.addClient(protocolStrings[1], this);
                }
                if (protocolStrings[0].equals("SEND")) {
                    MainServer.send(protocolStrings[1], protocolStrings[2]);
                }
                if (protocolStrings[0].equals("CLOSE")) {
                    System.out.println("closing the socket");
                    socket.close();
                }
                Logger.getLogger(MainServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));

                message = input.nextLine(); //IMPORTANT blocking call
                socket.getOutputStream().flush();
                protocolStrings = message.split("#");
            }
            writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
            socket.close();
            Logger.getLogger(MainServer.class.getName()).log(Level.INFO, "Closed a Connection");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void send(ClientHandler ch, String msg) {
        writer.println(msg);
    }

    public void sendOnline(String msg) {
        writer.println(msg);
    }
}
