package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
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
        System.err.println("trying to connect to " + addr + ":" + port);
        socket = new Socket(addr, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        login(username);
    }

    public static void onlyLogin(String username) throws IOException {
        ClientLib lib = new ClientLib(username);
        // Create needed directories
        if (new File(libDir, username).mkdir())
            System.err.println("New user " + username);
        else
            System.err.println(username + " login");
        lib.afterOperation();
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
        lib.output.writeUTF(friend);
        FriendStatus result = FriendStatus.translate(lib.input.readInt());
        if (result == FriendStatus.NOTFRIEND) {
            createDirectories(user, friend);
        }
        lib.afterOperation();
        return result;
    }

    public static FriendStatus delFriend(String user, String friend) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.output.writeInt(LobbyActions.DEL.code);
        lib.output.writeUTF(friend);
        FriendStatus result = FriendStatus.translate(lib.input.readInt());
        if (result == FriendStatus.ISFRIEND) {
            removeDirectories(user, friend);
        }
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
        lib.sendData(Paths.get(libDir.toString(), user, friend, filename).toString());
        lib.afterOperation();
    }

    public static void sendImage(String user, String friend, String filename) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.enterChat(friend);
        lib.output.writeInt(ChatActions.IMAGE.code);
        lib.sendData(Paths.get(libDir.toString(), user, friend, filename).toString());
        lib.afterOperation();
    }

    public static ChatLog[] getnew(String user, String friend) throws IOException {
        ClientLib lib = new ClientLib(user);
        lib.enterChat(friend);
        lib.output.writeInt(ChatActions.GETNEW.code);
        int len = lib.input.readInt();
        System.err.println("get len: " + len);
        ChatLog[] logs = new ChatLog[len];
        for(int i = 0; i < len; ++i) {
            LogType type = LogType.translate(lib.input.readInt());
            System.err.println("got type: " + type);
            String from = lib.input.readUTF();
            System.err.println("got from: " + from);
            String content = lib.input.readUTF();
            System.err.println("got content: " + content);
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

    public static FileOutputStream streamFromClient(String user, String friend, String filename) throws FileNotFoundException {
        File path = new File(libDir, user + "/" + friend + "/" + filename);
        return new FileOutputStream(path);
    }

    private void sendData(String filename) throws IOException {
        output.writeUTF(getSuffix(filename));
        File fileToSend = new File(Paths.get(filename).toString());
        RandomAccessFile file = new RandomAccessFile(fileToSend, "rw");
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
        fileToSend.delete();
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
        input.readInt();
    }

    private static String getSuffix(String filename) {
        LinkedList<String> chunks = new LinkedList<>(Arrays.asList(filename.split("\\.")));
        chunks.pop();
        String suffix = chunks.stream().collect(Collectors.joining("."));
        if(suffix.length() > 0) return '.' + suffix;
        return suffix;
    }

    private static void createDirectories(String user, String friend) {
        // Should be called with existing friend
        File userDir = new File(libDir, user);
        File friendDir = new File(libDir, friend);
        new File(userDir, friend).mkdir();
        new File(friendDir, user).mkdir();
    }


    private static void removeDirectories(String user, String friend) {
        File userDir = new File(libDir, user);
        File friendDir = new File(libDir, friend);
        File userFriendDir = new File(userDir, friend);
        File friendUserDir = new File(friendDir, user);
        if (userFriendDir.exists()) removeDirectory(userFriendDir);
        if (friendUserDir.exists()) removeDirectory(friendUserDir);
    }

    private static void removeDirectory(File dir) {
        for (String content : dir.list()) new File(dir, content).delete();
        dir.delete();
    }

    private static File generateFile() {
        try {
            return File.createTempFile("download", null, libDir);
        }
        catch(IOException e) { return null; }
    }
}
