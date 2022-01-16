package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import common.FriendStatus;
import common.actions.LobbyActions;

public class Lobby {
    private DataInputStream input;
    private DataOutputStream output;
    private Scanner userInput;
    private static final String banner = "Welcome! Here are some commands:\n"
    + "list - list friends\n"
    + "chat [username] - chat with a friend\n"
    + "add [username] - add [username] as a friend\n"
    + "delete [username] - remove [username] from the friend list\n"
    + "quit - leave\n";

    public Lobby(DataInputStream input, DataOutputStream output, Scanner userInput) {
        this.input = input;
        this.output = output;
        this.userInput = userInput;
    }

    public void run() throws IOException {
        System.out.print(banner);
        try {
            while(true) {
                System.out.print("> ");
                String[] command = userInput.nextLine().split(" ");
                if(command[0].equals("list") && command.length == 1) list();
                else if(command[0].equals("chat") && command.length == 2) chat(command[1]);
                else if(command[0].equals("add") && command.length == 2) add(command[1]);
                else if(command[0].equals("delete") && command.length == 2) delete(command[1]);
                else if(command[0].equals("quit") && command.length == 1) return;
                else System.out.format("Error: unrecognised command %s\n", command[0]);
            }
        }
        catch(NoSuchElementException e) {
            // user input returns EOF, ignored
        }
    }

    private void list() throws IOException {
        output.writeInt(LobbyActions.LIST.code);
        System.out.print(input.readUTF());
    }

    private void chat(String friend) throws IOException {
        output.writeInt(LobbyActions.CHAT.code);
        output.writeUTF(friend);
        switch(FriendStatus.translate(input.readInt())) {
            case ISFRIEND:
            break;
            default:
            System.out.format("%s is not your friend or doesn't exist\n", friend);
            return;
        }
        new Chat(input, output, userInput).run();
    }

    private void add(String friend) throws IOException {
        output.writeInt(LobbyActions.ADD.code);
        output.writeUTF(friend);
        switch(FriendStatus.translate(input.readInt())) {
            case ISFRIEND:
            System.out.format("%s is already your friend.\n", friend);
            break;
            case NOTFRIEND:
            System.out.format("Added %s as a friend.\n", friend);
            break;
            case NOTEXIST:
            System.out.format("User %s doesn't exist.\n", friend);
            break;
            case YOURSELF:
            System.out.println("You can't add yourself as a friend");
        }
    }

    private void delete(String friend) throws IOException {
        output.writeInt(LobbyActions.DEL.code);
        output.writeUTF(friend);
        switch(FriendStatus.translate(input.readInt())) {
            case ISFRIEND:
            System.out.format("Removed %s from the friend list.\n", friend);
            break;
            case NOTFRIEND:
            System.out.format("%s is not your friend.\n", friend);
            break;
            case NOTEXIST:
            System.out.format("User %s doesn't exist.\n", friend);
            break;
            case YOURSELF:
            System.out.println("You can't delete yourself from the friend list");
        }
    }
}
