package util;

import exception.UnauthorizedUserException;
import io.jsonwebtoken.Claims;
import spark.HaltException;
import spark.Request;
import spark.Response;

import java.util.Map;
import com.google.gson.Gson;


public class AuthorizationHandler {
    public static String authorizeAndExtractUserId(Request req) {
        String authHeader = req.headers("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedUserException("Authorization header is missing or invalid");
            return null;
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = JwtUtil.decodeJWT(token);
        } catch (Exception e) {
            throw new UnauthorizedUserException("Invalid token");
        }

        String userId = claims.getSubject();
        if(userId == null) {
            throw new UnauthorizedUserException("Authorization header is missing or invalid");
        }

        return userId;
    }

}
