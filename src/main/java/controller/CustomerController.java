package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import entity.UserRole;
import service.CustomerService;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static util.AuthorizationHandler.authorizeUser;
import static util.validator.RestaurantValidator.validateRestaurant;
import static util.validator.RestaurantValidator.validateCouponId;

public class CustomerController {
    private static final CustomerService customerService = new CustomerService();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();

    public static void initRoutes() {

        post("/vendors", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                RestaurantSearchRequestDto request = gson.fromJson(req.body(), RestaurantSearchRequestDto.class);
                List<RestaurantDto> response = customerService.searchRestaurant(request);
                res.status(200);
                return gson.toJson(response);
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        get("/vendors/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                long restaurantId = Long.parseLong(req.params(":id"));
                validateRestaurant(restaurantId);
                RestaurantDisplayResponse response = customerService.displayRestaurant(restaurantId);
                res.status(200);
                return gson.toJson(response);
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        post("/items", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                ItemSearchRequestDto request = gson.fromJson(req.body(), ItemSearchRequestDto.class);
                List<ItemDto> response = customerService.searchItem(request);
                res.status(200);
                return gson.toJson(response);
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        get("/items/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                long itemId = Long.parseLong(req.params(":id"));
                ItemDto response = customerService.displayItem(itemId);
                res.status(200);
                return gson.toJson(response);
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        get("/coupons", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                String couponCode = req.queryParams("coupon_code");
                CouponDto response = customerService.getCoupon(couponCode);
                res.status(200);
                return gson.toJson(Map.of("Coupon details", response));
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        post("/orders", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                OrderRegistrationRequest request = gson.fromJson(req.body(), OrderRegistrationRequest.class);
                validateRestaurant(request.getVendor_id());
                validateCouponId(request.getCoupon_id());
                OrderDto response = customerService.addOrder(request, Integer.parseInt(userId));
                res.status(200);
                return gson.toJson(response);
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        get("/orders/history", (req, res) -> {
            try {
                res.type("application/json");
                Long userId = (long) Integer.parseInt(authorizeAndExtractUserId(req, res, gson));
                authorizeUser(userId, UserRole.CUSTOMER);
                String search = req.queryParams("search");
                String vendor = req.queryParams("vendor");
                List<OrderDto> response = customerService.searchOrderHistory(search, vendor, userId);
                res.status(200);
                return gson.toJson(Map.of("List of past orders", response));
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        get("/orders/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                long orderId = Long.parseLong(req.params(":id"));
                OrderDto response = customerService.getOrder(orderId);
                res.status(200);
                return gson.toJson(response);
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        put("/favorites/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                long restaurantId = Long.parseLong(req.params(":id"));
                validateRestaurant(restaurantId);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                customerService.addToFavorites(Integer.parseInt(userId), restaurantId);
                res.status(200);
                return gson.toJson("Added to favorites");
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        delete("/favorites/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                long restaurantId = Long.parseLong(req.params(":id"));
                validateRestaurant(restaurantId);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                customerService.removeFromFavorites(Integer.parseInt(userId), restaurantId);
                res.status(200);
                return gson.toJson("Removed to favorites");
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        get("/favorites", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                List<RestaurantDto> response = customerService.getFavorites(Integer.parseInt(userId));
                res.status(200);
                return gson.toJson(Map.of("List of favorite restaurants", response));
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        post("/ratings", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                OrderRatingDto request =  gson.fromJson(req.body(), OrderRatingDto.class);
                customerService.submitOrderRating(request, (long)Integer.parseInt(userId));
                res.status(200);
                return gson.toJson("Rating submitted");
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        get("/ratings/items/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                Long itemId = (long) Integer.parseInt(req.params(":id"));
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                ItemRatingAvgResponseDto response = customerService.getItemAvgRating(itemId);
                res.status(200);
                return gson.toJson(Map.of("List of ratings and reviews", response));
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });
    
        get("/ratings/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                Long itemId = (long) Integer.parseInt(req.params(":id"));
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                ItemRatingResponseDto response = customerService.getItemRating(itemId);
                res.status(200);
                return gson.toJson(Map.of("Rating details", response));
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });
    
        delete("/ratings/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                Long itemId = (long) Integer.parseInt(req.params(":id"));
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                customerService.deleteRating(itemId);
                res.status(200);
                return gson.toJson("Rating deleted");
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });

        put("/ratings/:id", (req, res) -> {
            try {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                authorizeUser(Integer.parseInt(userId), UserRole.CUSTOMER);
                Long ratingId = (long) Integer.parseInt(req.params(":id"));
                ItemRatingRequestDto request =  gson.fromJson(req.body(), ItemRatingRequestDto.class);
                customerService.updateItemRating(request, (long)Integer.parseInt(userId), ratingId);
                res.status(200);
                return gson.toJson("Rating updated");
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
        });
    }

}
