package proj;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import proj.server.ConnectionHandler;

public class Server {
    ServerSocket socket;
    public static void main(String[] argv) {
        new Server(Integer.parseInt(argv[0])).run();
    }

    private Server(int port) {
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
                System.out.print("accept: ");
                System.out.println(e);
            }
        }
    }
}