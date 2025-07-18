package controller;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static util.AuthorizationHandler.authorizeUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dto.OrderDto;
import entity.UserRole;
import service.DeliveryService;
import util.LocalDateTimeAdapter;

public class DeliveryController {
    private static final DeliveryService deliveryService = new DeliveryService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();

    public static void initRoutes(){
        path("/deliveries", () -> {
            get("/available", (req, res) -> {
                try {
                    res.type("application/json");
                    String userId = authorizeAndExtractUserId(req, res, gson);
                    authorizeUser(Integer.parseInt(userId), UserRole.DELIVERY);
                    List<OrderDto> response = deliveryService.getAvailableOrders();
                    res.status(200);
                    return gson.toJson(Map.of("List of available deliveries", response));
                } catch (Exception e) {
                    return expHandler(e, res, gson);
                }
            });

            patch("/:id", (req, res) -> {
                try {
                    res.type("application/json");
                    String userId = authorizeAndExtractUserId(req, res, gson);
                    authorizeUser(Integer.parseInt(userId), UserRole.DELIVERY);
                    Long order_id = (long)Integer.parseInt(req.params(":id"));

                    Map<String, String> bodyMap = gson.fromJson(req.body(), new TypeToken<Map<String, String>>(){}.getType());
                    String status = bodyMap.get("status");

                    OrderDto response = deliveryService.changeOrderStatus(userId, order_id, status);
                    res.status(200);
                    return gson.toJson(Map.of("Changed status successfully", response));
                } catch (Exception e) {
                    return expHandler(e, res, gson);
                }
            });

            get("/history", (req, res) -> {
                try {
                    res.type("application/json");
                    Long userId = (long) Integer.parseInt(authorizeAndExtractUserId(req, res, gson));
                    authorizeUser(userId, UserRole.DELIVERY);

                    String search = req.queryParams("search");
                    String vendor = req.queryParams("vendor");
                    String user = req.queryParams("user");

                    List<OrderDto> response = deliveryService.searchDeliveryHistory(search, vendor, user, userId);
                    res.status(200);
                    return gson.toJson(Map.of("List of completed and active deliveries", response));
                } catch (Exception e) {
                    return expHandler(e, res, gson);
                }
            });
        });
    }

}
