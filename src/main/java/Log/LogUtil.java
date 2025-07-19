package Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtil {

    private static final String LOG_FILE_PATH = "http-requests.log";

    public static void logRequest(String method, String uri, String body, String contentType) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true), true)) {
            if(contentType.equals("Request")) {
                writer.println("----  HTTP Request  ----");
                writer.println("Time   : " + time);
                writer.println("Method : " + method);
                writer.println("URI    : " + uri);
                writer.println("Body   : " + body);
                writer.println("-----------------------");
            }else{
                writer.println("----  HTTP Response  ----");
                writer.println("Time   : " + time);
                writer.println("Body   : " + body);
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
}
