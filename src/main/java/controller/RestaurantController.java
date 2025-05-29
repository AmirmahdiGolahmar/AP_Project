package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import exception.auth.ForbiddenException;
import exception.auth.UnauthorizedUserException;
import exception.common.*;
import exception.user.InvalidCredentialsException;
import service.RestaurantService;
import util.LocalDateTimeAdapter;

import java.time.LocalDateTime;

import static spark.Spark.*;
import static util.AuthorizationHandler.authorizeAndExtractUserId;
import static validator.SellerValidator.validateSellerAndRestaurant;

import entity.*;

import java.util.List;
import java.util.Map;

public class RestaurantController {

    private static final RestaurantService restaurantService = new RestaurantService();
    private static final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void initRoutes(){

        path("/restaurants", () -> {
            post("", (req, res) -> {
            res.type("application/json");

                try {

                    RestaurantRegistrationRequest restaurantRegistrationRequest = gson.fromJson(req.body(), RestaurantRegistrationRequest.class);
                    Restaurant restaurant = restaurantService.createRestaurant(req, restaurantRegistrationRequest);

                    RestaurantResponse restaurantResponse = new RestaurantResponse(restaurant);

                    res.status(201);
                    return gson.toJson(restaurantResponse);

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

                try {
                    List<Restaurant> sellerRestaurants = restaurantService.findRestaurantsBySellerId(req);

                    res.status(200);
                    return gson.toJson(sellerRestaurants);

                } catch (InvalidCredentialsException | InvalidInputException iie) {
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

            put("/:id", (req, res) -> {
                res.type("application/json");

                String userId = authorizeAndExtractUserId(req);
                Long restaurantId = Long.parseLong(req.params(":id"));

                validateSellerAndRestaurant(userId, restaurantId);

                RestaurantUpdateRequest updateRequest = gson.fromJson(req.body(), RestaurantUpdateRequest.class);

                try {
                    RestaurantResponse updatedRestaurant =
                            restaurantService.updateRestaurant(restaurantId, updateRequest);

                    res.status(200);
                    return gson.toJson(updatedRestaurant);
                } catch (InvalidInputException | IllegalArgumentException iie) {
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

            post("/:id/item", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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

            put("/:id/item/:itemId", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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

            delete("/:id/item/:itemId", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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

            post("/:id/menu", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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

            put("/:id/menu/:title", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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

            delete("/:id/menu/:title", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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

            delete("/:id/menu/:title/:itemId", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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

            get("/:id/orders", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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

            patch("/orders/:orderId", (req, res) -> {

                try {
                    res.status(200);
                    return gson.toJson(Map.of("message", "User registered successfully"));

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
        });
    }
}








