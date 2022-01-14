package web;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import client.ClientLib;
import web.http.request.PostRequest;
import web.http.response.BadResponse;
import web.http.response.JSONResponse;
import web.http.response.GeneralGoodResponse;
import web.http.response.NotFoundResponse;

public class PostRequestHandler {
    public static void handle(PostRequest request, DataOutputStream output) throws IOException {
        System.err.println(request.location);
        switch(request.location) {
            case "/renew":
                renew(request, output);
            case "/add":
                add(request, output);
            case "/delete":
                delete(request, output);
            case "/send-text":
                sendText(request, output);
            return;
        }
        new NotFoundResponse().WriteResponse(output);
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
}
