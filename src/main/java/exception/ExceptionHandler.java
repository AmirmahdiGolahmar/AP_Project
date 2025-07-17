package exception;

import spark.Response;

import java.util.Arrays;
import java.util.Map;
import com.google.gson.Gson;

public class ExceptionHandler {
    public static Object expHandler(Exception ex, Response res, Gson gson) {
        if (ex instanceof InvalidInputException) {
            System.out.println("********************");
            System.out.println("1: StackTrace");
            System.out.println(Arrays.toString(ex.getStackTrace()));
            System.out.println("2: Message");
            System.out.println(ex.getMessage());
            System.out.println("********************");
            res.status(400);
            return gson.toJson(Map.of("error", "Invalid input"));
        } else if (ex instanceof UnauthorizedUserException) {
            res.status(401);
            return gson.toJson(Map.of("error", "Unauthorized"));
        } else if (ex instanceof ForbiddenException) {
            System.out.println("1: StackTrace");
            System.out.println(Arrays.toString(ex.getStackTrace()));
            System.out.println("2: Message");
            System.out.println(ex.getMessage());
            System.out.println("********************");
            res.status(403);
            return gson.toJson(Map.of("error", "Forbidden request"));
        } else if (ex instanceof NotFoundException) {
            System.out.println("1: StackTrace");
            System.out.println(Arrays.toString(ex.getStackTrace()));
            System.out.println("2: Message");
            System.out.println(ex.getMessage());
            System.out.println("********************");
            res.status(404);
            return gson.toJson(Map.of("error", "Resource not found"));
        } else if (ex instanceof AlreadyExistsException) {
            System.out.println("1: StackTrace");
            System.out.println(Arrays.toString(ex.getStackTrace()));
            System.out.println("2: Message");
            System.out.println(ex.getMessage());
            System.out.println("********************");
            res.status(409);
            return gson.toJson(Map.of("error", "Conflict occurred"));
        } else if (ex instanceof UnsupportedMediaTypeException) {
            System.out.println("1: StackTrace");
            System.out.println(Arrays.toString(ex.getStackTrace()));
            System.out.println("2: Message");
            System.out.println(ex.getMessage());
            System.out.println("********************");
            res.status(415);
            return gson.toJson(Map.of("error", "Unsupported Media Type"));
        } else if (ex instanceof TooManyRequestsException) {
            System.out.println("1: StackTrace");
            System.out.println(Arrays.toString(ex.getStackTrace()));
            System.out.println("2: Message");
            System.out.println(ex.getMessage());
            System.out.println("********************");
            res.status(429);
            return gson.toJson(Map.of("error", "Too Many Requests"));
        } else if(ex instanceof AuthenticationException) {
            System.out.println("1: StackTrace");
            System.out.println(Arrays.toString(ex.getStackTrace()));
            System.out.println("2: Message");
            System.out.println(ex.getMessage());
            System.out.println("********************");
            return gson.toJson(Map.of("error", "Unauthorized",
            "message", ex.getMessage()));

        } else {
            res.status(500);
            ex.printStackTrace();
            return gson.toJson(Map.of("error", "Internal server error"));
        }
    }

}
