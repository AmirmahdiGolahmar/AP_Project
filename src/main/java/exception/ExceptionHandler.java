package exception;

import com.sun.net.httpserver.HttpExchange;
import spark.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import com.google.gson.Gson;

import static util.HttpUtil.sendResponse;

public class ExceptionHandler {
    public static void expHandler(Exception ex, HttpExchange exchange, Gson gson) {
        int status;
        Map<String, Object> body;

        if (ex instanceof InvalidInputException) {
            status = 400;
            body = Map.of("error", "Invalid input", "message", ex.getMessage());
        } else if (ex instanceof UnauthorizedUserException || ex instanceof AuthenticationException ||
                ex instanceof AccessDeniedException) {
            status = 401;
            body = Map.of("error", "Unauthorized", "message", ex.getMessage());
        } else if (ex instanceof ForbiddenException) {
            status = 403;
            body = Map.of("error", "Forbidden request", "message", ex.getMessage());
        } else if (ex instanceof NotFoundException) {
            status = 404;
            body = Map.of("error", "Resource not found", "message", ex.getMessage());
        } else if (ex instanceof AlreadyExistsException || ex instanceof InvalidCredentialsException) {
            status = 409;
            body = Map.of("error", "Conflict occurred", "message", ex.getMessage());
        } else if (ex instanceof UnsupportedMediaTypeException) {
            status = 415;
            body = Map.of("error", "Unsupported Media Type", "message", ex.getMessage());
        } else if (ex instanceof TooManyRequestsException) {
            status = 429;
            body = Map.of("error", "Too Many Requests", "message", ex.getMessage());
        } else {
            status = 500;
            body = Map.of("error", "Internal server error", "message", ex.getMessage());
        }

        System.out.println("StackTrace : ");
        ex.printStackTrace();

        String json = gson.toJson(body);

        try{
            sendResponse(exchange, status, json);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void handleNullPointerException(NullPointerException ex) {
        StackTraceElement element = ex.getStackTrace()[0];
        String className = element.getClassName().toLowerCase();
        String methodName = element.getMethodName().toLowerCase();

        if(className.contains("bank") && className.contains("name")) throw new NullPointerException("Please fill your bank name");
        if(className.contains("account") && className.contains("number")) throw new NullPointerException("Please fill your account number");

        if(methodName.contains("bank") && methodName.contains("name")) throw new NullPointerException("Please fill your bank name");
        if(methodName.contains("account") && methodName.contains("number")) throw new NullPointerException("Please fill your account number");
    }


}
