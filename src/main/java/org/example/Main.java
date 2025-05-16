package org.example;
import java.util.logging.LogManager;
import static spark.Spark.*;

import entity.Customer;
import entity.User;
import entity.BankInfo;
import com.google.gson.Gson;
import java.util.List;

import static spark.Spark.*;
import com.google.gson.Gson;
import service.*;

import java.util.List;
import java.util.logging.LogManager;

import static spark.Spark.*;

import com.google.gson.Gson;
import entity.*;
import service.*;

import java.util.List;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        LogManager.getLogManager().reset(); // disable logging spam from Hibernate

        // Initialize service classes
        CustomerService customerService = new CustomerService();
        SellerService sellerService = new SellerService();
        DeliveryService deliveryService = new DeliveryService();
        AdminService adminService = new AdminService();

        port(4567);
        Gson gson = new Gson();

        // Root route
        get("/", (req, res) -> "User microservice (Hibernate + Spark) is running!");

        // Add Customer
        post("/addCustomer", (req, res) -> {
            BankInfo bankInfo = new BankInfo(
                    req.queryParams("bankName"),
                    req.queryParams("accountNumber"),
                    req.queryParams("accountHolder")
            );

            customerService.createCustomer(
                    req.queryParams("username"),
                    req.queryParams("password"),
                    req.queryParams("firstName"),
                    req.queryParams("lastName"),
                    req.queryParams("mobile"),
                    req.queryParams("email"),
                    req.queryParams("address"),
                    req.queryParams("photo"),
                    bankInfo
            );

            return "Customer added.";
        });

        // Add Seller
        post("/addSeller", (req, res) -> {
            BankInfo bankInfo = new BankInfo(
                    req.queryParams("bankName"),
                    req.queryParams("accountNumber"),
                    req.queryParams("accountHolder")
            );

            sellerService.createSeller(
                    req.queryParams("username"),
                    req.queryParams("password"),
                    req.queryParams("firstName"),
                    req.queryParams("lastName"),
                    req.queryParams("mobile"),
                    req.queryParams("email"),
                    req.queryParams("address"),
                    req.queryParams("photo"),
                    bankInfo,
                    req.queryParams("restaurantDescription")
            );

            return "Seller added.";
        });

        // Remove Customer
        delete("/removeCustomer/:id", (req, res) -> {
            Long id = Long.parseLong(req.params("id"));
            customerService.delete(id);
            return "Customer removed with ID: " + id;
        });

        // Get All Customers
        get("/customers", (req, res) -> {
            List<Customer> customers = customerService.getAll();
            res.type("application/json");
            return gson.toJson(customers);
        });

        // Remove Seller
        delete("/removeSeller/:id", (req, res) -> {
            Long id = Long.parseLong(req.params("id"));
            sellerService.delete(id);
            return "Seller removed with ID: " + id;
        });

        // Get All Sellers
        get("/sellers", (req, res) -> {
            List<Seller> sellers = sellerService.getAll();
            res.type("application/json");
            return gson.toJson(sellers);
        });
    }
}
