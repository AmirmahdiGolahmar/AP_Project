package org.example;

import Log.LoggingFilter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Filter;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

import controller.CustomerControllerHttpServer;
import controller.UserControllerHttpServer;
import controller.RestaurantControllerHttpServer;

import static Log.LogUtil.startLogging;

public class Main {
    public static void main(String[] args) throws IOException {
        // Reset Java Logging
        LogManager.getLogManager().reset();

        // Create HTTP Server on port 4567
        HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);

        server.createContext("/auth", new UserControllerHttpServer())
                .getFilters().add(new LoggingFilter());

        server.createContext("/restaurants", new RestaurantControllerHttpServer())
                .getFilters().add(new LoggingFilter());

        CustomerControllerHttpServer.init(server);


        // Use a thread pool executor
        server.setExecutor(Executors.newFixedThreadPool(10));
        startLogging("4567");
        System.out.println("HTTP server running on port 4567...");
        server.start();
    }
}



