package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import io.jsonwebtoken.Claims;
import service.UserService;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;

import exception.*;

import entity.*;
import util.JwtUtil;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.Map;

import util.TokenBlacklist;

public class UserController {
    private static final UserService userService = new UserService();
    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    public static void initRoutes() {

        path("/auth", () -> {

            post("/register", (req, res) -> {
                UserRegistrationRequest request = gson.fromJson(req.body(), UserRegistrationRequest.class);

                try {
                    User user = userService.createUser(request);

                    User loggedInUser = userService.login(request.getMobile(), request.getPassword());
                    String token = JwtUtil.generateToken(loggedInUser.getId(), loggedInUser.getRole().toString());

                    res.status(201);
                    UserRegistrationResponse response = new UserRegistrationResponse();
                    response.setMessage("User registered successfully");
                    response.setUser_id(loggedInUser.getId().toString());
                    response.setToken(token);
                    return gson.toJson(response);

                }catch (Exception e) { 
                    return expHandler(e, res, gson);
                }
            });

            post("/login", (req, res) -> {
                LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

                try {
                    User user = userService.login(loginRequest.getMobile(), loginRequest.getPassword());
                    String token = JwtUtil.generateToken(user.getId(), user.getRole().toString());

                    res.status(200);
                    loginResponse response = new loginResponse("User Login successfully", token, user);
                    return gson.toJson(response);

                } catch(Exception e){
                    return expHandler(e, res, gson);
                }
            });

            get("/profile", (req, res) ->{
                try {
                    res.type("application/json");
                    String userId = authorizeAndExtractUserId(req, res, gson);
                    UserDto user = new UserDto(userService.findUserById((long) Integer.parseInt(userId)));
                    res.status(200);
                    return gson.toJson(user);
                }catch(Exception e){
                    return expHandler(e, res, gson);
                }

            });

            put("/profile", (req, res) -> {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                try{
                    UserProfileUpdateRequest updateRequest = gson.fromJson(req.body(), UserProfileUpdateRequest.class);
                    userService.updateProfile((long)Integer.parseInt(userId), updateRequest);
                    res.status(200);
                    return gson.toJson(Map.of("message", "User profile updated successfully"));

                } catch(Exception e){
                    return expHandler(e, res, gson);
                }
            });

            post("/logout", (req, res) -> {

                try {
                    res.type("application/json");

                    String authHeader = req.headers("Authorization");
                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        res.status(400);
                        return gson.toJson(Map.of("error", "No token provided"));
                    }

                    String token = authHeader.substring(7);
                    TokenBlacklist.add(token);

                    res.status(200);
                    return gson.toJson(Map.of("message", "logout successfully"));

                } catch(Exception e){
                    return expHandler(e, res, gson);
                }
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
            } catch(Exception e){
                return expHandler(e, res, gson);
            }
        });

    };

}

