package util;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private static final long TIME_WINDOW_MS = 10_000; // 10 sec window
    private static final int MAX_REQUESTS = 10;         // max reqs per window

    private static final Map<String, UserAccessInfo> accessMap = new ConcurrentHashMap<>();

    public static boolean isAllowed(String ip) {
        UserAccessInfo info = accessMap.computeIfAbsent(ip, k -> new UserAccessInfo());
        long now = Instant.now().toEpochMilli();

        synchronized (info) {
            if (now - info.windowStart > TIME_WINDOW_MS) {
                info.windowStart = now;
                info.requestCount = 1;
                return true;
            } else {
                if (info.requestCount < MAX_REQUESTS) {
                    info.requestCount++;
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    private static class UserAccessInfo {
        long windowStart = Instant.now().toEpochMilli();
        int requestCount = 0;
    }
}
