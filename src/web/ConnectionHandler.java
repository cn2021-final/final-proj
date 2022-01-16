package web;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import web.http.HTTPRequest;
import web.http.HybridInputStream;
import web.http.request.BadRequest;
import web.http.request.GetRequest;
import web.http.request.PostRequest;
import web.http.request.RequestType;
import web.http.response.BadResponse;
import web.http.response.HTMLResponse;
import web.http.response.ScriptResponse;
import web.http.response.NotFoundResponse;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    private static List<String> locations;

    public ConnectionHandler(Socket s) {
        socket = s;
        locations = List.of(
            "/button.html",
            "/chat.js",
            "/lobby.html",
            "/login.html",
            "/chat.html",
            "/common.js",
            "/lobby.js",
            "/login.js"
        );
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            // Get page
            System.err.println("new connection");
            HybridInputStream input = new HybridInputStream(socket.getInputStream());
            HTTPRequest request = HTTPRequest.parseRequest(input);
            System.err.println(request.getClass());
            if (request.type == RequestType.GET) {
                handle_get((GetRequest) request);
            }
            else if(request.type == RequestType.POST) {
                PostRequestHandler.handle((PostRequest) request, new DataOutputStream(socket.getOutputStream()));
            }
            else if(request.type == RequestType.BAD) {
                handle_bad((BadRequest) request);
            }
            else {
                System.err.println("unrecognised request");
            }
            socket.close();
        }
        catch(IOException e) {
            System.err.println(e);
        }
    }

    private void handle_get(GetRequest request) throws IOException {
        try {
            if (!locations.contains(request.location)) {
                System.err.println(request.location);
                throw new FileNotFoundException("");
            }
            String extension = request.location.split("\\.")[1];
            if (extension.equals("html")) {
                new HTMLResponse("./src/frontend" + request.location)
                    .WriteResponse(new DataOutputStream(socket.getOutputStream()));
            } else if (extension.equals("js")) {
                new ScriptResponse("./src/frontend" + request.location)
                    .WriteResponse(new DataOutputStream(socket.getOutputStream()));
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
            new NotFoundResponse().WriteResponse(new DataOutputStream(socket.getOutputStream()));
        }
    }

    private void handle_bad(BadRequest request) throws IOException {
        new BadResponse().WriteResponse(new DataOutputStream(socket.getOutputStream()));
    }
}
