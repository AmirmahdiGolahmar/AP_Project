package org.example;
import java.util.logging.LogManager;
import static spark.Spark.*;

import controller.*;
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

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        LogManager.getLogManager().reset();

        System.out.println("Server is Running");

        // Set server port
        port(4567);

        // ✅ Log all incoming HTTP requests
        before((request, response) -> {
            System.out.println("\n===== New Request =====");
            System.out.println("Method: " + request.requestMethod());
            System.out.println("Path: " + request.pathInfo());
            System.out.println("Query: " + request.queryString());
            System.out.println("Body: " + request.body());
            System.out.println("========================\n");
        });

        // Init controllers
        UserController.initRoutes();
        RestaurantController.initRoutes();
        CustomerController.initRoutes();
        DeliveryController.initRoutes();
        AdminController.initRoutes();
        TransactionController.initRoutes();
    }
}


