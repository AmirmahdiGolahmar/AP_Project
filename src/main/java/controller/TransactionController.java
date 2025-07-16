package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.TransactionDto;
import entity.UserRole;
import service.TransactionService;
import util.LocalDateTimeAdapter;
import dto.PaymentDto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static util.AuthorizationHandler.authorizeUserForRole;
import static validator.SellerValidator.validateRestaurant;
import static validator.RestaurantValidator.validateCouponId;


import java.time.LocalDateTime;

public class TransactionController {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private static final TransactionService transactionService = new TransactionService();

    public static void initRoutes() {
        get("/transactions", (req, res) -> {
            try{
                res.type("application/json");
                Long userId = (long) Integer.parseInt(authorizeAndExtractUserId(req, res, gson));
                List<TransactionDto> response = transactionService.getTransactions(userId);
                res.status(200);
                return gson.toJson(Map.of("List of transactions", response));
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        post("/wallet/top-up", (req, res) -> {
            try{
                res.type("application/json");
                Long userId = (long) Integer.parseInt(authorizeAndExtractUserId(req, res, gson));
                Map<String, Integer> bodyMap = gson.fromJson(req.body(), new TypeToken<Map<String, Integer>>(){}.getType());
                Integer amount = bodyMap.get("amount");
                transactionService.topUp(userId, amount);
                res.status(200);
                return gson.toJson("Wallet topped up successfully");
            }catch (Exception e){
                return expHandler(e, res , gson);
            }
        });

        post("payment/online", (req, res) -> {
            try{
                res.type("application/json");
                Long userId = (long) Integer.parseInt(authorizeAndExtractUserId(req, res, gson));
                PaymentDto request = gson.fromJson(req.body(), PaymentDto.class);

                res.status(200);
                return gson.toJson("");
            }catch (Exception e){
                return expHandler(e, res , gson);
            }
        });


    }
}
