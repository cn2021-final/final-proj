package web;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import client.ClientLib;
import common.chat.ChatLog;
import web.http.request.PostRequest;
import web.http.response.BadResponse;
import web.http.response.JSONResponse;
import web.http.response.GeneralGoodResponse;
import web.http.response.NotFoundResponse;

public class PostRequestHandler {
    public static void handle(PostRequest request, DataOutputStream output) throws IOException {
        System.err.println(request.location);
        switch(request.location) {
            case "/login":
                login(request, output);
                return;
            case "/renew":
                renew(request, output);
                return;
            case "/add":
                add(request, output);
                return;
            case "/delete":
                delete(request, output);
                return;
            case "/refresh":
                refresh(request, output);
                return;
            case "/send-text":
                sendText(request, output);
                return;
        }
        if (request.location.startsWith("/send-file")) {
            sendFile(request, output);
            return;
        }
        if (request.location.startsWith("/send-image")) {
            sendImage(request, output);
            return;
        }
        new NotFoundResponse().WriteResponse(output);
    }

    private static void login(PostRequest request, DataOutputStream output) throws IOException {
        ByteArrayOutputStream input = new ByteArrayOutputStream();
        request.readData(input);
        JSONObject obj;
        try {
            obj = new JSONObject(input.toString());
            String sender = obj.getString("sender");
            ClientLib.onlyLogin(sender);
        }
        catch(JSONException | IOException e) {
            System.err.println(e);
            new BadResponse().WriteResponse(output);
            return;
        }
        new GeneralGoodResponse().WriteResponse(output);
    }

    private static void renew(PostRequest request, DataOutputStream output) throws IOException {
        ByteArrayOutputStream input = new ByteArrayOutputStream();
        request.readData(input);
        JSONObject obj;
        try {
            obj = new JSONObject(input.toString());
            String sender = obj.getString("sender");
            JSONArray friendList = new JSONArray(ClientLib.listFriends(sender));
            new JSONResponse(friendList.toString()).WriteResponse(output);
        }
        catch(JSONException | IOException e) {
            System.err.println(e);
            new BadResponse().WriteResponse(output);
            return;
        }
    }

    private static void add(PostRequest request, DataOutputStream output) throws IOException {
        ByteArrayOutputStream input = new ByteArrayOutputStream();
        request.readData(input);
        System.err.println(input.toString());
        JSONObject obj;
        try {
            obj = new JSONObject(input.toString());
            String sender = obj.getString("sender");
            String receiver = obj.getString("receiver");
            ClientLib.addFriend(sender, receiver);
            JSONArray friendList = new JSONArray(ClientLib.listFriends(sender));
            new JSONResponse(friendList.toString()).WriteResponse(output);
        }
        catch(JSONException | IOException e) {
            System.err.println(e);
            new BadResponse().WriteResponse(output);
            return;
        }
    }

    private static void delete(PostRequest request, DataOutputStream output) throws IOException {
        ByteArrayOutputStream input = new ByteArrayOutputStream();
        request.readData(input);
        System.err.println(input.toString());
        JSONObject obj;
        try {
            obj = new JSONObject(input.toString());
            String sender = obj.getString("sender");
            String receiver = obj.getString("receiver");
            ClientLib.delFriend(sender, receiver);
            JSONArray friendList = new JSONArray(ClientLib.listFriends(sender));
            new JSONResponse(friendList.toString()).WriteResponse(output);
        }
        catch(JSONException | IOException e) {
            System.err.println(e);
            new BadResponse().WriteResponse(output);
            return;
        }
    }

    private static void refresh(PostRequest request, DataOutputStream output) throws IOException {
        ByteArrayOutputStream input = new ByteArrayOutputStream();
        request.readData(input);
        try {
            JSONObject obj = new JSONObject(input.toString());
            String sender = obj.getString("sender");
            String receiver = obj.getString("receiver");
            ChatLog [] logs = ClientLib.getnew(sender, receiver);
            new JSONResponse(new JSONArray(logs).toString()).WriteResponse(output);
        } catch(JSONException | IOException e) {
            System.err.println(e);
            new BadResponse().WriteResponse(output);
        }
    }
    
    private static void sendText(PostRequest request, DataOutputStream output) throws IOException {
        ByteArrayOutputStream input = new ByteArrayOutputStream();
        request.readData(input);
        JSONObject obj;
        try {
            obj = new JSONObject(input.toString());
            String sender = obj.getString("sender");
            String receiver = obj.getString("receiver");
            String content = obj.getString("content");
            ClientLib.sendText(sender, receiver, content);
        }
        catch(JSONException | IOException e) {
            System.err.println(e);
            new BadResponse().WriteResponse(output);
            return;
        }

        new GeneralGoodResponse().WriteResponse(output);
    }

    private static void sendFile(PostRequest request, DataOutputStream output) throws IOException {
        String [] locations = request.location.split("/");
        String sender = locations[2];
        String receiver = locations[3];
        String filename = locations[4];
        try {
            // Receive data from browser
            request.readData(ClientLib.streamFromClient(sender, receiver, filename));

            // Send data to server
        }
        catch(IOException e) {
            System.err.println(e);
            new BadResponse().WriteResponse(output);
            return;
        }

        new GeneralGoodResponse().WriteResponse(output);
    }

    private static void sendImage(PostRequest request, DataOutputStream output) throws IOException {
    }
}
