package util;

import Log.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HttpUtil {

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static void sendResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);


        JsonElement jsonElement = JsonParser.parseString(responseJson);
        String prettyResponse = gson.toJson(jsonElement);

        LogUtil.logRequest(exchange.getRequestMethod(), exchange.getRequestURI().toString(), prettyResponse, "Response");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    public static <T> T readRequestBody(HttpExchange exchange, Class<T> clazz, Gson gson) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, clazz);
    }

    public static <T> T readRequestBody(HttpExchange exchange, TypeToken<T> typeToken,Gson gson) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, typeToken.getType());
    }

    public static String extractToken(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return auth.substring(7);
    }

    public static String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;

        for (String param : query.split("&")) {
            String[] entry = param.split("=", 2);
            if (entry.length == 2) {
                String paramKey = decode(entry[0]);
                String paramValue = decode(entry[1]);
                if (paramKey.equals(key)) return paramValue;
            }
        }

        return null;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return value; // fallback
        }
    }


}
