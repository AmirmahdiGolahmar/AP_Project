package controller;

import com.sun.net.httpserver.Filter;
import exception.InvalidInputException;
import exception.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import service.MenuService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import entity.*;
import service.CustomerService;
import service.RestaurantService;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
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
    private static final MenuService menuService = new MenuService();
    private static final RestaurantService restaurantService = new RestaurantService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();
    static String errorContent;

    CustomerControllerHttpServer() {
        Map<String,String> map = new HashMap<>();
        map.put("message", "Invalid path");
        errorContent = gson.toJson(map);
    }



    public static void init(HttpServer server, List<Filter> filters, Executor executor) {
        server.createContext("/vendors", new VendorsHandler(executor)).getFilters().addAll(filters);
        server.createContext("/items", new ItemsHandler(executor)).getFilters().addAll(filters);
        server.createContext("/coupons", new CouponsHandler(executor)).getFilters().addAll(filters);;
        server.createContext("/orders", new OrdersHandler(executor)).getFilters().addAll(filters);;
        server.createContext("/favorites", new FavoritesHandler(executor)).getFilters().addAll(filters);;
        server.createContext("/ratings", new RatingsHandler(executor)).getFilters().addAll(filters);
    }

    static class VendorsHandler implements HttpHandler {
        private final Executor executor;

        VendorsHandler(Executor executor) {
            this.executor = executor;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            executor.execute(() -> {
                try {
                    String method = exchange.getRequestMethod();
                    URI uri = exchange.getRequestURI();
                    String path = uri.getPath();
                    Customer customer = authorize(exchange, UserRole.CUSTOMER);

                    if ("POST".equalsIgnoreCase(method) && "/vendors".equals(path)) {
                        handleSearchRestaurants(exchange);
                        return;
                    }

                    Matcher matcher = Pattern.compile("/vendors/([0-9]+)/items").matcher(uri.getPath());
                    if ("GET".equalsIgnoreCase(method) && matcher.matches()) {
                        handleGetRestaurantItems(exchange, matcher);
                        return;
                    }

                    matcher = Pattern.compile("/vendors/([0-9]+)/menus").matcher(uri.getPath());
                    if ("GET".equalsIgnoreCase(method) && matcher.matches()) {
                        handleGetMenus(exchange, matcher);
                        return;
                    }

                    matcher = Pattern.compile("/vendors/([0-9]+)").matcher(uri.getPath());
                    if ("GET".equalsIgnoreCase(method) && matcher.matches()) {
                        handleDisplayRestaurants(exchange, matcher);
                        return;
                    }

                    sendResponse(exchange, 404, gson.toJson(errorContent));
                } catch (Exception e) {
                    expHandler(e, exchange, gson);
                } finally {
                    exchange.close();
                }
            });
        }

        private void handleGetRestaurantItems(HttpExchange exchange, Matcher matcher) throws IOException {
            long restaurantId = Long.parseLong(matcher.group(1));
            Restaurant restaurant = validateRestaurant(restaurantId);
            List<ItemDto> response = customerService.getRestaurantItems(restaurant);
            sendResponse(exchange, 200, gson.toJson(Map.of("Vendor items", response)));
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

        private void handleGetMenus(HttpExchange exchange, Matcher matcher) throws IOException {
            long restaurantId = Long.parseLong(matcher.group(1));
            Restaurant restaurant = validateRestaurant(restaurantId);
            List<MenuDto> response = menuService.getRestaurantMenus(restaurant);
            sendResponse(exchange, 200, gson.toJson(Map.of("Restaurant menus", response)));
        }
    }

    static class ItemsHandler implements HttpHandler {
        private final Executor executor;

        ItemsHandler(Executor executor) {
            this.executor = executor;
        }
        public void handle(HttpExchange exchange) throws IOException {
            executor.execute(() -> {
                try {
                    String method = exchange.getRequestMethod();
                    URI uri = exchange.getRequestURI();
                    String path = uri.getPath();
                    Customer customer = authorize(exchange, UserRole.CUSTOMER);

                    if ("POST".equalsIgnoreCase(method) && "/items".equals(path)) {
                        handleSearchItem(exchange);
                        return;
                    }
                    Matcher matcher = Pattern.compile("/items/cart-items").matcher(uri.getPath());
                    if ("POST".equals(method) && matcher.matches()) {
                        handelModifyCartItems(exchange, customer);
                        return;
                    }
                    matcher = Pattern.compile("/items/(\\d+)/cart-item").matcher(uri.getPath());
                    if ("GET".equals(method) && matcher.matches()) {
                        long itemId = Long.parseLong(matcher.group(1));
                        handelGetCartItem(exchange, customer, itemId);
                        return;
                    }


                    sendResponse(exchange, 404, gson.toJson(errorContent));
                } catch (Exception e) {
                    expHandler(e, exchange, gson);
                }
            });
        }

        private void handelGetCartItem(HttpExchange exchange, Customer customer, long itemId) throws IOException {

            Item item = validateItem(itemId, null);

            CartItemDto response =  customerService.getCartItemQuantity(customer, item);

            sendResponse(exchange, 200, gson.toJson(Map.of("Cart item", response)));
        }

        private void handelModifyCartItems(HttpExchange exchange, Customer customer) throws IOException {
            CartItemDto cartItem = readRequestBody(exchange, CartItemDto.class, gson);
            customerService.modifyCartItemQuantity(cartItem, customer);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Pre order modified")));
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
        private final Executor executor;

        CouponsHandler(Executor executor) {
            this.executor = executor;
        }

        public void handle(HttpExchange exchange) throws IOException {
            executor.execute(() -> {
                try {
                    String method = exchange.getRequestMethod();
                    URI uri = exchange.getRequestURI();
                    String path = uri.getPath();
                    Customer customer = authorize(exchange, UserRole.CUSTOMER);

                    if ("GET".equalsIgnoreCase(method)&& "/coupons".equals(path)) {
                        handleCouponDetails(exchange);
                        return;
                    }

                    Matcher matcher = Pattern.compile("/coupons/apply").matcher(path);
                    if ("POST".equalsIgnoreCase(method) && matcher.find()) {
                        handleApplyCoupon(exchange, matcher);
                        return;
                    }

                    sendResponse(exchange, 404, gson.toJson(errorContent));
                } catch (Exception e) {
                    expHandler(e, exchange, gson);
                }
            });
        }

        private void handleApplyCoupon(HttpExchange exchange, Matcher matcher) throws IOException {
            CouponApplyDto request = readRequestBody(exchange, CouponApplyDto.class, gson);
            restaurantService.applyCouponToOrder(request.getOrder_id(), request.getCoupon_code());
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Coupon applied successfully")));
        }

        private void handleCouponDetails(HttpExchange exchange) throws IOException {
            String couponCode = getQueryParam(exchange, "coupon_code");
            CouponDto response = customerService.getCoupon(couponCode);
            sendResponse(exchange, 200, gson.toJson(Map.of("Coupon details", response)));
        }

    }

    static class OrdersHandler implements HttpHandler {
        private final Executor executor;

        OrdersHandler(Executor executor) {
            this.executor = executor;
        }
        public void handle(HttpExchange exchange) throws IOException {
            executor.execute(() -> {
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

                    matcher = Pattern.compile("/orders/([0-9]+)").matcher(path);
                    if ("PUT".equals(method) && matcher.matches()) {
                        handleUpdateOrderAddress(exchange, matcher);
                        return;
                    }



                    sendResponse(exchange, 404, gson.toJson(errorContent));

                } catch (Exception e) {
                    expHandler(e, exchange, gson);
                }
            });
        }

        private void handleUpdateOrderAddress(HttpExchange exchange, Matcher matcher) throws IOException {
            Long orderId = Long.parseLong(matcher.group(1));
            AddressUpdateDto request = readRequestBody(exchange, AddressUpdateDto.class, gson);
            if(request == null || request.getAddress().isEmpty()) throw new InvalidInputException("invalid address");
            customerService.updateOrderAddress(request.getAddress(), orderId);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "address updated successfully")));
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
        private final Executor executor;

        FavoritesHandler(Executor executor) {
            this.executor = executor;
        }
        public void handle(HttpExchange exchange) throws IOException {
            executor.execute(() -> {
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

                    sendResponse(exchange, 404, errorContent);
                } catch (Exception e) {
                    expHandler(e, exchange, gson);
                }
            });
        }

        private void handelAddToFavorite(HttpExchange exchange, User user, Restaurant restaurant) throws IOException {
            customerService.addToFavorites(user, restaurant);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Added to favorites")));
        }

        private void handleRemoveFromFavorite(HttpExchange exchange, User user, Restaurant restaurant) throws IOException {
            customerService.removeFromFavorites(user, restaurant);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Added to favorites")));
        }

        private void handleGetFavorites(HttpExchange exchange, User user) throws IOException {
            List<RestaurantDto> response = customerService.getFavorites(user);
            sendResponse(exchange, 200, gson.toJson(Map.of("List of favorite restaurants", response)));
        }
    }

    static class RatingsHandler implements HttpHandler {
        private final Executor executor;

        RatingsHandler(Executor executor) {
            this.executor = executor;
        }
        public void handle(HttpExchange exchange) throws IOException {
            executor.execute(() -> {
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

                    matcher = Pattern.compile("/ratings/order/([0-9]+)").matcher(path);
                    if (matcher.matches()) {
                        long orderId = Long.parseLong(matcher.group(1));
                        if ("GET".equals(method)) {
                            handleGetRatingByOrderId(exchange, orderId);
                            return;
                        }
                    }

                    sendResponse(exchange, 404, gson.toJson(errorContent));

                } catch (Exception e) {
                    expHandler(e, exchange, gson);
                }
            });
        }

        private void handleGetRatingByOrderId(HttpExchange exchange, Long orderId) throws IOException {
            OrderRatingDto response = customerService.getOrderRating(orderId);
            sendResponse(exchange, 200, gson.toJson(Map.of("Order rating", response)));
        }

        private void handleAddRating(HttpExchange exchange, User user) throws IOException {
            OrderRatingDto request = readRequestBody(exchange, OrderRatingDto.class, gson);
            validateRatingRegistrationRequest(request);
            customerService.submitOrderRating(request, user);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Rating submitted")));
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
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Rating deleted")));
        }

        private void handleEditRating(HttpExchange exchange, User user, long ratingId) throws IOException {
            OrderRatingDto request = readRequestBody(exchange, OrderRatingDto.class, gson);
            customerService.updateOrderRating(request, user, ratingId);
            sendResponse(exchange, 200, gson.toJson(Map.of("Rating updated", request)));
        }
    }
}
