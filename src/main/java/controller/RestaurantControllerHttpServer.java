package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.OrderDao;
import dto.*;
import entity.*;
import exception.InvalidInputException;
import exception.NotFoundException;
import io.jsonwebtoken.Claims;
import service.ItemService;
import service.MenuService;
import service.RestaurantService;
import util.JwtUtil;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static exception.ExceptionHandler.expHandler;
import static exception.ExceptionHandler.handleNullPointerException;
import static util.AuthorizationHandler.authorizeUser;
import static util.HttpUtil.*;
import static util.validator.RestaurantValidator.*;
import static util.validator.SellerValidator.matchSellerRestaurant;

public class RestaurantControllerHttpServer implements HttpHandler {

    private static final RestaurantService restaurantService = new RestaurantService();
    private static final ItemService itemService = new ItemService();
    private static final MenuService menuService = new MenuService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls()
            .create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String method = exchange.getRequestMethod();

        try {
            Matcher matcher;

            /*User*/
            String token = extractToken(exchange);
            Claims claims = JwtUtil.validateToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            Seller seller = (Seller) authorizeUser(userId, UserRole.SELLER);

            /*Role*/
            if (!"seller".equalsIgnoreCase(claims.get("role").toString())) {
                sendResponse(exchange, 403, gson.toJson(Map.of("error", "Only sellers can create restaurants")));
                return;
            }

            /*Restaurant*/  /*Match Seller and Restaurant*/
            Long restaurantId = null;
            Restaurant restaurant = null;
            matcher = Pattern.compile("/restaurants/(\\d+)").matcher(path);
            if (matcher.find()) {
                restaurantId = Long.parseLong(matcher.group(1));
                restaurant = validateRestaurant(restaurantId);
                matchSellerRestaurant(seller, restaurant);
            }


            /*item*/
            Long itemId = null;
            Item item = null;
            matcher = Pattern.compile("/item/(\\d+)").matcher(path);
            if (matcher.find()) {
                itemId = Long.parseLong(matcher.group(1));
                item = validateItem(itemId, restaurant);
            }


            /*Menu*/
            String menuTitle = null;
            matcher = Pattern.compile("/menu/(.+)/(\\d+)").matcher(path);
            if(matcher.find()){
                menuTitle = matcher.group(1);
                itemId = Long.parseLong(matcher.group(2));
                item = validateItem(itemId, restaurant);
            }

            matcher = Pattern.compile("/menu/(.+)").matcher(path);;
            if (matcher.find() && menuTitle == null) {
                menuTitle = matcher.group(1);
            }

            /*Order*/
            Long orderId = null;
            Order order = null;
            matcher = Pattern.compile("/orders/(\\d+)").matcher(path);
            if (matcher.find()) {
                orderId = Long.parseLong(matcher.group(1));
                order = new OrderDao().findById(orderId);
                if (order == null || order.getRestaurant().getId() != restaurantId)
                    throw new NotFoundException("This order does not exist");
            }

            /*Routing*/
            if (path.equals("/restaurants") && method.equals("POST")) {
                handleCreateRestaurant(exchange, seller);
            } else if (path.equals("/restaurants/mine") && method.equals("GET")) {
                handleGetMyRestaurants(exchange, seller);
            } else if (path.matches("/restaurants/\\d+") && method.equals("PUT")) {
                handleUpdateRestaurant(exchange, restaurant);
            } else if (path.matches("/restaurants/\\d+/item") && method.equals("POST")) {
                handleAddItem(exchange, restaurant);
            } else if (path.matches("/restaurants/\\d+/item/\\d+") && method.equals("PUT")) {
                handleEditItem(exchange, restaurant, item);
            } else if (path.matches("/restaurants/\\d+/item/\\d+") && method.equals("DELETE")) {
                handleDeleteItem(exchange, restaurant, item);
            } else if (path.matches("/restaurants/\\d+/menu") && method.equals("POST")) {
                handleAddMenu(exchange, restaurant);
            } else if (path.matches("/restaurants/\\d+/menu/.+/\\d+") && method.equals("DELETE")) {
                handleDeleteItemFromMenu(exchange, restaurant, menuTitle, item);
            } else if (path.matches("/restaurants/\\d+/menu/.+") && method.equals("DELETE")) {
                handleDeleteMenu(exchange, restaurant, menuTitle);
            } else if (path.matches("/restaurants/\\d+/menu/.+") && method.equals("PUT")) {
                handleAddItemToMenu(exchange, restaurant, menuTitle);
            } else if (path.matches("/restaurants/\\d+/orders") && method.equals("GET")) {
                handleGetRestaurantOrders(exchange, restaurant);
            } else if (path.matches("/restaurants/orders/\\d+") && method.equals("PATCH")) {
                handleChangeOrderStatus(exchange, order);
            } else {
                sendResponse(exchange, 404, gson.toJson(Map.of("error", "Not found")));
            }
        } catch (NullPointerException e) {
            handleNullPointerException(e);
        } catch (Exception e) {
            expHandler(e, exchange, gson);
        }
    }

    private void handleCreateRestaurant(HttpExchange exchange, Seller seller) throws IOException {
        RestaurantRegistrationRequest request = readRequestBody(exchange, RestaurantRegistrationRequest.class, gson);
        validateRestaurantRegistrationRequest(request);
        Restaurant restaurant = restaurantService.createRestaurant(request, seller);
        RestaurantDto response = new RestaurantDto(restaurant);
        sendResponse(exchange, 200, gson.toJson(Map.of("message", "Restaurant created successfully", "response", response)));
    }

    private void handleGetMyRestaurants(HttpExchange exchange, Seller seller) throws IOException {
        List<RestaurantDto> restaurants = restaurantService.getSellerRestaurants(seller);
        sendResponse(exchange, 200, gson.toJson(Map.of("List of restaurants", restaurants)));
    }

    private void handleUpdateRestaurant(HttpExchange exchange, Restaurant restaurant) throws IOException {
        RestaurantUpdateRequest request = readRequestBody(exchange, RestaurantUpdateRequest.class, gson);
        RestaurantDto response = restaurantService.updateRestaurant(restaurant, request);
        sendResponse(exchange, 200, gson.toJson(response));
    }

    private void handleAddItem(HttpExchange exchange, Restaurant restaurant) throws IOException {
        ItemDto request = readRequestBody(exchange, ItemDto.class, gson);
        validateItemRegistrationRequest(request);
        ItemDto response = itemService.addItem(restaurant, request);
        sendResponse(exchange, 200, gson.toJson(
                Map.of("Food item created and added to restaurant successfully", response))
        );
    }

    private void handleEditItem(HttpExchange exchange, Restaurant restaurant, Item item) throws IOException {
        ItemDto request = readRequestBody(exchange, ItemDto.class, gson);
        ItemDto updatedItem = itemService.editItem(restaurant, item, request);
        sendResponse(exchange, 200, gson.toJson(Map.of("message", "Item updated successfully", "item", updatedItem)));
    }

    private void handleDeleteItem(HttpExchange exchange, Restaurant restaurant, Item item) throws IOException {
        itemService.deleteItem(restaurant, item);
        sendResponse(exchange, 200, gson.toJson(Map.of("message", "Food item removed successfully")));
    }

    private void handleAddMenu(HttpExchange exchange, Restaurant restaurant) throws IOException {
        MenuRegistrationDto request = readRequestBody(exchange, MenuRegistrationDto.class, gson);
        validateMenuRegistrationRequest(request);
        Menu menu = menuService.addMenu(request, restaurant);
        sendResponse(exchange, 200, gson.toJson(Map.of("message", "Food menu created and added to restaurant successfully", "title", menu.getTitle())));
    }

    private void handleDeleteMenu(HttpExchange exchange,Restaurant restaurant ,String menuTitle) throws IOException {
        menuService.deleteMenu(menuTitle, restaurant);
        sendResponse(exchange, 200, gson.toJson(Map.of("message", "Food menu removed from restaurant successfully")));
    }

    private void handleAddItemToMenu(HttpExchange exchange, Restaurant restaurant, String menuTitle) throws IOException {
        ItemAddToMenuRequestDto request = readRequestBody(exchange, ItemAddToMenuRequestDto.class, gson);
        validateItemAddToMenuRequest(request);
        menuService.addItem(menuTitle, restaurant, request);
        sendResponse(exchange, 200, gson.toJson(Map.of("message", "Food item added to restaurant menu successfully")));
    }

    private void handleDeleteItemFromMenu(HttpExchange exchange, Restaurant restaurant, String menuTitle, Item item) throws IOException {
        menuService.deleteItem(menuTitle, item, restaurant);
        sendResponse(exchange, 200, gson.toJson(Map.of("message", "Item removed from restaurant menu successfully")));
    }

    private void handleGetRestaurantOrders(HttpExchange exchange, Restaurant restaurant) throws IOException {
        Map<String, String> params = Map.of(
                "search", getQueryParam(exchange, "search"),
                "courier", getQueryParam(exchange, "courier"),
                "user", getQueryParam(exchange, "user"),
                "status", getQueryParam(exchange, "status")
        );
        List<OrderDto> orders = restaurantService.searchRestaurantOrders(
                params.get("search"), params.get("courier"), params.get("user"), params.get("status"), restaurant);
        sendResponse(exchange, 200, gson.toJson(Map.of("List of orders", orders)));
    }

    private void handleChangeOrderStatus(HttpExchange exchange, Order order) throws IOException {
        StatusDto request = readRequestBody(exchange, StatusDto.class, gson);
        if(request == null || request.getStatus() == null) throw new InvalidInputException("Invalid request");
        restaurantService.changeOrderStatus(order, request);
        sendResponse(exchange, 200, gson.toJson("Order status changed successfully"));
    }





}
