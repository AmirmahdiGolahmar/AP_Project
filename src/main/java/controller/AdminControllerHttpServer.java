package controller;

import com.sun.net.httpserver.Filter;
import util.Filter.LoggingFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import entity.*;
import dto.*;
import service.AdminService;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.AuthorizationHandler.authorize;
import static util.AuthorizationHandler.authorizeUser;
import static util.HttpUtil.*;
import static exception.ExceptionHandler.expHandler;

public class AdminControllerHttpServer {
    private static final AdminService adminService = new AdminService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();

    public static void init(HttpServer server, List<Filter> filters) {
        server.createContext("/admin/users", new UsersHandler()).getFilters().addAll(filters);
        server.createContext("/admin/orders", new OrdersHandler()).getFilters().addAll(filters);
        server.createContext("/admin/transactions", new TransactionsHandler()).getFilters().addAll(filters);
        server.createContext("/admin/coupons", new CouponsHandler()).getFilters().addAll(filters);
    }

    static class UsersHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();

                authorize(exchange, UserRole.ADMIN);

                Matcher matcher = Pattern.compile("/admin/users/([0-9]+)/status").matcher(path);
                if (matcher.matches() && ("PATCH".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method))) {
                    handleChangeUserStatus(exchange, matcher);
                    return;
                }

                if ("GET".equalsIgnoreCase(method) && "/admin/users".equals(path)) {
                    handleGetUsers(exchange);
                    return;
                }

                matcher = Pattern.compile("/admin/users/([0-9]+)/status").matcher(path);
                if ("GET".equalsIgnoreCase(method) && matcher.matches()) {
                    handelGetUserStatus(exchange, matcher);
                    return;
                }

                matcher = Pattern.compile("/admin/users/([0-9]+)").matcher(path);
                if ("DELETE".equalsIgnoreCase(method) && matcher.matches()) {
                    handleDeleteUsers(exchange, matcher);
                    return;
                }

                sendResponse(exchange, 404, gson.toJson(Map.of("message", "Invalid path")));

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handelGetUserStatus(HttpExchange exchange, Matcher matcher) throws IOException {
            Long userId = Long.parseLong(matcher.group(1));
            StatusDto response = adminService.getUserStatus(userId);
            sendResponse(exchange, 200, gson.toJson(response));
        }

        private void handleDeleteUsers(HttpExchange exchange, Matcher matcher) throws IOException {
            Long userId = Long.parseLong(matcher.group(1));
            adminService.removeUser(userId);
            sendResponse(exchange, 200, gson.toJson(Map.of("message","user removed")));
        }

        private void handleChangeUserStatus(HttpExchange exchange ,Matcher matcher) throws IOException {
            long userId = Long.parseLong(matcher.group(1));
            User user = authorizeUser(userId, null);
            StatusDto request = readRequestBody(exchange, StatusDto.class,gson);
            adminService.changeUserStatus(user, request);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Status updated")));
        }

        private void handleGetUsers(HttpExchange exchange ) throws IOException {
            List<UserDto> users = adminService.getAllUsers();
            sendResponse(exchange, 200, gson.toJson(Map.of("List of users", users)));
        }
    }

    static class OrdersHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {

                authorize(exchange, UserRole.ADMIN);

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    handleSearchOrders(exchange);
                    return;
                }

                sendResponse(exchange, 404, gson.toJson(Map.of("message", "Invalid path")));

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleSearchOrders(HttpExchange exchange ) throws IOException {
            String search = getQueryParam(exchange, "search");
            String vendor = getQueryParam(exchange, "vendor");
            String courier = getQueryParam(exchange, "courier");
            String customer = getQueryParam(exchange, "customer");
            String status = getQueryParam(exchange, "status");

            List<OrderDto> orders = adminService.searchOrders(search, vendor, courier, customer, status);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of orders", orders)));
        }
    }

    static class TransactionsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {

                authorize(exchange, UserRole.ADMIN);

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    handleSearchTransactions(exchange);
                    return;
                }

                sendResponse(exchange, 404, gson.toJson(Map.of("message", "Invalid path")));

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleSearchTransactions(HttpExchange exchange ) throws IOException {
            String search = getQueryParam(exchange, "search");
            String user = getQueryParam(exchange, "user");
            String method = getQueryParam(exchange, "method");
            String status = getQueryParam(exchange, "status");

            List<TransactionDto> txs = adminService.searchTransaction(search, user, method, status);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of financial transactions", txs)));
        }
    }

    static class CouponsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();

                authorize(exchange, UserRole.ADMIN);

                Matcher matcher = Pattern.compile("/admin/coupons/([0-9]+)").matcher(path);
                if (matcher.matches()) {
                    long id = Long.parseLong(matcher.group(1));
                    if ("GET".equalsIgnoreCase(method)) {
                        handleGetCoupon(exchange, id);
                        return;
                    } else if ("PUT".equalsIgnoreCase(method)) {
                        handleEditCoupon(exchange, id);
                        return;
                    } else if ("DELETE".equalsIgnoreCase(method)) {
                        handleDeleteCoupon(exchange, id);
                        return;
                    }
                }

                if ("GET".equalsIgnoreCase(method) && "/admin/coupons".equals(path)) {
                    handleGetAllCoupons(exchange);
                    return;
                }

                if ("POST".equalsIgnoreCase(method) && "/admin/coupons".equals(path)) {
                    handleAddCoupon(exchange);
                    return;
                }

                sendResponse(exchange, 404, gson.toJson(Map.of("message", "Invalid path")));
            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleGetAllCoupons(HttpExchange exchange) throws IOException {
            List<CouponDto> list = adminService.getAllCoupons();
            sendResponse(exchange, 200, gson.toJson(Map.of("List of all coupons", list)));
        }

        private void handleGetCoupon(HttpExchange exchange, long id) throws IOException {
            CouponDto coupon = adminService.getCoupon(id);
            sendResponse(exchange, 200, gson.toJson(Map.of("coupon details", coupon)));
        }

        private void handleEditCoupon (HttpExchange exchange, long id) throws IOException {
            CouponRequest request = readRequestBody(exchange, CouponRequest.class, gson);
            CouponDto updated = adminService.updateCoupon(request, id);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Coupon updated successfully", "coupon", updated)));
        }

        private void handleDeleteCoupon (HttpExchange exchange, long id) throws IOException {
            adminService.deleteCoupon(id);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Coupon deleted")));
        }

        private void handleAddCoupon (HttpExchange exchange) throws IOException {
            CouponRequest request = readRequestBody(exchange, CouponRequest.class, gson);
            CouponDto added = adminService.addCoupon(request);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "coupon added successfully", "coupon", added)));
        }
    }
}
