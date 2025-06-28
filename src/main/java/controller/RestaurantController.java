package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import service.RestaurantService;
import service.ItemService;
import service.UserService;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.BankInfo;
import io.jsonwebtoken.Claims;
import service.UserService;
import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static validator.SellerValidator.validateSellerAndRestaurant;

import exception.*;

import dao.*;
import entity.*;
import util.JwtUtil;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;
import java.util.Map;

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
                    return gson.toJson(response);
                } catch (InvalidInputException iie) {
                    res.status(400);
                    return gson.toJson(Map.of("error", "Invalid input"));

                } catch (UnauthorizedUserException uue) {
                    res.status(401);
                    return gson.toJson(Map.of("error", "Unauthorized"));

                } catch (ForbiddenException fe) {
                    res.status(403);
                    return gson.toJson(Map.of("error", "Forbidden"));

                } catch (NotFoundException nfe) {
                    res.status(404);
                    return gson.toJson(Map.of("error", "Not found"));

                } catch (AlreadyExistsException aee) {
                    res.status(409);
                    return gson.toJson(Map.of("error", "Phone number already exists"));

                } catch (UnsupportedMediaTypeException umte) {
                    res.status(415);
                    return gson.toJson(Map.of("error", "Unsupported Media Type"));

                } catch(TooManyRequestsException tmre) {
                    res.status(429);
                    return gson.toJson(Map.of("error", "Too Many Requests"));

                } catch (Exception e) {
                    res.status(500);
                    e.printStackTrace();
                    return gson.toJson(Map.of("error", "Internal server error"));
                } finally {
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
                return gson.toJson(restaurantService.findRestaurantsByISellerId(seller.getId()));
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
                } catch (SellerNotFoundException | NotFoundException e) {
                    res.status(404);
                    return gson.toJson(Map.of("error", e.getMessage()));
                }
                catch (AccessDeniedException e) {
                    res.status(401);
                    return gson.toJson(Map.of("error", e.getMessage()));
                } catch (IllegalArgumentException e) {
                    res.status(400);
                    return gson.toJson(Map.of("error", e.getMessage()));
                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(Map.of("error", "Internal server error"));
                }
            });

            post("/:id/item", (req, res) -> {
                res.type("application/json");

                String userId = authorizeAndExtractUserId(req, res, gson);
                Long restaurantId = Long.parseLong(req.params(":id"));

                validateSellerAndRestaurant(userId, restaurantId);

                itemDto updateRequest = gson.fromJson(req.body(), itemDto.class);

                try {
                    itemDto addedItem = itemService.addItemToRestaurant(restaurantId, updateRequest);
                    res.status(200);
                    return gson.toJson(addedItem);
                } catch (SellerNotFoundException | NotFoundException e) {
                    res.status(404);
                    return gson.toJson(Map.of("error", e.getMessage()));
                }
                catch (AccessDeniedException e) {
                    res.status(401);
                    return gson.toJson(Map.of("error", e.getMessage()));
                } catch (IllegalArgumentException e) {
                    res.status(400);
                    return gson.toJson(Map.of("error", e.getMessage()));
                } catch (Exception e) {
                    res.status(500);
                    e.printStackTrace();
                    return gson.toJson(Map.of("error", "Internal server error"));
                }
            });

            put("/:id/item/:item_id", (req, res) -> {
                res.type("application/json");

                String userId = authorizeAndExtractUserId(req, res, gson);
                Long restaurantId = Long.parseLong(req.params(":id"));
                Long itemId = Long.parseLong(req.params(":item_id"));

                try {
                    itemDto editRequest = gson.fromJson(req.body(), itemDto.class);
                    itemDto updatedItemDto = itemService.editItem(restaurantId, itemId, editRequest, Long.parseLong(userId));
                    res.status(200);
                    return gson.toJson(Map.of(
                            "message", "Item updated successfully",
                            "item", updatedItemDto
                    ));
                } catch (NotFoundException e) {
                    res.status(404);
                    return gson.toJson(Map.of("error", "Restaurant or item not found"));
                } catch (ForbiddenException e) {
                    res.status(403);
                    return gson.toJson(Map.of("error", "You are not allowed to edit this item"));
                } catch (InvalidInputException e) {
                    res.status(400);
                    return gson.toJson(Map.of("error", "Invalid input"));
                } catch (UnauthorizedUserException e) {
                    res.status(401);
                    return gson.toJson(Map.of("error", "Unauthorized"));
                }
            });

        });
    }
}
