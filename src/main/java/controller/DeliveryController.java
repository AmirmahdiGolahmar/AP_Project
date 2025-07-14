package controller;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static util.AuthorizationHandler.authorizeUserForRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dto.DeliverySearchRequestDto;
import dto.OrderDto;
import dto.RestaurantDto;
import dto.RestaurantSearchRequestDto;
import entity.UserRole;
import service.CustomerService;
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
                    authorizeUserForRole(Integer.parseInt(userId), UserRole.DELIVERY);
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
                    authorizeUserForRole(Integer.parseInt(userId), UserRole.DELIVERY);
                    Long order_id = (long)Integer.parseInt(req.params(":id"));
                    OrderDto response = deliveryService.acceptOrder(userId, order_id);
                    res.status(200);
                    return gson.toJson(Map.of("Changed status successfully", response));
                } catch (Exception e) {
                    return expHandler(e, res, gson);
                }
            });

            get("/history", (req, res) -> {
                try {
                    res.type("application/json");
                    String userId = authorizeAndExtractUserId(req, res, gson);
                    authorizeUserForRole(Integer.parseInt(userId), UserRole.DELIVERY);
                    DeliverySearchRequestDto request = gson.fromJson(req.body(), DeliverySearchRequestDto.class);
                    List<OrderDto> response = deliveryService.deliveryHistory(request, userId);
                    res.status(200);
                    return gson.toJson(Map.of("List of completed and active deliveries", response));
                } catch (Exception e) {
                    return expHandler(e, res, gson);
                }
            });
        });
    }

}
