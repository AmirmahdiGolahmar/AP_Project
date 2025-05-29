package service;
import dto.UserProfileUpdateRequest;
import dto.UserRegistrationRequest;
import entity.*;
import java.util.List;

import exception.auth.ForbiddenException;
import exception.common.AlreadyExistsException;
import exception.common.NotFoundException;
import exception.user.InvalidCredentialsException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import validator.*;
import dao.*;
import entity.User;

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

    public Customer isCustomer(long id) {
        User user = userDao.findById(id);
        if (user == null)
            throw new NotFoundException("User not found");

        if (user.getRole() != UserRole.CUSTOMER)
            throw new NotFoundException("Customer not found");

        Customer customer = customerDao.findById(id);
        if (customer == null)
            throw new NotFoundException("Customer not found");

        return customer;
    }

    public User createUser(UserRegistrationRequest request) {

        UserValidator.validateUser(request);

        Bank_info bank_info = new Bank_info(request.getBank_name(), request.getAccount_number());
        UserRole userRole;
        switch (request.getRole().toLowerCase()) {
            case "buyer":
            case "customer":
                userRole = UserRole.CUSTOMER;
                break;
            case "seller":
                userRole = UserRole.SELLER;
                break;
            default:
                userRole = UserRole.DELIVERY;
        }

        if (userRole == UserRole.CUSTOMER) {
            Customer customer = new Customer();
            fillUserFields(customer, request);
            saveWithDuplicationCheck(customerDao, customer);
            return customer;

        } else if (userRole == UserRole.SELLER) {
            Seller seller = new Seller();
            fillUserFields(seller, request);
            saveWithDuplicationCheck(sellerDao, seller);
            return seller;

        } else {
            Delivery delivery = new Delivery();
            fillUserFields(delivery, request);
            saveWithDuplicationCheck(deliveryDao, delivery);
            return delivery;
        }
    }


    public List<Customer> findAllCustomers() { return customerDao.findAll(); }

    public Seller isSeller(Long id) {
        User user = userDao.findById(id);
        if (user == null)
            throw new NotFoundException("User not found");

        if (user.getRole() != UserRole.SELLER)
            throw new ForbiddenException("Not allowed");

        Seller seller = sellerDao.findById(id);
        if (seller == null)
            throw new NotFoundException("Seller not found");

        return seller;
    }

    public List<Seller> findAllSellers() { return sellerDao.findAll(); }

    public Delivery isDelivery(long id) {
        User user = userDao.findById(id);
        if (user == null)
            throw new NotFoundException("User not found");

        if (user.getRole() != UserRole.DELIVERY)
            throw new NotFoundException("Delivery not found");

        Delivery delivery = deliveryDao.findById(id);
        if (delivery == null)
            throw new NotFoundException("Delivery not found");

        return delivery;
    }

    public List<Delivery> findAllDeliveries() { return deliveryDao.findAll(); }

    public List<User> findAllUsers() {
        List<User> allUsers = new java.util.ArrayList<>();
        allUsers.addAll(customerDao.findAll());
        allUsers.addAll(sellerDao.findAll());
        allUsers.addAll(deliveryDao.findAll());
        return allUsers;
    }

    public void deleteUser(Long id) {
        User user = findUserById(id);
        if (user instanceof Customer) {
            customerDao.delete(id);
        } else if (user instanceof Seller) {
            sellerDao.delete(id);
        } else if (user instanceof Delivery) {
            deliveryDao.delete(id);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    public User findUserById(Long id) {
//        User user = customerDao.findById(id);
//        if (user != null) return user;
//
//        user = sellerDao.findById(id);
//        if (user != null) return user;
//
//        user = deliveryDao.findById(id);
//        if (user != null) return user;

       User user = userDao.findById(id);
        if (user != null) return user;

        return null;
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

        if (user == null) throw new NotFoundException(mobile);

        if (!user.getPassword().equals(password))
            throw new InvalidCredentialsException("Wrong password");

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
        user.setPassword(request.getPassword());
        user.setFull_name(request.getFull_name());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPhoto(request.getProfileImageBase64());
        user.setBank_info(new Bank_info(request.getBank_name(), request.getAccount_number()));
        switch (request.getRole().toLowerCase()) {
            case "buyer":
            case "customer":
                user.setRole(UserRole.CUSTOMER);
                break;
            case "seller":
                user.setRole(UserRole.SELLER);
                break;
            default:
                user.setRole(UserRole.DELIVERY);
        }
    }

    public void updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userDao.findById(userId);

        if (request.getFull_name() != null) {
            user.setFull_name(request.getFull_name());
        }
        if (request.getPhone() != null) {
            user.setMobile(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getProfileImageBase64() != null) {
            user.setPhoto(request.getProfileImageBase64());
        }
        if(request.getBank_name() != null) {
            user.setBank_name(request.getBank_name());
        }
        if (request.getAccount_number() != null) {
            user.setAccount_number(request.getAccount_number());
        }

        userDao.update(user);
    }
}

