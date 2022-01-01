package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    DataInputStream input;
    DataOutputStream output;

    public ConnectionHandler(Socket s) {
        try {
            input = new DataInputStream(s.getInputStream());
            output = new DataOutputStream(s.getOutputStream());
        }
        catch(IOException e) {
            // IDC, just disconnect
            try { s.close(); } catch(Exception ignore) {}
            return;     
        }
        new Thread(this).start();
    }

    @Override
    public void run() {
        // TODO: implement the spec
    }

}
