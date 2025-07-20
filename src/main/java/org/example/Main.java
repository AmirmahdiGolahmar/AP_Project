package org.example;

import com.sun.net.httpserver.Filter;
import util.Filter.LoggingFilter;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

import controller.*;
import util.Filter.RateLimitFilter;
import util.Filter.TimeoutFilter;
import util.RateLimiter;

import static util.Log.LogUtil.startLogging;

public class Main {
    public static void main(String[] args) throws IOException {

        List<Filter> filters = List.of(new LoggingFilter(), new RateLimitFilter(), new TimeoutFilter());

        // Reset Java Logging
        LogManager.getLogManager().reset();

        // Create HTTP Server on port 4567
        HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);

        server.createContext("/auth", new UserControllerHttpServer())
                .getFilters().addAll(filters);

        server.createContext("/restaurants", new RestaurantControllerHttpServer())
                .getFilters().addAll(filters);

        CustomerControllerHttpServer.init(server, filters);

        DeliveryControllerHttpServer.init(server, filters);

        TransactionControllerHttpServer.init(server, filters);

        AdminControllerHttpServer.init(server, filters);

        server.createContext("/health", exchange -> {
                    String response = "Server is healthy!";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                })
                .getFilters().addAll(filters);

        server.createContext("/heavy-task", exchange -> {
            String response = doHeavyTask();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }).getFilters().addAll(filters);


        server.setExecutor(Executors.newFixedThreadPool(10));
        startLogging("4567");
        server.start();
        System.out.println("HTTP server running on port 4567...");
    }

    private static String doHeavyTask() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Task was interrupted.";
        }
        return "Heavy task done!";
    }
}



