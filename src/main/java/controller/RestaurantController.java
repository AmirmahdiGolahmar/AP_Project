package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import service.RestaurantService;
import service.UserService;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.BankInfo;
import io.jsonwebtoken.Claims;
import service.UserService;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;

import exception.*;

import dao.*;
import entity.*;
import util.JwtUtil;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.Map;

public class RestaurantController {

    private static final RestaurantService restaurantService = new RestaurantService();
    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void initRoutes(){
        path("/restaurant", () -> {
            post("", (req, res) -> {

                String userId = authorizeAndExtractUserId(req, res, gson);
                User seller = new UserDao().findById(Long.parseLong(userId)); // فرض بر اینه که متدش وجود داره

                if (seller == null || seller.getRole() != UserRole.SELLER) {
                    res.status(403); // Forbidden
                    return gson.toJson(Map.of("error", "Only sellers can create restaurants"));
                }

                // 4. ساختن رستوران
                RestaurantRegistrationRequest request = gson.fromJson(req.body(), RestaurantRegistrationRequest.class);
                restaurantService.createRestaurant(request, seller); // seller رو به متد پاس بده

                res.status(201);
                return gson.toJson(Map.of("message", "Restaurant created successfully"));
            });


            get("/mine", (req, res) -> {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                User seller = new UserDao().findById(Long.parseLong(userId)); // فرض بر اینه که متدش وجود داره

                if (seller == null || seller.getRole() != UserRole.SELLER) {
                    res.status(403); // Forbidden
                    return gson.toJson(Map.of("error", "Only sellers can see restaurant"));
                }
                return gson.toJson(restaurantService.findRestaurantsByISellerId(seller.getId()));
            });

            put("/:id", (req, res) -> {
                String userId = authorizeAndExtractUserId(req, res, gson);
                User seller = new UserDao().findById(Long.parseLong(userId));
                if (seller == null || seller.getRole() != UserRole.SELLER) {
                    res.status(403);
                    return gson.toJson(Map.of("error", "Only sellers can edit restaurant info"));
                }
                String idParam = req.params(":id");
                Long restaurantId;
                try {
                    restaurantId = Long.parseLong(idParam);
                } catch (NumberFormatException e) {
                    res.status(400);
                    return gson.toJson(Map.of("error", "Invalid restaurant ID"));
                }

                Restaurant restaurant = restaurantService.findById(restaurantId);
                if (restaurant == null) {
                    res.status(404);
                    return gson.toJson(Map.of("error", "Restaurant not found"));
                }
                if (!restaurant.getSeller().getId().equals(seller.getId())) {
                    res.status(403);
                    return gson.toJson(Map.of("error", "You are not authorized to update this restaurant"));
                }

                RestaurantUpdateRequest updateRequest = gson.fromJson(req.body(), RestaurantUpdateRequest.class);

                try {
                    RestaurantResponse updatedRestaurant = restaurantService.updateRestaurant(restaurantId, updateRequest);
                    res.status(200);
                    return gson.toJson(updatedRestaurant);
                } catch (IllegalArgumentException e) {
                    res.status(400);
                    return gson.toJson(Map.of("error", e.getMessage()));
                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(Map.of("error", "Internal server error"));
                }
            });

        });
    }
}
