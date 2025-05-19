package controller;

import com.google.gson.Gson;
import entity.BankInfo;
import service.UserService;

import static spark.Spark.*;

import dao.*;
import entity.*;

public class UserController {
    private static final UserService userService = new UserService();
    private static final Gson gson = new Gson();

    public static void initRoutes() {
        path("/api/user", () -> {

            post("/customer", (req, res) -> {
                CustomerRequest body = gson.fromJson(req.body(), CustomerRequest.class);
                userService.createUser(
                        Customer.class,
                        body.username, body.password,
                        body.firstName, body.lastName,
                        body.mobile, body.email, body.address,
                        body.photo, body.bankName, body.accountNumber,
                        body.shebaNumber, body.accountHolder,null, UserRole.CUSTOMER
                );
                res.status(201);
                return "Customer created";
            });

            post("/seller", (req, res) -> {
                SellerRequest body = gson.fromJson(req.body(), SellerRequest.class);
                userService.createUser(
                        Seller.class,
                        body.username, body.password,
                        body.firstName, body.lastName,
                        body.mobile, body.email, body.address,
                        body.photo, body.bankName, body.accountNumber,
                        body.shebaNumber, body.accountHolder,body.restaurantDescription, UserRole.SELLER
                );
                res.status(201);
                return "Seller created";
            });

            post("/delivery", (req, res) -> {
                DeliveryRequest body = gson.fromJson(req.body(), DeliveryRequest.class);
                userService.createUser(
                        Delivery.class,
                        body.username, body.password,
                        body.firstName, body.lastName,
                        body.mobile, body.email, body.address,
                        body.photo, body.bankName, body.accountNumber,
                        body.shebaNumber, body.accountHolder, null, UserRole.DELIVERY
                );
                res.status(201);
                return "Delivery agent created";
            });
        });

        get("/customers", (req, res) -> {
           res.type("application/json");
           return gson.toJson(userService.findAllCustomers());
        });

        get("/sellers", (req, res) -> {
            res.type("application/json");
            return gson.toJson(userService.findAllSellers());
        });

        get("/deliveries", (req, res) -> {
            res.type("application/json");
            return gson.toJson(userService.findAllDeliveries());
        });

        get("/all", (req, res) -> {
            res.type("application/json");
            return gson.toJson(userService.findAllUsers());
        });

        delete("/:id", (req, res) -> {
            Long id = Long.parseLong(req.params(":id"));
            userService.deleteUser(id);
            return "User deleted with id: " + id;
        });
    };

    // Inner classes for request bodies
    static class CustomerRequest {
        String username, password, firstName, lastName;
        String mobile, email, address, photo;
        String bankName, accountNumber, shebaNumber, accountHolder;
    }

    static class SellerRequest extends CustomerRequest {
        String restaurantDescription;
    }

    static class DeliveryRequest extends CustomerRequest {
    }
}

