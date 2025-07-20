package controller;

import util.Filter.LoggingFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.OrderDto;
import dto.StatusDto;
import entity.Delivery;
import entity.UserRole;
import service.DeliveryService;
import util.LocalDateTimeAdapter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.AuthorizationHandler.authorize;
import static util.HttpUtil.*;
import static exception.ExceptionHandler.expHandler;
import com.sun.net.httpserver.Filter;


public class DeliveryControllerHttpServer {

    private static final DeliveryService deliveryService = new DeliveryService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();

    public static void init(HttpServer server, List<Filter> filters) {
        server.createContext("/deliveries/available", new AvailableDeliveriesHandler()).getFilters().addAll(filters);
        server.createContext("/deliveries/history", new DeliveryHistoryHandler()).getFilters().addAll(filters);
        server.createContext("/deliveries", new DeliveryStatusHandler()).getFilters().addAll(filters);
    }

    static class AvailableDeliveriesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                authorize(exchange, UserRole.DELIVERY);

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    handelGetAvailableOrders(exchange);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handelGetAvailableOrders(HttpExchange exchange) throws IOException {
            List<OrderDto> response = deliveryService.getAvailableOrders();
            sendResponse(exchange, 200, gson.toJson(Map.of("List of available deliveries", response)));
        }
    }

    static class DeliveryStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                URI uri = exchange.getRequestURI();
                String method = exchange.getRequestMethod();
                String path = uri.getPath();

                String[] parts = path.split("/");
                if (parts.length == 3 && "PATCH".equalsIgnoreCase(method)) {
                    handelChangeOrderStatus(exchange, parts);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handelChangeOrderStatus(HttpExchange exchange, String[] parts) throws IOException {
            Delivery delivery = authorize(exchange, UserRole.DELIVERY);
            Long orderId = Long.parseLong(parts[2]);

            StatusDto bodyMap = readRequestBody(exchange, StatusDto.class, gson);
            String status = bodyMap.getStatus();

            OrderDto response = deliveryService.changeOrderStatus(delivery, orderId, status);
            sendResponse(exchange, 200, gson.toJson(Map.of("Changed status successfully", response)));
        }
    }

    static class DeliveryHistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    searchOrderHistory(exchange);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void searchOrderHistory(HttpExchange exchange) throws IOException {
            Delivery delivery = authorize(exchange, UserRole.DELIVERY);

            Map<String, String> params = new HashMap<>();
            String search = getQueryParam(exchange, "search");
            String vendor = getQueryParam(exchange, "vendor");
            String user = getQueryParam(exchange, "user");

            if (search != null) params.put("search", search);
            if (vendor != null) params.put("courier", vendor);
            if (user != null) params.put("user", user);

            List<OrderDto> response = deliveryService.searchDeliveryHistory(search, vendor, user, delivery);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of completed and active deliveries", response)));
        }
    }

}