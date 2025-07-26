package util.Log;

import util.FileCleaner;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

import static util.JsonSanitizer.sanitizeJson;

public class LogUtil {

    private static final String LOG_FILE_PATH = "http-requests.log";

    static {
        startLogFileMonitoring();
    }

    public static void logRequest(String method, String uri, String body, String contentType) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String cleanedBody = sanitizeJson(body, "profileImageBase64");

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true), true)) {
            if ("Request".equals(contentType)) {
                writer.println("----  HTTP Request  ----");
                writer.println("Time   : " + time);
                writer.println("Method : " + method);
                writer.println("URI    : " + uri);
                writer.println("Body   : " + cleanedBody);
                writer.println("-----------------------");
            } else {
                writer.println("----  HTTP Response  ----");
                writer.println("Time   : " + time);
                writer.println("Body   : " + cleanedBody);
                writer.println("-----------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startLogging(String port) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true), true)) {
            writer.println("HTTP server running on port " +  port + " ...") ;
            writer.println("Started Logging At   : " + time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startLogFileMonitoring() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            FileCleaner.clearFileIfTooLarge(LOG_FILE_PATH);
            FileCleaner.clearFileIfTooManyLines(LOG_FILE_PATH);
        }, 0, 30, TimeUnit.SECONDS);
    }
}
