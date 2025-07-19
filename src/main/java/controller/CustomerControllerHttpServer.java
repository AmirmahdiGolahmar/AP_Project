package controller;

import Log.LoggingFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import entity.*;
import exception.InvalidInputException;
import io.jsonwebtoken.Claims;
import service.CustomerService;
import util.JwtUtil;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import static util.AuthorizationHandler.*;
import static util.HttpUtil.*;
import static util.validator.RestaurantValidator.*;
import static exception.ExceptionHandler.expHandler;

public class CustomerControllerHttpServer {

    private static final CustomerService customerService = new CustomerService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();

    public static void init(HttpServer server) {
        server.createContext("/vendors", new VendorsHandler()).getFilters().add(new LoggingFilter());;
        server.createContext("/items", new ItemsHandler()).getFilters().add(new LoggingFilter());;
        server.createContext("/coupons", new CouponsHandler()).getFilters().add(new LoggingFilter());;
        server.createContext("/orders", new OrdersHandler()).getFilters().add(new LoggingFilter());;
        server.createContext("/favorites", new FavoritesHandler()).getFilters().add(new LoggingFilter());;
        server.createContext("/ratings", new RatingsHandler()).getFilters().add(new LoggingFilter());;
    }

    static class VendorsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                Customer customer = authorize(exchange, UserRole.CUSTOMER);

                if ("POST".equalsIgnoreCase(method) && "/vendors".equals(path)) {
                    handleSearchRestaurants(exchange);
                    return;
                }

                Matcher matcher = Pattern.compile("/vendors/([0-9]+)").matcher(uri.getPath());
                if ("GET".equalsIgnoreCase(method) && matcher.matches()) {
                    handleDisplayRestaurants(exchange, matcher);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");
            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleSearchRestaurants(HttpExchange exchange) throws IOException {
            RestaurantSearchRequestDto request = readRequestBody(exchange, RestaurantSearchRequestDto.class, gson);
            List<RestaurantDto> response = customerService.searchRestaurant(request);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of vendors",response)));
        }

        private void handleDisplayRestaurants(HttpExchange exchange, Matcher matcher) throws IOException {
            long restaurantId = Long.parseLong(matcher.group(1));
            Restaurant restaurant = validateRestaurant(restaurantId);
            RestaurantDisplayResponse response = customerService.displayRestaurant(restaurant);
            sendResponse(exchange, 200, gson.toJson(Map.of("Vendor menu items", response)));
        }
    }

    static class ItemsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                Customer customer = authorize(exchange, UserRole.CUSTOMER);

                if ("POST".equalsIgnoreCase(method) && "/items".equals(path)) {
                    handleSearchItem(exchange);
                    return;
                }
                Matcher matcher = Pattern.compile("/items/([0-9]+)").matcher(uri.getPath());
                if ("GET".equals(method) && matcher.matches()) {
                    handelDisplayItem(exchange, matcher);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");
            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleSearchItem(HttpExchange exchange) throws IOException {
            ItemSearchRequestDto request = readRequestBody(exchange, ItemSearchRequestDto.class, gson);
            List<ItemDto> response = customerService.searchItem(request);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of items", response)));
        }

        private void handelDisplayItem(HttpExchange exchange, Matcher matcher) throws IOException {
            long itemId = Long.parseLong(matcher.group(1));
            Item item = validateItem(itemId, null);
            ItemDto response = customerService.displayItem(item);
            sendResponse(exchange, 200, gson.toJson(Map.of("Item details", response)));
        }
    }

    static class CouponsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                Customer customer = authorize(exchange, UserRole.CUSTOMER);

                if ("GET".equalsIgnoreCase(method)) {
                    handleCouponDetails(exchange);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");
            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleCouponDetails(HttpExchange exchange) throws IOException {
            String couponCode = getQueryParam(exchange, "coupon_code");
            CouponDto response = customerService.getCoupon(couponCode);
            sendResponse(exchange, 200, gson.toJson(Map.of("Coupon details", response)));
        }

    }

    static class OrdersHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                Customer customer = authorize(exchange, UserRole.CUSTOMER);

                if ("POST".equalsIgnoreCase(method) && "/orders".equals(path)) {
                    handleAddOrder(exchange,  customer);
                    return;
                }

                if ("GET".equals(method) && "/orders/history".equals(path)) {
                    handleSearchOrder(exchange, customer);
                    return;
                }

