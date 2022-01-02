package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.LinkedList;
import java.util.Queue;

import common.chat.ChatLog;
import common.chat.LogType;

public class Chatroom {
    private final String username;
    private final File userDir, friendDir;
    private final RandomAccessFile userLog, friendLog;
    private final FileChannel sessionLock;
    public static final String histfile = "history";

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
}
