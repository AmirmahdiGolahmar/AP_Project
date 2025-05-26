package validator;


import dto.LoginRequest;
import entity.User;
import exception.InvalidCredentialsException;
import exception.InvalidInputException;
import dto.UserRegistrationRequest;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.Map;

public class UserValidator {

    public static void validateUser(UserRegistrationRequest request) {
        if (request.getPassword() == null || request.getPassword().length() < 4) {
            throw new InvalidInputException("Invalid field password");
        }

        if (request.getFullName() == null || request.getFullName().isEmpty()) {
            throw new InvalidInputException("Invalid field full name");
        }

        if (request.getMobile() == null || request.getMobile().length() != 11
                || !request.getMobile().startsWith("09")) {
            throw new InvalidInputException("Invalid field mobile");
        }

        if (request.getEmail() == null || !request.getEmail().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidInputException("Invalid field email");
        }

        if (request.getRole() == null ||
                !(request.getRole().equalsIgnoreCase("customer") ||
                        request.getRole().equalsIgnoreCase("buyer") ||
                        request.getRole().equalsIgnoreCase("seller") ||
                        request.getRole().equalsIgnoreCase("delivery"))) {
            throw new InvalidInputException("Invalid field role");
        }
    }

    public static void validateLogin(LoginRequest request) {
        if (request.getMobile() == null || request.getPassword() == null) {
            throw new InvalidInputException("Phone and password must be provided");
        }
    }

    public static User authenticateUser(String mobile, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery(
                    "FROM User WHERE mobile = :mobile", User.class);
            query.setParameter("mobile", mobile);
            User user = (User) query.getSingleResultOrNull();

            if (user == null)
                throw new InvalidCredentialsException("User not found with mobile: " + mobile);
            if (!user.getPassword().equals(password)) {
                throw new InvalidCredentialsException("Invalid password");
            }
            return user;

        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate user", e);
        }
    }

}