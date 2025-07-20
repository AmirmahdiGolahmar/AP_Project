package util.Filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.*;

public class TimeoutFilter extends Filter {

    private static final long TIMEOUT_SECONDS = 2;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public String description() {
        return "Handles request timeouts by cancelling long-running handlers.";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        Future<?> future = executor.submit(() -> {
            try {
                chain.doFilter(exchange);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            String response = "Request timed out";
            exchange.sendResponseHeaders(504, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (InterruptedException | ExecutionException e) {
            future.cancel(true);
            String response = "Internal server error";
            exchange.sendResponseHeaders(500, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
