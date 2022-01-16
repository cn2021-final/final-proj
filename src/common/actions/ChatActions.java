package common.actions;

import java.util.TreeMap;

public enum ChatActions {
    TEXT(1), IMAGE(2), BINARY(3), GETDATA(4), GETHIST(5), GETNEW(6), EXIT(7), GETOFFSET(8);

    public final int code;
    private static final TreeMap<Integer, ChatActions> reverseLookup = initialiseMap();

    ChatActions(int code) { this.code = code; }

    private static TreeMap<Integer, ChatActions> initialiseMap() {
        TreeMap<Integer, ChatActions> map = new TreeMap<>();
        for(ChatActions action : values()) map.put(action.code, action);
        return map;
    }

    public static ChatActions translate(int code) { return reverseLookup.get(code); }
}
