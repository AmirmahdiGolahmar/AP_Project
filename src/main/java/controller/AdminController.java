package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CouponDto;
import dto.CouponRequest;
import entity.Coupon;
import service.*;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;

public class AdminController {
    private static final AdminService adminService = new AdminService();

    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void initRoutes(){

        get("/admin/coupons", (req, res) ->{
            try{
                res.type("application/json");
                List<CouponDto> coupons = adminService.getAllCoupons();
                return gson.toJson(Map.of("List of all coupons",  coupons));
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        post("/admin/coupons", (req, res) ->{
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

        get("/admin/coupons/:id", (req, res) ->{
            try{
                res.type("application/json");
                Long id = Long.parseLong(req.params(":id"));
                CouponDto coupon = adminService.getCoupon(id);
                return gson.toJson(Map.of("coupon details", coupon));
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        put("/admin/coupons/:id", (req, res) ->{
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

        delete("/admin/coupons/:id", (req, res) ->{
            try{
                res.type("application/json");
                Long id = Long.parseLong(req.params(":id"));
                adminService.deleteCoupon(id);
                return gson.toJson(Map.of("message", "Coupon deleted"));
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });


    }
}
