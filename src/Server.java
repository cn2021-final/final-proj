import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import server.ConnectionHandler;
import server.UserManager;

public class Server {
    ServerSocket socket;
    UserManager userManager;
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
        userManager = new UserManager();
    }

    private void run() {
        while(true) {
            try {
                Socket s = socket.accept();
                new ConnectionHandler(userManager, s);
            }
            catch(IOException e) {
                System.out.print("failed to accept new connection: ");
                System.out.println(e);
            }
        }
    }
}