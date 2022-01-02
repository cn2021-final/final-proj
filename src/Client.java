import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import client.Login;

public class Client {
    Socket socket;
    DataInputStream input;
    DataOutputStream output;
    public static void main(String[] argv) {
        new Client(argv[0], Integer.parseInt(argv[1])).run();
    }

    public Client(String addr, int port) {
        try {
            socket = new Socket(addr, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e) {
            System.out.format("Can't connect to the server at %s:%d: ", addr, port);
            System.out.println(e);
            System.exit(1);
        }
    }

    private void run() {
        try {
            new Login(input, output).run();
            socket.close();
        }
        catch(IOException e) {}
    }
}