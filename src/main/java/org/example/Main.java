package org.example;

import com.sun.net.httpserver.Filter;
import util.Filter.LoggingFilter;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

import controller.*;
import util.Filter.RateLimitFilter;
import util.Filter.TimeoutFilter;

import static util.Log.LogUtil.startLogging;

public class Main {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {



        List<Filter> filters = List.of(new LoggingFilter(), new RateLimitFilter(), new TimeoutFilter());

        LogManager.getLogManager().reset();

        HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);

        UserControllerHttpServer.init(server, filters, executor);

        DataProvider.init(server, filters, executor);

        server.createContext("/restaurants", new RestaurantControllerHttpServer()).getFilters().addAll(filters);

        CustomerControllerHttpServer.init(server, filters, executor);

        DeliveryControllerHttpServer.init(server, filters);

        TransactionControllerHttpServer.init(server, filters);

        AdminControllerHttpServer.init(server, filters);

        server.createContext("/health", exchange -> {
                    String response = "Server is healthy!";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }).getFilters().addAll(filters);

        server.createContext("/heavy-task", exchange -> {
            executor.submit(() -> {
                String response = doHeavyTask();
                try {
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    exchange.close();
                }

            });
        }).getFilters().addAll(filters);


        server.setExecutor(executor);
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



