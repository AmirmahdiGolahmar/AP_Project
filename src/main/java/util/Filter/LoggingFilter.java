package util.Filter;

import util.Log.LogUtil;
import util.Log.WrappedHttpExchange;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class LoggingFilter extends Filter {
    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        // Save the original body into a byte[]
        InputStream originalInputStream = exchange.getRequestBody();
        byte[] requestBodyBytes = originalInputStream.readAllBytes();
        String requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);

        // بازنویسی request body برای handler
        InputStream newInputStream = new ByteArrayInputStream(requestBodyBytes);
        exchange = new WrappedHttpExchange(exchange, newInputStream);

        // Print Log
        LogUtil.logRequest(exchange.getRequestMethod(), exchange.getRequestURI().toString(), requestBody, "Request");

        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Logs request without consuming body";
    }
}
