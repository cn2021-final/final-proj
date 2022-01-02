import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import server.ConnectionHandler;

public class Server {
    ServerSocket socket;
    File serverDirectory;
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
        serverDirectory = new File("server");
        serverDirectory.mkdirs();
    }

    private void run() {
        while(true) {
            try {
                Socket s = socket.accept();
                new ConnectionHandler(serverDirectory, s);
            }
            catch(IOException e) {
                System.out.print("failed to accept new connection: ");
                System.out.println(e);
            }
        }
    }
}