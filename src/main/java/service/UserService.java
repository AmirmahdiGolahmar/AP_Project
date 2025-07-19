package service;
import dto.*;
import entity.*;
import java.util.List;
import exception.*;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import util.validator.UserValidator;
import dto.UserRegistrationRequest;
import dao.*;
import util.validator.validator.*;

import static util.validator.validator.*;

public class UserService {
    private final CustomerDao customerDao;
    private final SellerDao sellerDao;
    private final DeliveryDao deliveryDao;
    private final UserDao userDao;

    public UserService() {
        this.customerDao = new CustomerDao();
        this.sellerDao = new SellerDao();
        this.deliveryDao = new DeliveryDao();
        this.userDao = new UserDao();
    }

    public User createUser(UserRegistrationRequest request) {

        UserValidator.validateUserRegistrationRequest(request);

        UserRole userRole = switch (request.getRole().toLowerCase()) {
            case "buyer" -> UserRole.CUSTOMER;
            case "customer" -> UserRole.CUSTOMER;
            case "seller" -> UserRole.SELLER;
            default -> UserRole.DELIVERY;
        };

        if(userRole != UserRole.CUSTOMER &&
                (request.getBank_info() == null || request.getBank_info().getAccount_number() == null ||
                        request.getBank_info().getAccount_number().isBlank() ||
                        request.getBank_info().getBank_name() == null) ||
                        request.getBank_info().getBank_name().isBlank()
        ) throw new InvalidInputException(userRole.toString() + " must register bank name and account number");

        if (userRole == UserRole.CUSTOMER) {
            Customer customer = new Customer(request.getMobile());
            fillUserFields(customer, request);
            saveWithDuplicationCheck(customerDao, customer);
            return customer;
        } else if (userRole == UserRole.SELLER) {
            Seller seller = new Seller(request.getMobile());
            fillUserFields(seller, request);
            saveWithDuplicationCheck(sellerDao, seller);
            return seller;

        } else {
            Delivery delivery = new Delivery(request.getMobile());
            fillUserFields(delivery, request);
            saveWithDuplicationCheck(deliveryDao, delivery);
            return delivery;
        }
    }

    public User findUserById(Long id) {

       User user = userDao.findById(id);
        if (user == null) throw new NotFoundException("This user doesn't exist");
        return user;
    }

    public User login(String mobile, String password) {
        User user = null;

        try {
            user = customerDao.findByMobile(mobile);
        } catch (NoResultException ignored) {}

        if (user == null) {
            try {
                user = sellerDao.findByMobile(mobile);
            } catch (NoResultException ignored) {}
        }

        if (user == null) {
            try {
                user = deliveryDao.findByMobile(mobile);
            } catch (NoResultException ignored) {}
        }

        if (user == null) throw new UserNotFoundException(mobile);

        if (!user.getPassword().equals(password))
            throw new InvalidCredentialsException();

        return user;
    }

    private <T> void saveWithDuplicationCheck(GenericDao<T> dao, T entity) {
        try {
            dao.save(entity);
        } catch (ConstraintViolationException e) {
            throw new AlreadyExistsException("Phone number already exists");
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new AlreadyExistsException("Phone number already exists");
            }
            throw e;
        }
    }

    private void fillUserFields(User user, UserRegistrationRequest request) {
        UserRole userRole = switch (request.getRole().toLowerCase()) {
            case "buyer" -> UserRole.CUSTOMER;
            case "customer" -> UserRole.CUSTOMER;
            case "seller" -> UserRole.SELLER;
            default -> UserRole.DELIVERY;
        };
        user.setRole(userRole);
        user.setPassword(request.getPassword());
        user.setFullName(request.getFull_name());
        user.setMobile(request.getMobile());
        if(request.getEmail() != null)  user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        if(request.getProfileImageBase64() != null) user.setPhoto(request.getProfileImageBase64());
        if(user.getBankInfo() != null){
            if(user.getBankInfo().getBankName() != null) user.setBankName(request.getBank_info().getBank_name());
            if(user.getBankInfo().getAccountNumber() != null) user.setAccountNumber(request.getBank_info().getAccount_number());
        }
    }

    public void updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userDao.findById(userId);

        if (request.getFull_name() != null) {
            fullNameValidator(request.getFull_name());
            user.setFullName(request.getFull_name());
        }
        if (request.getPhone() != null) {
            mobileValidator(request.getPhone());
            user.setMobile(request.getPhone());
        }
        if (request.getEmail() != null) {
            emailValidator(request.getEmail());
            user.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            addressValidator(request.getAddress());
            user.setAddress(request.getAddress());
        }
        if (request.getProfileImageBase64() != null) {
            user.setPhoto(request.getProfileImageBase64());
        }
        if(request.getBank_info() != null){
            if(request.getBank_info().getBank_name() != null) {
                user.setBankName(request.getBank_info().getBank_name());
            }
            if (request.getBank_info().getAccount_number() != null) {
                user.setAccountNumber(request.getBank_info().getAccount_number());
            }
        }

        userDao.update(user);
    }
}

