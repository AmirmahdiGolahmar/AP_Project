package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import service.RestaurantService;
import service.ItemService;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;

import static exception.ExceptionHandler.expHandler;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static validator.RestaurantValidator.itemValidator;
import static validator.SellerValidator.validateSellerAndRestaurant;

import exception.*;

import dao.*;
import entity.*;
import java.util.Map;
import exception.ExceptionHandler.*;

public class RestaurantController {

    private static final RestaurantService restaurantService = new RestaurantService();
    private static final ItemService itemService = new ItemService();
    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void initRoutes(){
        path("/restaurants", () -> {
            post("", (req, res) -> {
                try{
                    String userId = authorizeAndExtractUserId(req, res, gson);
                    User seller = new UserDao().findById(Long.parseLong(userId));

                    if (seller == null || seller.getRole() != UserRole.SELLER) {
                        res.status(403); // Forbidden
                        return gson.toJson(Map.of("error", "Only sellers can create restaurants"));
                    }

                    RestaurantRegistrationRequest request = gson.fromJson(req.body(), RestaurantRegistrationRequest.class);
                    Restaurant restaurant = restaurantService.createRestaurant(request, seller);

                    res.status(201);
                    restaurantDto response = new restaurantDto(
                            restaurant.getId(),
                            restaurant.getName(),
                            restaurant.getAddress(),
                            restaurant.getPhone(),
                            restaurant.getLogo(),
                            restaurant.getTaxFee()
                    );
                    return gson.toJson(Map.of("message", "Restaurant created successfully",
                                            "response", response));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            get("/mine", (req, res) -> {
                res.type("application/json");
                String userId = authorizeAndExtractUserId(req, res, gson);
                User seller = new UserDao().findById(Long.parseLong(userId));

                if (seller == null || seller.getRole() != UserRole.SELLER) {
                    res.status(403); // Forbidden
                    return gson.toJson(Map.of("error", "Only sellers can see restaurant"));
                }

                try{
                    return gson.toJson(restaurantService.findRestaurantsByISellerId(seller.getId()));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            put("/:id", (req, res) -> {
                res.type("application/json");

                String userId = authorizeAndExtractUserId(req, res, gson);
                Long restaurantId = Long.parseLong(req.params(":id"));

                validateSellerAndRestaurant(userId, restaurantId);

                RestaurantUpdateRequest updateRequest = gson.fromJson(req.body(), RestaurantUpdateRequest.class);

                try {
                    restaurantDto updatedRestaurant =
                            restaurantService.updateRestaurant(restaurantId, updateRequest);

                    res.status(200);
                    return gson.toJson(updatedRestaurant);
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            post("/:id/item", (req, res) -> {
                res.type("application/json");

                String userId = authorizeAndExtractUserId(req, res, gson);
                Long restaurantId = Long.parseLong(req.params(":id"));

                validateSellerAndRestaurant(userId, restaurantId);

                itemDto registrationRequest = gson.fromJson(req.body(), itemDto.class);
                itemValidator(registrationRequest);

                try {
                    itemDto addedItem = itemService.addItemToRestaurant(restaurantId, registrationRequest);
                    res.status(200);
                    return gson.toJson(addedItem);
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            put("/:id/item/:item_id", (req, res) -> {
                res.type("application/json");

                String userId = authorizeAndExtractUserId(req, res, gson);
                Long restaurantId = Long.parseLong(req.params(":id"));
                Long itemId = Long.parseLong(req.params(":item_id"));
                validateSellerAndRestaurant(userId, restaurantId);

                try {
                    itemDto editRequest = gson.fromJson(req.body(), itemDto.class);
                    itemDto updatedItemDto = itemService.editItem(restaurantId, itemId, editRequest, Long.parseLong(userId));
                    res.status(200);
                    return gson.toJson(Map.of(
                            "message", "Item updated successfully",
                            "item", updatedItemDto
                    ));
                }catch (Exception e){
                    return expHandler(e, res, gson);
                }
            });

            delete("/:id/item/:item_id", (req, res) -> {
                res.type("application/json");

                String userId = authorizeAndExtractUserId(req, res, gson);
                Long restaurantId = Long.parseLong(req.params(":id"));
                Long itemId = Long.parseLong(req.params(":item_id"));

                validateSellerAndRestaurant(userId, restaurantId);

                try {
                    itemService.deleteItem(restaurantId, itemId);
                    res.status(200);
                    return gson.toJson(Map.of("message", "Food item removed successfully"));
                } catch (Exception e) {
                    return expHandler(e, res, gson);
                }
            });

        });
    }
}