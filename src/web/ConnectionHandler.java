package web;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import web.http.HTTPRequest;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    public ConnectionHandler(Socket s) {
        socket = s;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            System.err.println("new connection");
            HTTPRequest request = HTTPRequest.parseRequest(new DataInputStream(socket.getInputStream()));
            System.err.println(request.getClass()); 
            socket.close();
        }
        catch(IOException e) {}
    }
}
