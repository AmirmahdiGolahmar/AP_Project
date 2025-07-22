package util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class TokenBlacklist {
    private static final Path filePath = Path.of(System.getProperty("user.dir"), "TokenBlackList");

    static {
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize blacklist file", e);
        }
    }

    public static void add(String token) {
        try {
            Files.writeString(filePath, token + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write token to blacklist", e);
        }
    }

    public static boolean contains(String token) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            return lines.contains(token);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read blacklist file", e);
        }
    }
}

