package org.example;
import java.util.logging.LogManager;
import static spark.Spark.*;
import service.UserService;
import entity.User;
import entity.BankInfo;
import com.google.gson.Gson;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        LogManager.getLogManager().reset();

        UserService userService = new UserService();

        port(4567);

        post("/add", (req, res) -> {

            String bankName = req.queryParams("bankName");
            String accountNumber = req.queryParams("accountNumber");
            String accountHolder = req.queryParams("accountHolder");

            BankInfo bankInfo = null;
            if (bankName != null && accountNumber != null && accountHolder != null) {
                bankInfo = new BankInfo(bankName, accountNumber, accountHolder);

            }

            User user = new User(
                    req.queryParams("firstName"),
                    req.queryParams("lastName"),
                    req.queryParams("mobile"),
                    req.queryParams("email"),
                    req.queryParams("photo"),
                    req.queryParams("address"),
                    bankInfo
            );

            userService.createUser(user);
            return "User added: " + user.getFirstName() + " " + user.getLastName();
        });

        delete("/remove/:id", (req, res) -> {
            Long id = Long.parseLong(req.params(":id"));
            userService.deleteUser(id);
            return "User removed with id: " + id;
        });

        Gson gson = new Gson();
        get("/users", (req, res) -> {
            List<User> users = userService.getAllUsers();
            res.type("application/json");
            return gson.toJson(users);
        });



        get("/", (req, res) -> "User microservice (Hibernate + Spark) is running!");
    }
}
