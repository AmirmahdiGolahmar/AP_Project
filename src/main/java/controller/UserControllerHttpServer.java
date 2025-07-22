package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.AdminSeeder;
import dto.*;
import entity.User;
import io.jsonwebtoken.Claims;
import service.UserService;
import util.JwtUtil;
import util.LocalDateTimeAdapter;
import util.TokenBlacklist;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static util.HttpUtil.*;

import static exception.ExceptionHandler.expHandler;
import static exception.ExceptionHandler.handleNullPointerException;

public class UserControllerHttpServer {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls()
            .create();

    private static final UserService userService = new UserService();

    public static void init(HttpServer server, List<Filter> filters, Executor executor) {
        server.createContext("/auth", new AuthHandler(executor)).getFilters().addAll(filters);
    }

    static class AuthHandler implements HttpHandler{
        private final Executor executor;
        AuthHandler(Executor executor) {
            this.executor = executor;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            executor.execute(() -> {
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
                }catch(NullPointerException e){
                    handleNullPointerException(e);
                }
                catch (Exception e) {
                    expHandler(e, exchange, gson);
                }
            });
        }

        private void handleRegister(HttpExchange exchange) throws IOException {
            UserRegistrationRequest request = readRequestBody(exchange, UserRegistrationRequest.class, gson);
            User user = userService.createUser(request);
            User loggedInUser = userService.login(request.getMobile(), request.getPassword());
            String token = JwtUtil.generateToken(loggedInUser.getId(), loggedInUser.getRole().toString());

            UserRegistrationResponse response = new UserRegistrationResponse();
            response.setMessage("User registered successfully");
            response.setUser_id(loggedInUser.getId().toString());
            response.setToken(token);

            sendResponse(exchange, 201, gson.toJson(response));
        }

        private void handleLogin(HttpExchange exchange) throws IOException {
            LoginRequest request = readRequestBody(exchange, LoginRequest.class, gson);
            User user = userService.login(request.getMobile(), request.getPassword());
            String token = JwtUtil.generateToken(user.getId(), user.getRole().toString());

            loginResponse response = new loginResponse("User Login successfully", token, user);
            sendResponse(exchange, 200, gson.toJson(response));
        }

        private void handleGetProfile(HttpExchange exchange) throws IOException {
            String token = extractToken(exchange);
            Claims claims = JwtUtil.validateToken(token);
            Long userId = Long.parseLong(claims.getSubject());

            UserDto user = new UserDto(userService.findUserById(userId));
            sendResponse(exchange, 200, gson.toJson(user));
        }

        private void handleUpdateProfile(HttpExchange exchange) throws IOException {
            String token = extractToken(exchange);
            Claims claims = JwtUtil.validateToken(token);
            Long userId = Long.parseLong(claims.getSubject());

            UserProfileUpdateRequest request = readRequestBody(exchange, UserProfileUpdateRequest.class, gson);
            userService.updateProfile(userId, request);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "User profile updated successfully")));
        }

        private void handleLogout(HttpExchange exchange) throws IOException {
            String token = extractToken(exchange);
            TokenBlacklist.add(token);
            sendResponse(exchange, 200, gson.toJson(Map.of("message", "Logout successfully")));
        }
    }
}
