import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.ClientLib;
import web.ConnectionHandler;

public class WebServer {
    ServerSocket socket;
    public static void main(String[] argv) {
        ClientLib.init(argv[1], Integer.parseInt(argv[2]));
        new WebServer(Integer.parseInt(argv[0])).run();
    }

    private WebServer(int port) {
        try {
            socket = new ServerSocket(port);
        }
        catch(IOException e) {
            System.out.print("Can't start the server at port " + port + ": ");
            System.out.println(e);
            System.exit(1);
        }
    }

    private void run() {
        while(true) {
            try {
                Socket s = socket.accept();
                new ConnectionHandler(s);
            }
            catch(IOException e) {
                System.out.print("failed to accept new connection: ");
                System.out.println(e);
            }
        }
    }
}
