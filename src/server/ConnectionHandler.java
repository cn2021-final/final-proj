package server;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    Socket socket;

    public ConnectionHandler(Socket s) {
        socket = s;
        new Thread(this).start();
    }

    @Override
    public void run() {
        // TODO
    }

}
