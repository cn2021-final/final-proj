package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;


import common.FriendStatus;
import common.actions.ChatActions;
import common.actions.LobbyActions;
import common.chat.ChatLog;
import common.chat.LogType;

public class ClientLib {
    private static String addr;
    private static int port;
    private static File libDir;

    public static void init(String addr, int port) {
        ClientLib.addr = addr;
        ClientLib.port = port;
        libDir = new File("libDir");
        libDir.mkdirs();
    }

    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    private ClientLib(String username) throws IOException {
        socket = new Socket(addr, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        login(username);
    }

    public static String[] listFriends(String username) throws IOException {
        ClientLib lib = new ClientLib(username);
        lib.output.writeInt(LobbyActions.LIST.code);
        String[] result = lib.input.readUTF().split(System.lineSeparator());
        lib.afterOperation();
        return result;
    }

    public static FriendStatus addFriend(String user, String friend) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.output.writeInt(LobbyActions.ADD.code);
        FriendStatus result = FriendStatus.translate(lib.input.readInt());
        lib.afterOperation();
        return result;
    }

    public static FriendStatus delFriend(String user, String friend) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.output.writeInt(LobbyActions.DEL.code);
        FriendStatus result = FriendStatus.translate(lib.input.readInt());
        lib.afterOperation();
        return result;
    }

    public static void sendText(String user, String friend, String content) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.enterChat(friend);
        lib.output.writeInt(ChatActions.TEXT.code);
        lib.output.writeUTF(content);
        lib.afterOperation();
    }

    public static void sendBinary(String user, String friend, String filename) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.enterChat(friend);
        lib.output.writeInt(ChatActions.BINARY.code);
        lib.sendData(filename);
        lib.afterOperation();
    }

    public static void sendImage(String user, String friend, String filename) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.enterChat(friend);
        lib.output.writeInt(ChatActions.IMAGE.code);
        lib.sendData(filename);
        lib.afterOperation();
    }

    public static ChatLog[] getnew(String user, String friend) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.enterChat(friend);
        lib.output.writeInt(ChatActions.GETNEW.code);
        int len = lib.input.readInt();
        ChatLog[] logs = new ChatLog[len];
        for(int i = 0; i < len; ++i) {
            LogType type = LogType.translate(lib.input.readInt());
            String from = lib.input.readUTF();
            String content = lib.input.readUTF();
            logs[i] = new ChatLog(type, from, content);
        }
        lib.afterOperation();
        return logs;
    }

    public static File getBinary(String user, String friend, String filename) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.enterChat(friend);
        lib.output.writeInt(ChatActions.GETDATA.code);
        lib.output.writeUTF(filename);
        long size = lib.input.readLong(), progress = 0;
        if(size < 0) return null;
        File f = generateFile();
        RandomAccessFile file = new RandomAccessFile(f, "rw");
        byte[] buf = new byte[4096];
        while(progress < size) {
            if(size - progress < 4096) buf = new byte[(int)(size - progress)];
            lib.input.read(buf);
            file.write(buf);
            progress += 4096;
        }
        file.close();
        lib.afterOperation();
        return f;
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

    private void afterOperation() throws IOException {
        socket.close();
    }

    private void login(String username) throws IOException {
        output.writeUTF(username);
    }

    private void enterChat(String friend) throws IOException {
        output.writeInt(LobbyActions.CHAT.code);
        output.writeUTF(friend);
    }

    private static String getSuffix(String filename) {
        LinkedList<String> chunks = new LinkedList<>(Arrays.asList(filename.split("\\.")));
        chunks.pop();
        String suffix = chunks.stream().collect(Collectors.joining("."));
        if(suffix.length() > 0) return '.' + suffix;
        return suffix;
    }

    private static File generateFile() {
        try {
            return File.createTempFile("download", null, libDir);
        }
        catch(IOException e) { return null; }
    }
}
