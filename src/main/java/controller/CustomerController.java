package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.*;
import service.CustomerService;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static util.AuthorizationHandler.authorizeUserAsCustomer;
import static validator.SellerValidator.validateRestaurant;
import static validator.RestaurantValidator.validateCouponId;

public class CustomerController {
    private static final CustomerService customerService = new CustomerService();
    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();

    public static void initRoutes(){

        post("/vendors", (req, res) ->{
            try{
                res.type("application/json");
                RestaurantSearchRequestDto request = gson.fromJson(req.body(), RestaurantSearchRequestDto.class);
                List<RestaurantDto> response= customerService.searchRestaurant(request);
                return gson.toJson(response);
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        get("/vendors/:id", (req, res) ->{
            try{
                res.type("application/json");
                long restaurantId = Long.parseLong(req.params(":id"));
                validateRestaurant(restaurantId);
                RestaurantDisplayResponse response = customerService.displayRestaurant(restaurantId);
                return gson.toJson(response);
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        post("/items", (req, res) ->{
            try{
                res.type("application/json");
                ItemSearchRequestDto request = gson.fromJson(req.body(), ItemSearchRequestDto.class);
                List<ItemDto> response= customerService.searchItem(request);
                return gson.toJson(response);
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        get("/items/:id", (req, res) ->{
            try{
                res.type("application/json");
                long itemId = Long.parseLong(req.params(":id"));
                ItemDto response = customerService.displayItem(itemId);
                return gson.toJson(response);
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        get("/coupons", (req, res) ->{
            try{
                res.type("application/json");
                Map<String, String> bodyMap = gson.fromJson(req.body(), new TypeToken<Map<String, String>>(){}.getType());
                String couponCode = bodyMap.get("coupon_code");
                CouponDto response = customerService.getCoupon(couponCode);
                return gson.toJson(Map.of("Coupon details", response));
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        post("/orders", (req, res) ->{
            try{
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                OrderRegistrationRequest request = gson.fromJson(req.body(), OrderRegistrationRequest.class);
                authorizeUserAsCustomer(Integer.parseInt(userId));
                validateRestaurant(request.getVendor_id());
                validateCouponId(request.getCoupon_id());
                OrderDto response = customerService.addOrder(request, Integer.parseInt(userId));
                return gson.toJson(response);
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        get("/orders/:id", (req, res) ->{
            try{
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUserAsCustomer(Integer.parseInt(userId));
                long orderId = Long.parseLong(req.params(":id"));
                OrderDto response = customerService.getOrder(orderId);
                return gson.toJson(response);
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });


    }
}
