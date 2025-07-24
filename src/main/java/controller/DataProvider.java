package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dao.ItemDao;
import dao.OrderDao;
import dao.RestaurantDao;
import dao.UserDao;
import dto.*;
import entity.Item;
import entity.Order;
import entity.Restaurant;
import entity.User;
import io.jsonwebtoken.Claims;
import service.RestaurantService;
import service.UserService;
import util.JwtUtil;
import util.LocalDateTimeAdapter;
import util.TokenBlacklist;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static exception.ExceptionHandler.expHandler;
import static exception.ExceptionHandler.handleNullPointerException;
import static util.HttpUtil.*;
import static util.validator.RestaurantValidator.validateItem;
import com.fasterxml.jackson.databind.ObjectMapper;


public class DataProvider {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls()
            .create();

    private static final RestaurantDao restaurantDao = new RestaurantDao();
    private static final OrderDao orderDao = new OrderDao();
    private static final ItemDao itemDao = new ItemDao();
    private static final UserService userService = new UserService();
    private static final RestaurantService restaurantService = new RestaurantService();

    public static void init(HttpServer server, List<Filter> filters, Executor executor) {
        server.createContext("/dt", new DataProvider.dtHandler(executor)).getFilters().addAll(filters);
    }

    static class dtHandler implements HttpHandler {
        private final Executor executor;
        dtHandler(Executor executor) {
            this.executor = executor;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            executor.execute(() -> {
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                String method = exchange.getRequestMethod();

                try {
                    Matcher matcher;
                    if (path.matches("/dt/user/\\d+") && method.equals("GET")) {
                        matcher = Pattern.compile("/dt/user/(\\d+)").matcher(path);
                        Long userId = null;
                        if (matcher.find()) userId = Long.parseLong(matcher.group(1));
                        handleGetUserData(exchange, userId);
                    } else if (path.matches("/dt/restaurant/\\d+") && method.equals("GET")) {
                        matcher = Pattern.compile("/dt/restaurant/(\\d+)").matcher(path);
                        Long restaurantId = null;
                        if (matcher.find()) restaurantId = Long.parseLong(matcher.group(1));
                        handleGetRestaurant(exchange, restaurantId);
                    }else if (path.matches("/dt/item/\\d+") && method.equals("GET")) {
                            matcher = Pattern.compile("/dt/item/(\\d+)").matcher(path);
                            Long itemId = null;
                            if (matcher.find()) itemId = Long.parseLong(matcher.group(1));
                            handleGetItem(exchange, itemId);
                    } else {
                        sendResponse(exchange, 404, gson.toJson(Map.of("error", "Not found")));
                    }
                }catch(NullPointerException e){
                    handleNullPointerException(e);
                }
                catch (Exception e) {
                    expHandler(e, exchange, gson);
                }
            });
        }

        private void handleGetItem(HttpExchange exchange, Long itemId) throws IOException {
            Item item = itemDao.findById(itemId);
            ItemDto response = new ItemDto(item);
            sendResponse(exchange, 200, gson.toJson(response));
        }

        private void handleGetRestaurant(HttpExchange exchange, Long restaurantId) throws IOException {
            Restaurant restaurant = restaurantDao.findById(restaurantId);
            RestaurantDto restaurantDto = new RestaurantDto(restaurant);
            sendResponse(exchange, 200, gson.toJson(restaurantDto));
        }

        private void handleGetUserData(HttpExchange exchange, Long userId) throws IOException {
            UserDto user = new UserDto(userService.findUserById(userId));
            sendResponse(exchange, 200, gson.toJson(user));
        }
    }
}
