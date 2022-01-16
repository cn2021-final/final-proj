package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Collectors;

import common.actions.ChatActions;
import common.chat.ChatLog;
import common.chat.LogType;

public class Chat {
    private final DataInputStream input;
    private final DataOutputStream output;
    private final Scanner userInput;
    private long lastMessageOffset = 0;
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
                else if(command[0].equals("/d") && command.length == 2) binary(command[1]);
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

    private void image(String filename) throws IOException {
        if(!fileReady(filename)) return;
        output.writeInt(ChatActions.IMAGE.code);
        sendData(filename);
        getnew();
    }

    private void binary(String filename) throws IOException {
        if(!fileReady(filename)) return;
        output.writeInt(ChatActions.BINARY.code);
        sendData(filename);
        getnew();
    }

    private void sendData(String filename) throws IOException {
        output.writeUTF(getSuffix(filename));
        RandomAccessFile file = new RandomAccessFile(new File(Paths.get(filename).toString()), "rw");
        long size = file.length(), progress = 0;
        output.writeLong(size);
        byte[] buf = new byte[4096];
        while(progress < size) {
            if(size - progress < 4096) buf = new byte[(int)(size - progress)];
            file.read(buf);
            output.write(buf);
            progress += 4096;
        }
        file.close();
    }

    private void get(String filename) throws IOException {
        output.writeInt(ChatActions.GETDATA.code);
        output.writeUTF(filename);
        long size = input.readLong(), progress = 0;
        if(size < 0) {
            badFile(filename);
            return;
        }
        File f = new File(Paths.get(filename).toString());
        f.createNewFile();
        RandomAccessFile file = new RandomAccessFile(f, "rw");
        byte[] buf = new byte[4096];
        while(progress < size) {
            if(size - progress < 4096) buf = new byte[(int)(size - progress)];
            input.read(buf);
            file.write(buf);
            progress += 4096;
        }
        file.close();
    }

    private void history() throws IOException {
        output.writeInt(ChatActions.GETHIST.code);
        output.writeLong(lastMessageOffset);
        output.writeInt(10);
        lastMessageOffset = input.readLong();
        int len = input.readInt();
        receiveLogs(len);
    }

    private long getLastReadOffset() throws IOException {
        output.writeInt(ChatActions.GETOFFSET.code);
        return input.readLong();
    }

    private void text(String content) throws IOException {
        output.writeInt(ChatActions.TEXT.code);
        output.writeUTF(content);
        getnew();
    }

    private void getnew() throws IOException {
        output.writeInt(ChatActions.GETNEW.code);
        int len = input.readInt();
        receiveLogs(len);
        lastMessageOffset = getLastReadOffset();
    }

    private void receiveLogs(int len) throws IOException {
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
    
    private boolean fileReady(String filename) {
        if(new File(Paths.get(filename).toString()).canRead()) return true;
        badFile(filename);
        return false;
    }

    private void badFile(String filename) {
        System.out.format("File %s can't be read.\n", filename);
    }

    private void quit() throws IOException {
        output.writeInt(ChatActions.EXIT.code);
    }

    private static String getSuffix(String filename) {
        LinkedList<String> chunks = new LinkedList<>(Arrays.asList(filename.split("\\.")));
        chunks.pop();
        String suffix = chunks.stream().collect(Collectors.joining("."));
        if(suffix.length() > 0) return '.' + suffix;
        return suffix;
    }

}
