package util;

import dao.UserDao;
import entity.User;
import entity.UserRole;
import exception.AuthenticationException;
import exception.NotFoundException;
import io.jsonwebtoken.Claims;
import spark.Request;
import spark.Response;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;

import static util.HttpUtil.extractToken;


public class AuthorizationHandler {
    public static String authorizeAndExtractUserId (Request req, Response res, Gson gson) throws AuthenticationException  {
        try {
            String authHeader = req.headers("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationException("login first");
            }

            String token = authHeader.substring(7);

            if (TokenBlacklist.contains(token)) {
                throw new AuthenticationException("login first");
            }

            Claims claims = JwtUtil.validateToken(token);
            return claims.getSubject();

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("invalid token");
        }
    }
    public static String authorizeAndExtractUserId(HttpExchange exchange, Gson gson) throws AuthenticationException {
        try {
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationException("login first");
            }

            String token = authHeader.substring(7);

            if (TokenBlacklist.contains(token)) {
                throw new AuthenticationException("login first");
            }

            Claims claims = JwtUtil.validateToken(token);
            return claims.getSubject();

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("invalid token");
        }
    }

    public static User authorizeUser(long userId, UserRole role) {
        User user = new UserDao().findById(userId);
        if(user == null) {
            throw new NotFoundException("User doesn't exist");
        }
        if(user.getRole() != role) {
            throw new AuthenticationException("You are not Authorized for this role");
        }
        return user;
    }

    public static <T> T authorize(HttpExchange exchange, UserRole expectedRole) {
        String token = extractToken(exchange);
        Claims claims = JwtUtil.validateToken(token);
        Long userId = Long.parseLong(claims.getSubject());
        return (T) authorizeUser(userId, expectedRole);
    }

}
