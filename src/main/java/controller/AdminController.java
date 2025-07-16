package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dto.*;
import entity.Coupon;
import service.*;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;

public class AdminController {
    private static final AdminService adminService = new AdminService();

    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void initRoutes(){

        path("/admin",() -> {

            get("/users", (req, res) -> {
                try{
                    res.type("application/json");
                    List<UserDto> response = adminService.getAllUsers();
                    return gson.toJson(Map.of("List of users",  response));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            patch("/users/:id/status", (req, res) -> {
                try{
                    res.type("application/json");
                    Long userId = (long)Integer.parseInt(req.params(":id"));

                    Map<String, String> bodyMap = gson.fromJson(req.body(), new TypeToken<Map<String, String>>(){}.getType());
                    String status = bodyMap.get("status");

                    adminService.changeUserStatus(userId, status);
                    return gson.toJson("Status updated");
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });
            
            get("/orders", (req, res) -> {
            try {
                res.type("application/json");

                String search = req.queryParams("search");
                String vendor = req.queryParams("vendor");
                String courier = req.queryParams("courier");
                String customer = req.queryParams("customer");
                String status = req.queryParams("status");

                List<OrderDto> response = adminService.searchOrders(search, vendor, courier, customer, status);

                return gson.toJson(Map.of("List of orders", response));
            } catch (Exception e) {
                return expHandler(e, res, gson);
            }
            });

            get("/transactions", (req, res) -> {
                try {
                    res.type("application/json");

                    String search = req.queryParams("search");
                    String user = req.queryParams("user");
                    String method = req.queryParams("method");
                    String status = req.queryParams("status");

                    List<PaymentReceiptDto> response = adminService.searchTransaction(search, user, method, status);

                    return gson.toJson(Map.of("List of financial transactions", response));
                } catch (Exception e) {
                    return expHandler(e, res, gson);
                }
            });

            get("/coupons", (req, res) ->{
                try{
                    res.type("application/json");
                    List<CouponDto> coupons = adminService.getAllCoupons();
                    return gson.toJson(Map.of("List of all coupons",  coupons));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            post("/coupons", (req, res) ->{
                try{
                    res.type("application/json");
                    CouponRequest request = gson.fromJson(req.body(), CouponRequest.class);
                    CouponDto response= adminService.addCoupon(request);
                    return gson.toJson(Map.of("message", "coupon added successfully",
                            "coupon", response));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            get("/coupons/:id", (req, res) ->{
                try{
                    res.type("application/json");
                    Long id = Long.parseLong(req.params(":id"));
                    CouponDto coupon = adminService.getCoupon(id);
                    return gson.toJson(Map.of("coupon details", coupon));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            put("/coupons/:id", (req, res) ->{
                try{
                    res.type("application/json");
                    Long id = Long.parseLong(req.params(":id"));
                    CouponRequest request = gson.fromJson(req.body(), CouponRequest.class);
                    CouponDto coupon = adminService.updateCoupon(request, id);
                    return gson.toJson(Map.of("message", "Coupon updated successfully",
                            "coupon", coupon));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            delete("/coupons/:id", (req, res) ->{
                try{
                    res.type("application/json");
                    Long id = Long.parseLong(req.params(":id"));
                    adminService.deleteCoupon(id);
                    return gson.toJson(Map.of("message", "Coupon deleted"));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

        });
    }
}
