package common.actions;

import java.util.TreeMap;

public enum LobbyActions {
    LIST(1), CHAT(2), ADD(3), DEL(4), QUIT(5);

    public final int code;
    private static final TreeMap<Integer, LobbyActions> reverseLookup = initialiseMap();

    LobbyActions(int code) { this.code = code; }

    private static TreeMap<Integer, LobbyActions> initialiseMap() {
        TreeMap<Integer, LobbyActions> map = new TreeMap<>();
        for(var action : values()) map.put(action.code, action);
        return map;
    }

    public static LobbyActions translate(int code) { return reverseLookup.get(code); }
}
