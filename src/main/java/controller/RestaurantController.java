package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import service.RestaurantService;
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
    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void initRoutes(){
        path("/restaurant", () -> {
            post("", (req, res) -> {

                try{
                    String userId = authorizeAndExtractUserId(req, res, gson);
                    User seller = new UserDao().findById(Long.parseLong(userId));

                    if (seller == null || seller.getRole() != UserRole.SELLER) {
                        res.status(403); // Forbidden
                        return gson.toJson(Map.of("error", "Only sellers can create restaurants"));
                    }

                    // 4. ساختن رستوران
                    RestaurantRegistrationRequest request = gson.fromJson(req.body(), RestaurantRegistrationRequest.class);
                    restaurantService.createRestaurant(request, seller); // seller رو به متد پاس بده

                    res.status(201);
                    return gson.toJson(Map.of("message", "Restaurant created successfully"));
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
                User seller = new UserDao().findById(Long.parseLong(userId)); // فرض بر اینه که متدش وجود داره

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
                    RestaurantResponse updatedRestaurant =
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

        });
    }
}
