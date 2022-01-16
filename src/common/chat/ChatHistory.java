package common.chat;

import java.util.Stack;

public class ChatHistory {
    public long offset = 0;
    private final Stack<ChatLog> logs;

    public ChatHistory() {
        logs = new Stack<>();
    }
    
    public void add(ChatLog log) {
        logs.push(log);
    }

    public ChatLog get() {
        return logs.pop();
    }

    public int size() {
        return logs.size();
    }
}
