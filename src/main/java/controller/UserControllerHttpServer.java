package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import entity.User;
import io.jsonwebtoken.Claims;
import service.UserService;
import util.JwtUtil;
import util.LocalDateTimeAdapter;
import util.TokenBlacklist;
import util.ResponseUtil;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import static exception.ExceptionHandler.expHandler;
import static util.ResponseUtil.sendResponse;

public class UserControllerHttpServer implements HttpHandler {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private static final UserService userService = new UserService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.equals("/auth/register") && method.equalsIgnoreCase("POST")) {
                handleRegister(exchange);
            } else if (path.equals("/auth/login") && method.equalsIgnoreCase("POST")) {
                handleLogin(exchange);
            } else if (path.equals("/auth/profile") && method.equalsIgnoreCase("GET")) {
                handleGetProfile(exchange);
            } else if (path.equals("/auth/profile") && method.equalsIgnoreCase("PUT")) {
                handleUpdateProfile(exchange);
            } else if (path.equals("/auth/logout") && method.equalsIgnoreCase("POST")) {
                handleLogout(exchange);
            } else {
                sendResponse(exchange, 404, gson.toJson(Map.of("error", "Not found")));
            }
        } catch (Exception e) {
            expHandler(e, exchange, gson);
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        UserRegistrationRequest request = readRequestBody(exchange, UserRegistrationRequest.class);
        try {
            User user = userService.createUser(request);
            User loggedInUser = userService.login(request.getMobile(), request.getPassword());
            String token = JwtUtil.generateToken(loggedInUser.getId(), loggedInUser.getRole().toString());

            UserRegistrationResponse response = new UserRegistrationResponse();
            response.setMessage("User registered successfully");
            response.setUser_id(loggedInUser.getId().toString());
            response.setToken(token);

            sendResponse(exchange, 201, gson.toJson(response));
        } catch (Exception e) {
            expHandler(e, exchange, gson);
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        LoginRequest request = readRequestBody(exchange, LoginRequest.class);
        try {
            User user = userService.login(request.getMobile(), request.getPassword());
            String token = JwtUtil.generateToken(user.getId(), user.getRole().toString());

            loginResponse response = new loginResponse("User Login successfully", token, user);
            sendResponse(exchange, 200, gson.toJson(response));
        } catch (Exception e) {
            expHandler(e, exchange, gson);
        }
    }

    private void handleGetProfile(HttpExchange exchange) throws IOException {
        try {
            String token = extractToken(exchange);
            Claims claims = JwtUtil.validateToken(token);
            Long userId = Long.parseLong(claims.getSubject());

            UserDto user = new UserDto(userService.findUserById(userId));
            sendResponse(exchange, 200, gson.toJson(user));
        } catch (Exception e) {
            expHandler(e, exchange, gson);
        }
    }

    private void handleUpdateProfile(HttpExchange exchange) throws IOException {
        try {
            String token = extractToken(exchange);
            Claims claims = JwtUtil.validateToken(token);
            Long userId = Long.parseLong(claims.getSubject());

            UserProfileUpdateRequest request = readRequestBody(exchange, UserProfileUpdateRequest.class);
            userService.updateProfile(userId, request);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "User profile updated successfully")));
        } catch (Exception e) {
            expHandler(e, exchange, gson);
        }
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        try {
            String token = extractToken(exchange);
            TokenBlacklist.add(token);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Logout successfully")));
        } catch (Exception e) {
            expHandler(e, exchange, gson);
        }
    }

    private String extractToken(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return auth.substring(7);
    }

    private <T> T readRequestBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, clazz);
    }


}
