package Log;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;

public class WrappedHttpExchange extends HttpExchange {
    private final HttpExchange original;
    private final InputStream newRequestBody;

    public WrappedHttpExchange(HttpExchange original, InputStream newRequestBody) {
        this.original = original;
        this.newRequestBody = newRequestBody;
    }

    @Override public Headers getRequestHeaders() { return original.getRequestHeaders(); }
    @Override public Headers getResponseHeaders() { return original.getResponseHeaders(); }
    @Override public URI getRequestURI() { return original.getRequestURI(); }
    @Override public String getRequestMethod() { return original.getRequestMethod(); }
    @Override public HttpContext getHttpContext() { return original.getHttpContext(); }
    @Override public void close() { original.close(); }
    @Override public InputStream getRequestBody() { return newRequestBody; }
    @Override public OutputStream getResponseBody() { return original.getResponseBody(); }
    @Override public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
        original.sendResponseHeaders(rCode, responseLength);
    }
    @Override public InetSocketAddress getRemoteAddress() { return original.getRemoteAddress(); }
    @Override public InetSocketAddress getLocalAddress() { return original.getLocalAddress(); }
    @Override public String getProtocol() { return original.getProtocol(); }
    @Override public Object getAttribute(String name) { return original.getAttribute(name); }
    @Override public void setAttribute(String name, Object value) { original.setAttribute(name, value); }
    @Override public void setStreams(InputStream i, OutputStream o) { original.setStreams(i, o); }
    @Override public HttpPrincipal getPrincipal() { return original.getPrincipal(); }

    // ✅ اضافه‌شده برای JDKهای جدید
    @Override
    public int getResponseCode() {
        return original.getResponseCode();
    }
}
