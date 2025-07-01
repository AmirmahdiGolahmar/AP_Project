package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dao.UserDao;
import dto.*;
import entity.*;
import exception.*;
import service.ItemService;
import service.RestaurantService;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static validator.RestaurantValidator.itemValidator;
import static validator.SellerValidator.validateSellerAndRestaurant;

public class RestaurantControllerHttpServer {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private static final RestaurantService restaurantService = new RestaurantService();
    private static final ItemService itemService = new ItemService();

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(4567), 0);
        server.createContext("/restaurants", new Handler());
        server.createContext("/restaurants/mine", new Handler());
        server.createContext("/restaurants/item", new Handler());
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class Handler implements HttpHandler {
        private final Pattern restaurantIdPattern = Pattern.compile("^/restaurants/([0-9]+)$");
        private final Pattern itemPattern = Pattern.compile("^/restaurants/([0-9]+)/item$", Pattern.CASE_INSENSITIVE);
        private final Pattern itemIdPattern = Pattern.compile("^/restaurants/([0-9]+)/item/([0-9]+)$", Pattern.CASE_INSENSITIVE);

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if (method.equals("POST") && path.equals("/restaurants")) {
                    handleCreateRestaurant(exchange);
                } else if (method.equals("GET") && path.equals("/restaurants/mine")) {
                    handleGetMine(exchange);
                } else if (method.equals("PUT")) {
                    Matcher matcher = restaurantIdPattern.matcher(path);
                    if (matcher.matches()) {
                        Long restaurantId = Long.parseLong(matcher.group(1));
                        handleUpdateRestaurant(exchange, restaurantId);
                        return;
                    }
                    matcher = itemIdPattern.matcher(path);
                    if (matcher.matches()) {
                        Long restaurantId = Long.parseLong(matcher.group(1));
                        Long itemId = Long.parseLong(matcher.group(2));
                        handleEditItem(exchange, restaurantId, itemId);
                        return;
                    }
                } else if (method.equals("POST")) {
                    Matcher matcher = itemPattern.matcher(path);
                    if (matcher.matches()) {
                        Long restaurantId = Long.parseLong(matcher.group(1));
                        handleAddItem(exchange, restaurantId);
                        return;
                    }
                } else if (method.equals("DELETE")) {
                    Matcher matcher = itemIdPattern.matcher(path);
                    if (matcher.matches()) {
                        Long restaurantId = Long.parseLong(matcher.group(1));
                        Long itemId = Long.parseLong(matcher.group(2));
                        handleDeleteItem(exchange, restaurantId, itemId);
                        return;
                    }
                }

                sendResponse(exchange, 404, gson.toJson(Map.of("error", "Not Found")));

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, gson.toJson(Map.of("error", "Internal server error")));
            }
        }

        private void handleCreateRestaurant(HttpExchange exchange) throws IOException {
            String userId = authorizeAndExtractUserId(exchange, gson);
            User seller = new UserDao().findById(Long.parseLong(userId));

            if (seller == null || seller.getRole() != UserRole.SELLER) {
                sendResponse(exchange, 403, gson.toJson(Map.of("error", "Only sellers can create restaurants")));
                return;
            }

            RestaurantRegistrationRequest request = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), RestaurantRegistrationRequest.class);
            try {
                Restaurant restaurant = restaurantService.createRestaurant(request, seller);
                RestaurantDto response = new RestaurantDto(
                        restaurant.getId(), restaurant.getName(), restaurant.getAddress(),
                        restaurant.getPhone(), restaurant.getLogo(), restaurant.getTaxFee()
                );
                sendResponse(exchange, 201, gson.toJson(response));
            } catch (Exception e) {
                handleException(exchange, e);
            }
        }

        private void handleGetMine(HttpExchange exchange) throws IOException {
            String userId = authorizeAndExtractUserId(exchange, gson);
            User seller = new UserDao().findById(Long.parseLong(userId));

            if (seller == null || seller.getRole() != UserRole.SELLER) {
                sendResponse(exchange, 403, gson.toJson(Map.of("error", "Only sellers can see restaurant")));
                return;
            }

            List<RestaurantDto> restaurants = restaurantService.findRestaurantsByISellerId(seller.getId());
            sendResponse(exchange, 200, gson.toJson(restaurants));
        }

        private void handleUpdateRestaurant(HttpExchange exchange, Long restaurantId) throws IOException {
            String userId = authorizeAndExtractUserId(exchange, gson);
            validateSellerAndRestaurant(userId, restaurantId);
            RestaurantUpdateRequest updateRequest = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), RestaurantUpdateRequest.class);

            try {
                RestaurantDto updated = restaurantService.updateRestaurant(restaurantId, updateRequest);
                sendResponse(exchange, 200, gson.toJson(updated));
            } catch (Exception e) {
                handleException(exchange, e);
            }
        }

        private void handleAddItem(HttpExchange exchange, Long restaurantId) throws IOException {
            String userId = authorizeAndExtractUserId(exchange, gson);
            ItemDto dto = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), ItemDto.class);
            itemValidator(dto);
            validateSellerAndRestaurant(userId, restaurantId);

            try {
                ItemDto item = itemService.addItem(restaurantId, dto);
                sendResponse(exchange, 200, gson.toJson(item));
            } catch (Exception e) {
                handleException(exchange, e);
            }
        }

        private void handleEditItem(HttpExchange exchange, Long restaurantId, Long itemId) throws IOException {
            String userId = authorizeAndExtractUserId(exchange, gson);
            ItemDto dto = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), ItemDto.class);
            validateSellerAndRestaurant(userId, restaurantId);

            try {
                ItemDto updated = itemService.editItem(restaurantId, itemId, dto, Long.parseLong(userId));
                sendResponse(exchange, 200, gson.toJson(Map.of("message", "Item updated successfully", "item", updated)));
            } catch (Exception e) {
                handleException(exchange, e);
            }
        }

        private void handleDeleteItem(HttpExchange exchange, Long restaurantId, Long itemId) throws IOException {
            String userId = authorizeAndExtractUserId(exchange, gson);
            validateSellerAndRestaurant(userId, restaurantId);

            try {
                itemService.deleteItem(restaurantId, itemId);
                sendResponse(exchange, 200, gson.toJson(Map.of("message", "Food item removed successfully")));
            } catch (Exception e) {
                handleException(exchange, e);
            }
        }

        private void handleException(HttpExchange exchange, Exception e) throws IOException {
            if (e instanceof InvalidInputException) {
                sendResponse(exchange, 400, gson.toJson(Map.of("error", "Invalid input")));
            } else if (e instanceof AlreadyExistsException) {
                sendResponse(exchange, 409, gson.toJson(Map.of("error", "Already exists")));
            } else if (e instanceof UnsupportedMediaTypeException) {
                sendResponse(exchange, 415, gson.toJson(Map.of("error", "Unsupported Media Type")));
            } else if (e instanceof TooManyRequestsException) {
                sendResponse(exchange, 429, gson.toJson(Map.of("error", "Too Many Requests")));
            } else if (e instanceof UnauthorizedUserException) {
                sendResponse(exchange, 401, gson.toJson(Map.of("error", "Unauthorized")));
            } else if (e instanceof ForbiddenException) {
                sendResponse(exchange, 403, gson.toJson(Map.of("error", "Forbidden")));
            } else if (e instanceof NotFoundException) {
                sendResponse(exchange, 404, gson.toJson(Map.of("error", "Not Found")));
            } else {
                e.printStackTrace();
                sendResponse(exchange, 500, gson.toJson(Map.of("error", "Internal server error")));
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, responseBody.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }
        }
    }
}
