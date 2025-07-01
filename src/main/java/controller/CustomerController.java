package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import service.CustomerService;
import service.ItemService;
import service.RestaurantService;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.List;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static validator.SellerValidator.validateRestaurant;

public class CustomerController {
    private static final RestaurantService restaurantService = new RestaurantService();
    private static final ItemService itemService = new ItemService();
    private static final CustomerService customerService = new CustomerService();
    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
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

        get("/vendors/:id/items", (req, res) ->{
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
    }
}
