package util.Filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import util.RateLimiter;

import java.io.IOException;
import java.io.OutputStream;

public class RateLimitFilter extends Filter {
    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String ip = exchange.getRemoteAddress().getAddress().getHostAddress();

        if (!RateLimiter.isAllowed(ip)) {
            String msg = "429 Too Many Requests\n";
            exchange.sendResponseHeaders(429, msg.length());
            OutputStream os = exchange.getResponseBody();
            os.write(msg.getBytes());
            os.close();
            return;
        }

        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "Rate limiter filter";
    }
}
