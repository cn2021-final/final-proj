package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    DataInputStream input;
    DataOutputStream output;
    Socket socket;
    UserManager userManager;

    public ConnectionHandler(UserManager userManager, Socket socket) {
        this.userManager = userManager;
        this.socket = socket;
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e) {
            // IDC
            return;     
        }
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            new Login(input, output, userManager).run();
            socket.close();
        }
        catch(IOException e) {}
        System.out.println("Child thread terminated.");
    }
}
