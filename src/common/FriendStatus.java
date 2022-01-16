package common;

import java.util.TreeMap;

public enum FriendStatus {
    NOTFRIEND(1), ISFRIEND(2), NOTEXIST(3), YOURSELF(4);

    public final int code;
    private static final TreeMap<Integer, FriendStatus> reverseLookup = initialiseMap();

    FriendStatus(int code) { this.code = code; }

    private static TreeMap<Integer, FriendStatus> initialiseMap() {
        TreeMap<Integer, FriendStatus> map = new TreeMap<>();
        for(var action : values()) map.put(action.code, action);
        return map;
    }

    public static FriendStatus translate(int code) { return reverseLookup.get(code); }
}
