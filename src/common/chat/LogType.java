package common.chat;

import java.util.TreeMap;

public enum LogType {
    TEXT(1), IMAGE(2), BINARY(3);

    public final int code;
    private static final TreeMap<Integer, LogType> reverseLookup = initialiseMap();
    
    LogType(int code) {this.code = code; }

    private static TreeMap<Integer, LogType> initialiseMap() {
        TreeMap<Integer, LogType> map = new TreeMap<>();
        for(var action : values()) map.put(action.code, action);
        return map;
    }

    public static LogType translate(int code) { return reverseLookup.get(code); }
}