                Matcher matcher = Pattern.compile("/orders/([0-9]+)").matcher(path);
                if ("GET".equals(method) && matcher.matches()) {
                    handleGetOrder(exchange, matcher);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleAddOrder(HttpExchange exchange, Customer customer) throws IOException {
            OrderRegistrationRequest request = readRequestBody(exchange, OrderRegistrationRequest.class, gson);
            validateOrderRegistrationRequest(request);
            Restaurant restaurant = validateRestaurant(request.getVendor_id());
            Coupon coupon = null;
            if(request.getCoupon_id() != null) coupon = validateCouponId(request.getCoupon_id());
            OrderDto response = customerService.addOrder(request, customer, restaurant, coupon);
            sendResponse(exchange, 200, gson.toJson(Map.of("Order submitted", response)));
        }

        private void handleSearchOrder(HttpExchange exchange, Customer customer) throws IOException {
            String search = getQueryParam(exchange, "search");
            String vendor = getQueryParam(exchange, "vendor");
            List<OrderDto> response = customerService.searchOrderHistory(search, vendor, customer);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of past orders", response)));
        }

        private void handleGetOrder(HttpExchange exchange, Matcher matcher) throws IOException {
            long orderId = Long.parseLong(matcher.group(1));
            OrderDto response = customerService.getOrder(orderId);
            sendResponse(exchange, 200, gson.toJson(Map.of("Order details", response)));
        }
    }

    static class FavoritesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                User user = authorize(exchange, UserRole.CUSTOMER);

                Matcher matcher = Pattern.compile("/favorites/([0-9]+)").matcher(path);
                if (matcher.matches()) {
                    long restaurantId = Long.parseLong(matcher.group(1));
                    Restaurant restaurant = validateRestaurant(restaurantId);

                    if ("PUT".equals(method)) {
                        handelAddToFavorite(exchange ,user, restaurant);
                        return;
                    } else if ("DELETE".equals(method)) {
                        handleRemoveFromFavorite(exchange ,user, restaurant);
                        return;
                    }
                }

                if ("GET".equals(method) && "/favorites".equals(path)) {
                    handleGetFavorites(exchange, user);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");
            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handelAddToFavorite(HttpExchange exchange, User user, Restaurant restaurant) throws IOException {
            customerService.addToFavorites(user, restaurant);
            sendResponse(exchange, 200, gson.toJson("Added to favorites"));
        }

        private void handleRemoveFromFavorite(HttpExchange exchange, User user, Restaurant restaurant) throws IOException {
            customerService.removeFromFavorites(user, restaurant);
            sendResponse(exchange, 200, gson.toJson("Removed from favorites"));
        }

        private void handleGetFavorites(HttpExchange exchange, User user) throws IOException {
            List<RestaurantDto> response = customerService.getFavorites(user);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of favorite restaurants", response)));
        }
    }

    static class RatingsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                User user = authorize(exchange, UserRole.CUSTOMER);

                if ("POST".equals(method) && "/ratings".equals(path)) {
                    handleAddRating(exchange, user);
                    return;
                }

                Matcher matcher = Pattern.compile("/ratings/items/([0-9]+)").matcher(path);
                if ("GET".equals(method) && matcher.matches()) {
                    handleViewRatings(exchange, matcher);
                    return;
                }

                Matcher matcherRating = Pattern.compile("/ratings/([0-9]+)").matcher(path);
                if (matcherRating.matches()) {
                    long ratingId = Long.parseLong(matcherRating.group(1));
                    if ("GET".equals(method)) {
                        handleGetRating(exchange, ratingId);
                        return;
                    } else if ("DELETE".equals(method)) {
                        handleDeleteRating(exchange, ratingId);
                        return;
                    } else if ("PUT".equals(method)) {
                        handleEditRating(exchange, user, ratingId);
                        return;
                    }
                }

                sendResponse(exchange, 404, "Invalid path");

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleAddRating(HttpExchange exchange, User user) throws IOException {
            OrderRatingDto request = readRequestBody(exchange, OrderRatingDto.class, gson);
            validateRatingRegistrationRequest(request);
            customerService.submitOrderRating(request, user);
            sendResponse(exchange, 200, gson.toJson("Rating submitted"));
        }

        private void handleViewRatings(HttpExchange exchange, Matcher matcher) throws IOException {
            Long itemId = Long.parseLong(matcher.group(1));
            ItemRatingAvgResponseDto response = customerService.getItemAvgRating(itemId);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of ratings and reviews", response)));
        }

        private void handleGetRating(HttpExchange exchange, long ratingId) throws IOException {
            ItemRatingResponseDto response = customerService.getItemRating(ratingId);
            sendResponse(exchange, 200, gson.toJson(Map.of("Rating details", response)));
        }

        private void handleDeleteRating(HttpExchange exchange, long ratingId) throws IOException {
            customerService.deleteRating(ratingId);
            sendResponse(exchange, 200, gson.toJson("Rating deleted"));
        }

        private void handleEditRating(HttpExchange exchange, User user, long ratingId) throws IOException {
            ItemRatingRequestDto request = readRequestBody(exchange, ItemRatingRequestDto.class, gson);
            customerService.updateItemRating(request, user, ratingId);
            sendResponse(exchange, 200, gson.toJson(Map.of("Rating updated", request)));
        }
    }
}
