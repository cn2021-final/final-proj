package web;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import web.http.HTTPRequest;
import web.http.HybridInputStream;
import web.http.request.PostRequest;
import web.http.response.NotFoundResponse;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    public ConnectionHandler(Socket s) {
        socket = s;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            // TODO: this is an example of connection handling
            System.err.println("new connection");
            HybridInputStream input = new HybridInputStream(socket.getInputStream());
            PostRequest request = (PostRequest) HTTPRequest.parseRequest(input);
            ByteArrayOutputStream buffer =  new ByteArrayOutputStream();
            request.readData(buffer);
            System.err.println(request.getClass());
            new NotFoundResponse().WriteResponse(new DataOutputStream(socket.getOutputStream()));
            socket.close();
        }
        catch(IOException e) {
            System.err.println(e);
        }
    }
}
