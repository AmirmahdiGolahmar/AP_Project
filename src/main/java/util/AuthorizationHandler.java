package util;

import exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import spark.HaltException;
import spark.Request;
import spark.Response;

import java.util.Map;
import com.google.gson.Gson;


public class AuthorizationHandler {
    public static String authorizeAndExtractUserId(Request req, Response res, Gson gson) {
        String authHeader = req.headers("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.status(401);
            res.body(gson.toJson(Map.of("error", "Authorization header is missing or invalid")));
            return null;
        }

        String token = authHeader.substring(7);

        System.out.println("**** BlackList : " + TokenBlacklist.blacklist);

        if (TokenBlacklist.contains(token)) {
            res.status(403);
            res.body(gson.toJson(Map.of("error", "please login")));
            return null;
        }

        Claims claims;
        try {
            claims = JwtUtil.decodeJWT(token);
        } catch (Exception e) {
            res.status(401);
            res.body(gson.toJson(Map.of("error", "Invalid token")));
            return null;
        }

        String userId = claims.getSubject();
        if(userId == null) {
            res.status(401);
            res.body(gson.toJson(Map.of("error", "Authorization header is missing or invalid")));
            return null;
        }

        return userId;
    }

}
