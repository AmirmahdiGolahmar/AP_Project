package util;

import exception.auth.UnauthorizedUserException;
import io.jsonwebtoken.Claims;
import spark.HaltException;
import spark.Request;
import spark.Response;

import java.util.Map;
import com.google.gson.Gson;


public class AuthorizationHandler {
    public static String authorizeAndExtractUserId(Request req) {
        try {
            String authHeader = req.headers("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedUserException("login first");
            }

            String token = authHeader.substring(7);

            if (TokenBlacklist.contains(token)) {
                throw new UnauthorizedUserException("login first");
            }

            Claims claims = JwtUtil.decodeJWT(token);
            return claims.getSubject();

        } catch (UnauthorizedUserException uue) {
            throw uue;
        } catch (Exception e) {
            throw new UnauthorizedUserException("invalid token");
        }
    }

}
