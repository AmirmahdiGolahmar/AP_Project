package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.RestaurantRegistrationRequest;
import dto.UserRegistrationRequest;
import service.RestaurantService;
import service.UserService;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.LoginRequest;
import entity.BankInfo;
import io.jsonwebtoken.Claims;
import service.UserService;
import static spark.Spark.*;
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
                // 1. خواندن Authorization header
                String authHeader = req.headers("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    res.status(401);
                    return gson.toJson(Map.of("error", "Authorization header is missing or invalid"));
                }

                // 2. استخراج توکن و اعتبارسنجی
                String token = authHeader.substring(7); // Remove "Bearer "
                Claims claims;
                try {
                    claims = JwtUtil.decodeJWT(token);
                } catch (Exception e) {
                    res.status(401);
                    return gson.toJson(Map.of("error", "Invalid token"));
                }

                // 3. گرفتن mobile از توکن و پیدا کردن یوزر
                String userId = claims.getSubject();
                User seller = new UserDao().findById(Long.parseLong(userId)); // فرض بر اینه که متدش وجود داره

                if (seller == null || seller.getRole() != UserRole.SELLER) {
                    res.status(403); // Forbidden
                    return gson.toJson(Map.of("error", "Only sellers can create restaurants" + seller.getRole()));
                }

                // 4. ساختن رستوران
                RestaurantRegistrationRequest request = gson.fromJson(req.body(), RestaurantRegistrationRequest.class);
                restaurantService.createRestaurant(request, seller); // seller رو به متد پاس بده

                res.status(201);
                return gson.toJson(Map.of("message", "Restaurant created successfully"));
            });


            get("/mine", (req, res) -> {
                res.type("application/json");
                return gson.toJson(restaurantService.findAllRestaurants());
            });
        });
    }
}
