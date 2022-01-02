package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    DataInputStream input;
    DataOutputStream output;
    Socket socket;
    File serverDirectory;

    public ConnectionHandler(File directory, Socket s) {
        serverDirectory = directory;
        socket = s;
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
            new Login(input, output, serverDirectory).run();
            socket.close();
        }
        catch(IOException e) {}
    }

}
