package service;

import dao.CustomerDao;
import dao.DeliveryDao;
import dao.SellerDao;
import entity.*;
import java.util.List;

public class UserService {
    private final CustomerDao customerDao;
    private final SellerDao sellerDao;
    private final DeliveryDao deliveryDao;

    public UserService() {
        this.customerDao = new CustomerDao();
        this.sellerDao = new SellerDao();
        this.deliveryDao = new DeliveryDao();
    }

    public void createCustomer(String username, String password, String firstName, String lastName,
                               String mobile, String email, String address, String photo, BankInfo bankInfo) {

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(UserRole.CUSTOMER);

        Profile profile = new Profile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setMobile(mobile);
        profile.setEmail(email);
        profile.setAddress(address);
        profile.setPhoto(photo);
        profile.setBankInfo(bankInfo);

        Customer customer = new Customer();
        customer.setAccount(account);
        customer.setProfile(profile);

        customerDao.save(customer);
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


    public void createSeller(String username, String password, String firstName, String lastName,
                             String mobile, String email, String address, String photo,
                             BankInfo bankInfo, String restaurantDescription) {

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(UserRole.SELLER);

        SellerProfile profile = new SellerProfile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setMobile(mobile);
        profile.setEmail(email);
        profile.setAddress(address);
        profile.setPhoto(photo);
        profile.setBankInfo(bankInfo);
        profile.setRestaurantDescription(restaurantDescription);

        Seller seller = new Seller();
        seller.setAccount(account);
        seller.setProfile(profile);

        sellerDao.save(seller);
    }

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


    public void createDelivery(String username, String password, String firstName, String lastName,
                               String mobile, String email, String address, String photo, BankInfo bankInfo) {

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(UserRole.DELIVERY);

        Profile profile = new Profile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setMobile(mobile);
        profile.setEmail(email);
        profile.setAddress(address);
        profile.setPhoto(photo);
        profile.setBankInfo(bankInfo);

        Delivery delivery = new Delivery();
        delivery.setAccount(account);
        delivery.setProfile(profile);

        deliveryDao.save(delivery);
    }

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



}
