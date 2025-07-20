package controller;

import Log.LoggingFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dto.AmountDto;
import dto.PaymentReceiptDto;
import dto.PaymentRequestDto;
import dto.TransactionDto;
import entity.User;
import entity.UserRole;
import service.TransactionService;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static exception.ExceptionHandler.expHandler;
import static util.AuthorizationHandler.authorize;
import static util.HttpUtil.readRequestBody;
import static util.HttpUtil.sendResponse;

public class TransactionControllerHttpServer {

    private static final TransactionService transactionService = new TransactionService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();

    public static void init(HttpServer server) {
        server.createContext("/transactions", new TransactionsHandler()).getFilters().add(new LoggingFilter());
        server.createContext("/wallet/top-up", new WalletTopUpHandler()).getFilters().add(new LoggingFilter());
        server.createContext("/payment/online", new OnlinePaymentHandler()).getFilters().add(new LoggingFilter());
    }

    static class TransactionsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetTransactions(exchange);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handleGetTransactions(HttpExchange exchange) throws IOException {
            User user = authorize(exchange, null);
            List<TransactionDto> response = transactionService.getTransactions(user.getId());
            sendResponse(exchange, 200, gson.toJson(Map.of("List of transactions", response)));
        }
    }

    static class WalletTopUpHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("POST".equals(exchange.getRequestMethod())) {
                    handleTopUp(exchange);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private  void handleTopUp(HttpExchange exchange) throws IOException {
            User user = authorize(exchange, UserRole.CUSTOMER);
            AmountDto request = readRequestBody(exchange, AmountDto.class, gson);
            transactionService.topUp(user, request);
            sendResponse(exchange, 200, gson.toJson("Wallet topped up successfully"));
        }
    }

    static class OnlinePaymentHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("POST".equals(exchange.getRequestMethod())) {
                    handlePayment(exchange);
                    return;
                }

                sendResponse(exchange, 404, "Invalid path");

            } catch (Exception e) {
                expHandler(e, exchange, gson);
            }
        }

        private void handlePayment(HttpExchange exchange) throws IOException {
            User user = authorize(exchange, UserRole.CUSTOMER);
            PaymentRequestDto request = readRequestBody(exchange, PaymentRequestDto.class, gson);
            PaymentReceiptDto response = transactionService.pay(request, user);
            sendResponse(exchange, 200, gson.toJson(Map.of("Payment successful", response)));
        }
    }
}
