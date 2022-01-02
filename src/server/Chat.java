package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Queue;

import common.actions.ChatActions;
import common.chat.ChatLog;

public class Chat {
    private DataInputStream input;
    private DataOutputStream output;
    private Chatroom chatroom;

    public Chat(DataInputStream input, DataOutputStream output, Chatroom chatroom) {
        this.input = input;
        this.output = output;
        this.chatroom = chatroom;
    }

    public void run() throws IOException {
        while(true) {
            ChatActions action = ChatActions.translate(input.readInt());
            switch(action) {
                case TEXT:
                text();
                break;
                case IMAGE:
                break;
                case BINARY:
                break;
                case GETDATA:
                break;
                case GETHIST:
                break;
                case GETNEW:
                getnew();
                break;
                case EXIT:
                return;
            }
        }
    }

    private void text() throws IOException {
        String content = input.readUTF();
        chatroom.sendText(content);
    }

    private void getnew() throws IOException {
        Queue<ChatLog> logs = chatroom.getUnread();
        output.writeInt(logs.size());
        for(ChatLog log : logs) {
            output.writeInt(log.type.code);
            output.writeUTF(log.user);
            output.writeUTF(log.content);
        }
    }
}
