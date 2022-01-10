package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import common.FriendStatus;

public class UserManager {
    private File rootDir = new File("serverDir");

    public UserManager() {
        rootDir.mkdirs();
    }

    public void login(String username) {
        if(new File(rootDir, username).mkdir()) System.out.format("new user %s\n", username);
        else System.out.format("login from %s\n", username);
    }

    public String getFriedns(String username) {
        String r = "";
        for(File f : new File(rootDir, username).listFiles()) r += f.getName() + '\n';
        return r;
    }

    public FriendStatus addFriend(String username, String friend) {
        File friendDir = new File(rootDir, friend);
        if(!friendDir.exists()) return FriendStatus.NOTEXIST;
        File userDir = new File(rootDir, username);
        File userFriend = new File(userDir, friend);
        if(userFriend.exists()) return FriendStatus.ISFRIEND;
        friendInit(userFriend);
        friendInit(new File(friendDir, username));
        return FriendStatus.NOTFRIEND;
    }

    private void friendInit(File userFriend) {
        try {
            userFriend.mkdir();
            File log = new File(userFriend, Chatroom.histfile);
            log.createNewFile();
            RandomAccessFile content = new RandomAccessFile(log, "rw");
            content.writeLong(0); // useless padding, last seen
            content.writeLong(0); // offset of the last message
            content.seek(0);
            content.writeLong(content.length()); // last seen is set to the end of file
            content.close();
        }
        catch(IOException e) {
            System.out.print("Can't create file: ");
            System.out.println(e);
        }
    }

    public FriendStatus delFriend(String username, String friend) {
        File friendDir = new File(rootDir, friend);
        if(!friendDir.exists()) return FriendStatus.NOTEXIST;
        File userDir = new File(rootDir, username);
        File userFriend = new File(userDir, friend);
        if(!userFriend.exists()) return FriendStatus.NOTFRIEND;
        deleteDir(userFriend);
        deleteDir(new File(friendDir, username));
        return FriendStatus.ISFRIEND;
    }

    private void deleteDir(File dir) {
        for(String content : dir.list()) new File(dir, content).delete();
        dir.delete();
    }

    public Chatroom makeChatroom(String username, String friend) {
        File friendDir = new File(rootDir, friend);
        if(!friendDir.exists()) return null;
        File userFriend = new File(new File(rootDir, username), friend);
        if(!userFriend.exists()) return null;
        File friendFriend = new File(friendDir, username);
        try {
            return new Chatroom(username, userFriend, friendFriend);
        }
        catch(FileNotFoundException e) {
            return null;
        }
    }
}
