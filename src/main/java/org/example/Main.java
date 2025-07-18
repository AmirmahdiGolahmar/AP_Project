package org.example;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.LogManager;
import com.sun.net.httpserver.HttpServer;
import controller.*;

public class Main {
    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().reset();
        HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);
        server.createContext("/auth", new UserControllerHttpServer());
        server.setExecutor(null); // default executor
        System.out.println("HTTP server running on port 4567...");
        server.start();
    }
}



