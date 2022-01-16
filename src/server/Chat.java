package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Queue;

import common.actions.ChatActions;
import common.chat.ChatHistory;
import common.chat.ChatLog;

public class Chat {
    private DataInputStream input;
    private DataOutputStream output;
    private Chatroom chatroom;

    public Chat(DataInputStream input, DataOutputStream output, Chatroom chatroom) {
        this.input = input;
        this.output = output;
        this.chatroom = chatroom;
    }

    public void run() throws IOException {
        while(true) {
            ChatActions action = ChatActions.translate(input.readInt());
            switch(action) {
                case TEXT:
                text();
                break;
                case IMAGE:
                image();
                break;
                case BINARY:
                binary();
                break;
                case GETDATA:
                getData();
                break;
                case GETHIST:
                getHistory();
                break;
                case GETNEW:
                getnew();
                break;
                case EXIT:
                return;
                case GETOFFSET:
                getLastMessageOffset();
                break;
            }
        }
    }

    private void text() throws IOException {
        String content = input.readUTF();
        chatroom.sendText(content);
    }

    private void getnew() throws IOException {
        Queue<ChatLog> logs = chatroom.getUnread();
        output.writeInt(logs.size());
        for(ChatLog log : logs) {
            output.writeInt(log.type.code);
            output.writeUTF(log.user);
            output.writeUTF(log.content);
        }
    }

    private void binary() throws IOException {
        String filename = receiveBinary();
        chatroom.sendBinary(filename);
    }

    private void image() throws IOException {
        String filename = receiveBinary();
        chatroom.sendImage(filename);
    }

    private void getData() throws IOException {
        File f = chatroom.openFile(input.readUTF());
        if(!f.canRead()) {
            output.writeLong(-1);
            return;
        }
        RandomAccessFile file = new RandomAccessFile(f, "rw");
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

    private String receiveBinary() throws IOException {
        String suffix = input.readUTF();
        File f = chatroom.createFile(suffix);
        RandomAccessFile file = new RandomAccessFile(f, "rw");
        long size = input.readLong();
        long progress = 0;
        byte[] buf = new byte[4096];
        while(progress < size) {
            if(size - progress < 4096) buf = new byte[(int)(size - progress)];
            input.read(buf);
            file.write(buf);
            progress += 4096;
        }
        file.close();
        return f.getName();
    }

    private void getHistory() throws IOException {
        // input: offset, count
        // output: offset, count, stuff
        long offset = input.readLong();
        int count = input.readInt();
        ChatHistory history = chatroom.getHistory(offset, count);
        output.writeLong(history.offset);
        int sz = history.size();
        output.writeInt(sz);
        for(int i = 0; i < sz; ++i) {
            ChatLog log = history.get();
            output.writeInt(log.type.code);
            output.writeUTF(log.user);
            output.writeUTF(log.content);
        }
    }

    private void getLastMessageOffset() throws IOException {
        // input: nothing
        // output: offset of last read
        output.writeLong(chatroom.getLastMessageOffset());
    }
}
