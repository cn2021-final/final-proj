package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import common.actions.ChatActions;
import common.chat.ChatLog;
import common.chat.LogType;

public class Chat {
    private final DataInputStream input;
    private final DataOutputStream output;
    private final Scanner userInput;
    private static final String banner = "Here are some commands:\n"
    + "/i [filename] - image\n"
    + "/d [filename] - binary data\n"
    + "/g [filename] - get image / binary data\n"
    + "/l - more history\n"
    + "/r - check for new messages\n"
    + "/q - quit\n"
    + "anything else - send the raw text\n";

    public Chat(DataInputStream input, DataOutputStream output, Scanner userInput) {
        this.input = input;
        this.output = output;
        this.userInput = userInput;
    }

    public void run() throws IOException {
        getnew();
        System.out.print(banner);
        while(true) {
            System.out.print(">> ");
            String content = userInput.nextLine();
            if(content.charAt(0) == '/') {
                String[] command = content.split(" ");
                if(command[0].equals("/i") && command.length == 2) image(command[1]);
                else if(command[0].equals("/d") && command.length == 2) data(command[1]);
                else if(command[0].equals("/g") && command.length == 2) get(command[1]);
                else if(command[0].equals("/l") && command.length == 1) history();
                else if(command[0].equals("/r") && command.length == 1) getnew();
                else if(command[0].equals("/q") && command.length == 1) {
                    quit();
                    return;
                }
                else System.out.format("Error: unrecognised command %s\n", content);
            }
            else text(content);
        }
    }

    private void image(String filename) {

    }

    private void data(String filename) {

    }

    private void get(String filename) {

    }

    private void history() {

    }

    private void text(String content) throws IOException {
        output.writeInt(ChatActions.TEXT.code);
        output.writeUTF(content);
        getnew();
    }

    private void getnew() throws IOException {
        output.writeInt(ChatActions.GETNEW.code);
        int len = input.readInt();
        for(int i = 0; i < len; ++i) {
            LogType type = LogType.translate(input.readInt());
            String user = input.readUTF();
            String content = input.readUTF();
            presentMessage(new ChatLog(type, user, content));
        }
    }

    private void presentMessage(ChatLog log) {
        System.out.format("%s:\n", log.user);
        switch(log.type) {
            case BINARY:
            System.out.print("[DATA] ");
            break;
            case IMAGE:
            System.out.print("[IMAGE] ");
            break;
            case TEXT: // do nothing
        }
        System.out.println(log.content);
    }

    private void quit() throws IOException {
        output.writeInt(ChatActions.EXIT.code);
    }
}
