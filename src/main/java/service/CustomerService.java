package service;

import dao.CustomerDao;
import entity.*;

import java.util.List;

public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService() {
        this.customerDao = new CustomerDao();
    }

    public void createCustomer(String username, String password, String firstName, String lastName,
                                 String mobile, String email, String address, String photo, BankInfo bankInfo) {

        // Create account
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(UserRole.CUSTOMER);

        // Create profile
        Profile profile = new Profile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setMobile(mobile);
        profile.setEmail(email);
        profile.setAddress(address);
        profile.setPhoto(photo);
        profile.setBankInfo(bankInfo);

        // Combine in customer
        Customer customer = new Customer();
        customer.setAccount(account);
        customer.setProfile(profile);

        customerDao.save(customer);
    }

    public Customer getById(Long id) {
        return customerDao.findById(id);
    }

    public void delete(Long id) {
        customerDao.delete(id);
    }

    public void update(Customer customer) {
        customerDao.update(customer);
    }

    public List<Customer> getAll() {
        return customerDao.findAll();
    }
}
