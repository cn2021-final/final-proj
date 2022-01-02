package common.chat;

public class ChatLog {
    public final LogType type;
    public final String user, content;

    public ChatLog(LogType type, String user, String content) {
        this.type = type;
        this.user = user;
        this.content = content;
    }
}
