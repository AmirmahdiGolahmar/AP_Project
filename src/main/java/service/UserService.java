package service;

import dao.CustomerDao;
import dao.DeliveryDao;
import dao.SellerDao;
import entity.*;
import java.util.List;
import exception.*;
import jakarta.persistence.NoResultException;

public class UserService {
    private final CustomerDao customerDao;
    private final SellerDao sellerDao;
    private final DeliveryDao deliveryDao;

    public UserService() {
        this.customerDao = new CustomerDao();
        this.sellerDao = new SellerDao();
        this.deliveryDao = new DeliveryDao();
    }

    public <T extends User> void createUser(Class<T> userType,
                                         String password,
                                         String firstName, String lastName,
                                         String mobile, String email,
                                         String address, String photo,
                                         String bankName, String accountNumber,
                                         String restaurantDescription, UserRole role) {

        try {
            T user = userType.getDeclaredConstructor().newInstance();

            BankInfo bankInfo = new BankInfo(bankName, accountNumber);

            user.setPassword(password);
            user.setRole(role);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMobile(mobile);
            user.setEmail(email);
            user.setAddress(address);
            user.setPhoto(photo);
            user.setBankInfo(bankInfo);

            if (user.getRole() == UserRole.CUSTOMER) {
                Customer customer = (Customer) user;
                customerDao.save(customer);
            } else if (user.getRole() == UserRole.DELIVERY) {
                Delivery delivery = (Delivery) user;
                deliveryDao.save(delivery);
            }else if (user.getRole() == UserRole.SELLER) {
                Seller seller = (Seller) user;
                sellerDao.save(seller);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }


    public void updateCustomer(Customer customer) {
        customerDao.update(customer);
    }

    public void deleteCustomer(Long customerId) {
        customerDao.delete(customerId);
    }

    public Customer findCustomerById(Long id) {
        return customerDao.findById(id);
    }

    public List<Customer> findAllCustomers() { return customerDao.findAll(); }


    public void updateSeller(Seller seller) {
        sellerDao.update(seller);
    }

    public void deleteSeller(Long sellerId) {
        sellerDao.delete(sellerId);
    }

    public Seller findSellerById(Long id) {
        return sellerDao.findById(id);
    }

    public List<Seller> findAllSellers() { return sellerDao.findAll(); }

    public void updateDelivery(Delivery delivery) {
        deliveryDao.update(delivery);
    }

    public void deleteDelivery(Long deliveryId) {
        deliveryDao.delete(deliveryId);
    }

    public Delivery findDeliveryById(Long id) {
        return deliveryDao.findById(id);
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
        User user = customerDao.findById(id);
        if (user != null) return user;

        user = sellerDao.findById(id);
        if (user != null) return user;

        user = deliveryDao.findById(id);
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

        if (user == null) throw new UserNotFoundException(mobile);

        if (!user.getPassword().equals(password))
            throw new InvalidCredentialsException();

        return user;
    }


}

