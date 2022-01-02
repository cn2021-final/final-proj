package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import common.actions.ChatActions;

public class Chat {
    private DataInputStream input;
    private DataOutputStream output;
    public static final String histfile = "history";

    public void run() throws IOException {
        // TODO
        while(true) {
            ChatActions action = ChatActions.translate(input.readInt());
            switch(action) {
                case TEXT:
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
                break;
                case EXIT:
                return;
            }
        }
    }
}
