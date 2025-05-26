package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.LoginRequest;
import dto.LoginResponse;
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

import dto.UserRegistrationRequest;

public class UserController {
    private static final UserService userService = new UserService();
    //private static final Gson gson = new Gson();
    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    public static void initRoutes() {

        path("/auth", () -> {

            post("/register", (req, res) -> {
                UserRegistrationRequest request = gson.fromJson(req.body(), UserRegistrationRequest.class);
                try {
                    userService.createUser(request);
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

                } catch (InvalidInputException e) {
                    res.status(400);
                    return gson.toJson(Map.of("error", e.getMessage()));

                } catch (AlreadyExistsException e) {
                    res.status(409);
                    return gson.toJson(Map.of("error", e.getMessage()));

                } catch (Exception e) {
                    res.status(500);
                    e.printStackTrace();
                    return gson.toJson(Map.of("error", "Internal server error"));
                }
            });

            post("/login", (req, res) -> {

                try {
                    LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
                    User user = userService.login(loginRequest);
                    String token = JwtUtil.generateToken(user.getId(), user.getRole().toString());

                    LoginResponse userDto = new LoginResponse(user);

                    res.status(200);
                    return gson.toJson(Map.of(
                            "message", "Login successful",
                            "token", token,
                            "user", userDto
                    ));

                } catch (InvalidInputException iie) {
                    res.status(400);
                    return gson.toJson(Map.of("error", iie.getMessage()));

                } catch (InvalidCredentialsException ice){
                    res.status(401);
                    return gson.toJson(Map.of("error", "Unauthorized request"));

                } catch (RuntimeException e) {
                    res.status(500);
                    return gson.toJson(Map.of("error", e.getMessage()));
                }


            });

            get("/profile", (req, res) -> {
                String authHeader = req.headers("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    res.status(401);
                    return gson.toJson(Map.of("error", "Missing or invalid Authorization header"));
                }

                String token = authHeader.substring(7);
                Claims claims;
                try {
                    claims = JwtUtil.verifyToken(token);
                } catch (Exception e) {
                    res.status(401);
                    return gson.toJson(Map.of("error", "Invalid token"));
                }

                Long userId = Long.valueOf(claims.getSubject());
                User user = userService.findUserById(userId);
                if (user == null) {
                    res.status(404);
                    return gson.toJson(Map.of("error", "User not found"));
                }
                res.status(200);
                return gson.toJson(user);

            });

//            put("/profile", (req, res) -> {
//                String authHeader = req.headers("Authorization");
//
//                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                    res.status(401);
//                    return gson.toJson(Map.of("error", "Missing or invalid Authorization header"));
//                }
//
//                String token = authHeader.substring(7);
//                Claims claims;
//                try {
//                    claims = JwtUtil.verifyToken(token);
//                } catch (Exception e) {
//                    res.status(401);
//                    return gson.toJson(Map.of("error", "Invalid token"));
//                }
//
//                Long userId = Long.valueOf(claims.getSubject());
//                User user = userService.findUserById(userId);
//
//                try {
//                    switch (user) {
////                        case Customer customer -> userService.updateCustomer();
////                        case Seller seller -> userService.updateSeller();
////                        case Delivery delivery -> userService.updateDelivery();
//                        case null, default -> throw new InvalidCredentialsException();
//                    }
//                    res.status(200);
//                    return gson.toJson(Map.of("message", "Profile updated successfully"));
//                } catch (InvalidInputException e) {
//                    res.status(400);
//                    return gson.toJson(Map.of("error", e.getMessage()));
//                }
//            });

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

}

