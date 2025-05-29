package service;
import dto.LoginRequest;
import dto.UserRegistrationRequest;
import entity.*;
import java.util.List;
import exception.*;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import validator.*;
import dao.*;
import entity.User;


public class UserService {
    private final UserDao userDao;
    private final CustomerDao customerDao;
    private final SellerDao sellerDao;
    private final DeliveryDao deliveryDao;

    public UserService() {
        this.userDao = new UserDao();
        this.customerDao = new CustomerDao();
        this.sellerDao = new SellerDao();
        this.deliveryDao = new DeliveryDao();
    }

    public Customer isCustomer(long id) {
        User user = userDao.findById(id);
        if (user == null)
            throw new UserNotFoundException("User not found");

        if (user.getRole() != UserRole.CUSTOMER)
            throw new CustomerNotFoundException("Customer not found");

        Customer customer = customerDao.findById(id);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");

        return customer;
    }

    public User createUser(UserRegistrationRequest request) {

        UserValidator.validateUser(request);

        BankInfo bankInfo = new BankInfo(request.getBankName(), request.getAccountNumber());
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

    public Seller isSeller(Long id) {
        User user = userDao.findById(id);
        if (user == null)
            throw new UserNotFoundException("User not found");

        if (user.getRole() != UserRole.SELLER)
            throw new SellerNotFoundException("Seller not found");

        Seller seller = sellerDao.findById(id);
        if (seller == null)
            throw new SellerNotFoundException("Seller not found");

        return seller;
    }

    public void updateSeller(Seller seller) {
        sellerDao.update(seller);
    }

    public void deleteSeller(Long sellerId) {
        sellerDao.delete(sellerId);
    }

    public Seller findSellerById(Long id) {
        return (Seller) sellerDao.findById(id);
    }

    public List<Seller> findAllSellers() { return sellerDao.findAll(); }

    public Delivery isDelivery(long id) {
        User user = userDao.findById(id);
        if (user == null)
            throw new UserNotFoundException("User not found");

        if (user.getRole() != UserRole.DELIVERY)
            throw new DeliveryNotFoundException("Delivery not found");

        Delivery delivery = deliveryDao.findById(id);
        if (delivery == null)
            throw new CustomerNotFoundException("Delivery not found");

        return delivery;
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

    public User login(LoginRequest request) {
        UserValidator.validateLogin(request);
        return UserValidator.authenticateUser(request.getMobile(), request.getPassword());
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
        user.setFullName(request.getFullName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPhoto(request.getPhoto());
        user.setBankInfo(new BankInfo(request.getBankName(), request.getAccountNumber()));
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


}

