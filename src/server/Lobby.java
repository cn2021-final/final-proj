package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import common.FriendStatus;
import common.actions.LobbyActions;

public class Lobby {
    private DataInputStream input;
    private DataOutputStream output;
    private UserManager userManager;
    private String username;

    public Lobby(DataInputStream input, DataOutputStream output, String username, UserManager userManager) {
        this.input = input;
        this.output = output;
        this.username = username;
        this.userManager = userManager;
    }

    public void run() throws IOException {
        while(true) {
            LobbyActions action = LobbyActions.translate(input.readInt());
            switch(action) {
                case LIST:
                listFriends();
                break;
                case CHAT:
                chat();
                break;
                case ADD:
                addFriend();
                break;
                case DEL:
                delFriend();
                break;
                case QUIT:
                return;
            }
        }
    }

    private void listFriends() throws IOException {
        output.writeUTF(userManager.getFriedns(username));
    }

    private void chat() throws IOException {
        String friend = input.readUTF();
        userManager.makeChatroom(username, friend).run();
    }

    private void addFriend() throws IOException {
        String friend = input.readUTF();
        FriendStatus result = userManager.addFriend(username, friend);
        output.writeInt(result.code);
    }

    private void delFriend() throws IOException {
        String friend = input.readUTF();
        FriendStatus result = userManager.delFriend(username, friend);
        output.writeInt(result.code);
    }
}
