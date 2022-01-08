package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import common.chat.ChatLog;
import common.chat.LogType;

public class Chatroom {
    private final String username;
    private final File userDir, friendDir;
    private final RandomAccessFile userLog, friendLog;
    private final FileChannel sessionLock;
    public static final String histfile = "history";
    private static final Random random = new Random();
    private static final String charset = "abcdefghijklmnopqrstuvwxyz";

    public Chatroom(String username, File userDir, File friendDir) throws FileNotFoundException {
        this.username = username;
        this.userDir = userDir;
        this.friendDir = friendDir;
        userLog = new RandomAccessFile(new File(userDir, histfile), "rw");
        friendLog = new RandomAccessFile(new File(friendDir, histfile), "rw");
        if(userDir.getName().compareTo(friendDir.getName()) <= 0) sessionLock = userLog.getChannel();
        else sessionLock = friendLog.getChannel();
    }

    public Queue<ChatLog> getUnread() {
        LinkedList<ChatLog> logs = new LinkedList<>();
        FileLock lock = lockSession();
        if(lock == null) return logs;
        seekUntilRead(userLog);

        while(true) {
            try {
               LogType type =  LogType.translate(userLog.readInt());
               String user = userLog.readUTF();
               String content = userLog.readUTF();
               logs.push(new ChatLog(type, user, content));
            }
            catch(IOException e) {
                break;
            }
        }
        finishReading();

        unlockSession(lock);
        return logs;
    }

    public void sendText(String content) {
        FileLock lock = lockSession();
        if(lock == null) return;

        ChatLog log = new ChatLog(LogType.TEXT, username, content);
        updateLog(userLog, log);
        updateLog(friendLog, log);

        unlockSession(lock);
    }

    public File createFile(String suffix) {
        FileLock lock = lockSession();
        if(lock == null) return null;

        File file = generateFile(suffix);

        unlockSession(lock);
        return file;
    }

    public void sendBinary(String filename) {
        FileLock lock = lockSession();
        if(lock == null) return;

        ChatLog log = new ChatLog(LogType.BINARY, username, filename);
        updateLog(userLog, log);
        updateLog(friendLog, log);

        unlockSession(lock);
    }

    public void sendImage(String filename) {
        FileLock lock = lockSession();
        if(lock == null) return;

        ChatLog log = new ChatLog(LogType.IMAGE, username, filename);
        updateLog(userLog, log);
        updateLog(friendLog, log);

        unlockSession(lock);
    }

    public File openFile(String filename) {
        return new File(userDir, filename);
    }

    private FileLock lockSession() {
        try {
            return sessionLock.lock();
        }
        catch(IOException e) {
            System.out.print("failed to get file lock: ");
            System.out.println(e);
        }
        return null;
    }

    private void unlockSession(FileLock lock) {
        try {
            lock.close();
        }
        catch(IOException e) {
            System.out.print("failed to release file lock: ");
            System.out.println(e);
        }
    }

    private void seekUntilRead(RandomAccessFile file) {
        try {
            file.seek(0);
            file.seek(file.readLong());
        }
        catch(IOException e) {}
    }

    private void seekUntilEnd(RandomAccessFile file) {
        try {
            file.seek(file.length());
        }
        catch(IOException e) {}
    }

    private void finishReading() {
        try {
            userLog.seek(0);
            userLog.writeLong(userLog.length());
        }
        catch(IOException e) {}
    }

    private void updateLog(RandomAccessFile file, ChatLog log) {
        seekUntilEnd(file);
        try {
            file.writeInt(log.type.code);
            file.writeUTF(log.user);
            file.writeUTF(log.content);
        }
        catch(IOException e) {}
    }

    private File generateFile(String suffix) {
        try {
            while(true) {
                String filename = "";
                for(int i = 0; i < 7; ++i) filename += charset.charAt(random.nextInt(charset.length()));
                filename += suffix;
                File file = new File(userDir, filename);
                if(!file.exists()) {
                    file.createNewFile();
                    Files.createLink(new File(friendDir, filename).toPath(), file.toPath());
                    return file;
                }
            }
        }
        catch(IOException e) { return null; }
    }
}
