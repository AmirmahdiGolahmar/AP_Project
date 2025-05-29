package util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBlacklist {
    public static final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public static void add(String token) {
        blacklist.add(token);
    }

    public static boolean contains(String token) {
        return blacklist.contains(token);
    }
}
