package exception;

import com.sun.net.httpserver.HttpExchange;
import spark.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import com.google.gson.Gson;

public class ExceptionHandler {
    public static Object expHandler(Exception ex, Response res, Gson gson) {
        if (ex instanceof InvalidInputException) {
            return gson.toJson(Map.of("error", "Invalid input"));
        } else if (ex instanceof UnauthorizedUserException) {
            res.status(401);
            return gson.toJson(Map.of("error", "Unauthorized"));
        } else if (ex instanceof ForbiddenException) {
            res.status(403);
            return gson.toJson(Map.of("error", "Forbidden request"));
        } else if (ex instanceof NotFoundException) {
            res.status(404);
            return gson.toJson(Map.of("error", "Resource not found"));
        } else if (ex instanceof AlreadyExistsException) {
            res.status(409);
            return gson.toJson(Map.of("error", "Conflict occurred"));
        } else if (ex instanceof UnsupportedMediaTypeException) {
            res.status(415);
            return gson.toJson(Map.of("error", "Unsupported Media Type"));
        } else if (ex instanceof TooManyRequestsException) {
            res.status(429);
            return gson.toJson(Map.of("error", "Too Many Requests"));
        } else if(ex instanceof AuthenticationException) {
            return gson.toJson(Map.of("error", "Unauthorized",
            "message", ex.getMessage()));
        } else {
            res.status(500);
            return gson.toJson(Map.of("error", "Internal server error"));
        }
    }

    public static void expHandler(Exception ex, HttpExchange exchange, Gson gson) throws IOException {
        int status;
        Map<String, Object> body;

        if (ex instanceof InvalidInputException) {
            status = 400;
            body = Map.of("error", "Invalid input","message", ex.getMessage());
        } else if (ex instanceof UnauthorizedUserException) {
            status = 401;
            body = Map.of("error", "Unauthorized","message", ex.getMessage());
        } else if (ex instanceof ForbiddenException) {
            status = 403;
            body = Map.of("error", "Forbidden request","message", ex.getMessage());
        } else if (ex instanceof NotFoundException) {
            status = 404;
            body = Map.of("error", "Resource not found","message", ex.getMessage());
        } else if (ex instanceof AlreadyExistsException) {
            status = 409;
            body = Map.of("error", "Conflict occurred","message", ex.getMessage());
        } else if (ex instanceof UnsupportedMediaTypeException) {
            status = 415;
            body = Map.of("error", "Unsupported Media Type","message", ex.getMessage());
        } else if (ex instanceof TooManyRequestsException) {
            status = 429;
            body = Map.of("error", "Too Many Requests","message", ex.getMessage());
        } else if (ex instanceof AuthenticationException) {
            status = 401;
            body = Map.of("error", "Unauthorized", "message", ex.getMessage());
        } else {
            status = 500;
            body = Map.of("error", "Internal server error","message", ex.getMessage());
        }

        String json = gson.toJson(body);
        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

}
