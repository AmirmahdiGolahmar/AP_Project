package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.LoginRequest;
import dto.UserProfileResponse;
import dto.UserProfileUpdateRequest;
import io.jsonwebtoken.Claims;
import service.UserService;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;

import exception.*;

import entity.*;
import util.JwtUtil;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.Map;

import dto.UserRegistrationRequest;
import util.TokenBlacklist;

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
                    res.status(201);
                    return gson.toJson(Map.of("message", "User registered successfully"));

                } catch (AlreadyExistsException e) {
                    res.status(409);
                    return gson.toJson(Map.of("error", e.getMessage()));

                } catch (InvalidInputException e) {
                    res.status(400);
                    return gson.toJson(Map.of("error", e.getMessage()));

                } catch (Exception e) {
                    res.status(500);
                    e.printStackTrace();
                    return gson.toJson(Map.of("error", "Internal server error"));
                }
            });

            post("/login", (req, res) -> {
                LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

                try {
                    User user = userService.login(loginRequest.getMobile(), loginRequest.getPassword());
                    String token = JwtUtil.generateToken(user.getId(), user.getRole().toString());

                    res.status(200);
                    return gson.toJson(Map.of(
                            "token", token,
                            "full name", user.getFullName(),
                            "id", user.getId(),
                            "role", user.getRole().toString()
                    ));

                } catch (UserNotFoundException e) {
                    res.status(404);
                    return gson.toJson(Map.of("error", e.getMessage()));

                } catch (InvalidCredentialsException e) {
                    res.status(401);
                    return gson.toJson(Map.of("error", e.getMessage()));

                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(Map.of("error", "Internal server error"));
                }
            });

            get("/profile", (req, res) ->{
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                UserProfileResponse user =
                        new UserProfileResponse(userService.findUserById((long) Integer.parseInt(userId)));
                return gson.toJson(user);
            });

            put("/profile", (req, res) -> {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                UserProfileUpdateRequest updateRequest = gson.fromJson(req.body(), UserProfileUpdateRequest.class);
                System.out.println("Parsed full name: " + updateRequest.getFull_name());
                System.out.println("req.body :\n" + req.body());
                try{
                    userService.updateProfile((long) Integer.parseInt(userId), updateRequest);
                    res.status(200);
                    System.out.println("Updated user: " + gson.toJson(updateRequest));
                    return gson.toJson(Map.of("message", "Profile updated successfully"));
                }catch (UserNotFoundException e) {
                    res.status(404);
                    return gson.toJson(Map.of("error", e.getMessage()));
                }catch(AuthenticationException e){
                    res.status(401);
                    return gson.toJson(Map.of("error", e.getMessage()));
                }catch (Exception e) {
                    res.status(500);
                    return gson.toJson(Map.of("error", "Internal server error"));
                }
            });

            post("/logout", (req, res) -> {
                res.type("application/json");

                String authHeader = req.headers("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    res.status(400);
                    return gson.toJson(Map.of("error", "No token provided"));
                }

                String token = authHeader.substring(7);
                TokenBlacklist.add(token);

                res.status(200);
                return gson.toJson(Map.of("message", "Logged out successfully"));
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



        get("/me", (req, res) -> {
            String authHeader = req.headers("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                res.status(401);
                return gson.toJson(Map.of("error", "Missing or invalid Authorization header"));
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = JwtUtil.decodeJWT(token);
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

