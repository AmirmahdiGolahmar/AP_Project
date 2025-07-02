package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CouponRegistrationRequest;
import dto.RestaurantDisplayResponse;
import dto.RestaurantDto;
import dto.RestaurantSearchRequestDto;
import entity.Coupon;
import service.*;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.List;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.get;
import static spark.Spark.post;
import static validator.SellerValidator.validateRestaurant;

public class AdminController {
    private static final AdminService adminService = new AdminService();

    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void initRoutes(){

        get("/admin/coupons", (req, res) ->{
            try{
                res.type("application/json");
                List<Coupon> coupons = adminService.getAllCoupons();
                return gson.toJson(coupons);
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });

        post("/admin/coupons", (req, res) ->{
            try{
                res.type("application/json");
                CouponRegistrationRequest request = gson.fromJson(req.body(), CouponRegistrationRequest.class);
                Coupon response= adminService.addCoupon(request);
                return gson.toJson(response);
            }catch (Exception e){
                return expHandler(e, res, gson);
            }
        });
    }
}
