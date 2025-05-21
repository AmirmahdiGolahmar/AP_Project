package org.example;
import java.util.logging.LogManager;
import static spark.Spark.*;

import controller.UserController;
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
        LogManager.getLogManager().reset();

        port(4567);
        UserController.initRoutes();
    }
}
