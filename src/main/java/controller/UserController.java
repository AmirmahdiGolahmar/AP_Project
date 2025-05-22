package controller;

import com.google.gson.Gson;
import dto.LoginRequest;
import entity.BankInfo;
import io.jsonwebtoken.Claims;
import service.UserService;
import static spark.Spark.*;

import dao.*;
import entity.*;
import util.JwtUtil;

import java.util.Map;

public class UserController {
    private static final UserService userService = new UserService();
    private static final Gson gson = new Gson();

    public static void initRoutes() {

        path("/api/user", () -> {

            post("/customer", (req, res) -> {

                CustomerRequest body = gson.fromJson(req.body(), CustomerRequest.class);
                userService.createUser(
                        Customer.class,
                        body.password,
                        body.firstName, body.lastName,
                        body.mobile, body.email, body.address,
                        body.photo, body.bankName, body.accountNumber,
                        null, UserRole.CUSTOMER
                );
                res.status(201);
                return "Customer created";
            });

            post("/seller", (req, res) -> {
                SellerRequest body = gson.fromJson(req.body(), SellerRequest.class);
                userService.createUser(
                        Seller.class,
                        body.password,
                        body.firstName, body.lastName,
                        body.mobile, body.email, body.address,
                        body.photo, body.bankName, body.accountNumber,
                        body.restaurantDescription, UserRole.SELLER
                );
                res.status(201);
                return "Seller created";
            });

            post("/delivery", (req, res) -> {
                DeliveryRequest body = gson.fromJson(req.body(), DeliveryRequest.class);
                userService.createUser(
                        Delivery.class,
                        body.password,
                        body.firstName, body.lastName,
                        body.mobile, body.email, body.address,
                        body.photo, body.bankName, body.accountNumber,
                        null, UserRole.DELIVERY
                );
                res.status(201);
                return "Delivery agent created";
            });
        });

        get("/customers", (req, res) -> {
           res.type("application/json");
           return gson.toJson(userService.findAllCustomers());
        });

        get("/sellers", (req, res) -> {
            res.type("application/json");
            return gson.toJson(userService.findAllSellers());
        });

        get("/deliveries", (req, res) -> {
            res.type("application/json");
            return gson.toJson(userService.findAllDeliveries());
        });

        get("/all", (req, res) -> {
            res.type("application/json");
            return gson.toJson(userService.findAllUsers());
        });

        delete("/:id", (req, res) -> {
            Long id = Long.parseLong(req.params(":id"));
            userService.deleteUser(id);
            return "User deleted with id: " + id;
        });

        post("/login", (req, res) -> {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

            try {
                User user = userService.login(loginRequest.getMobile(), loginRequest.getPassword());
                String token = JwtUtil.generateToken(user.getId(), user.getRole().toString());

                res.status(200);
                return gson.toJson(Map.of(
                        "token", token,
                            "full name", user.getFirstName()+ " " + user.getLastName(),
                        "id", user.getId(),
                        "role", user.getRole().toString()
                ));
            } catch (RuntimeException e) {
                res.status(401);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        get("/me", (req, res) -> {
            String authHeader = req.headers("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                res.status(401);
                return gson.toJson(Map.of("error", "Missing or invalid Authorization header"));
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = JwtUtil.verifyToken(token);
                Long userId = Long.valueOf(claims.getSubject());
                String role = claims.get("role", String.class);

                return gson.toJson(Map.of(
                        "userId", userId,
                        "role", role
                ));
            } catch (RuntimeException e) {
                res.status(401);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });


    };

    // Inner classes for request bodies
    static class CustomerRequest {
        String password, firstName, lastName;
        String mobile, email, address, photo;
        String bankName, accountNumber;
    }

    static class SellerRequest extends CustomerRequest {
        String restaurantDescription;
    }

    static class DeliveryRequest extends CustomerRequest {
    }
}

