package service;

import dao.DeliveryDao;
import entity.*;

import java.util.List;
import jakarta.persistence.EntityManager;

public class DeliveryService {
    private final DeliveryDao deliveryDao;

    public DeliveryService() {
        this.deliveryDao = new DeliveryDao();
    }

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

    public Delivery getById(Long id) {
        return deliveryDao.findById(id);
    }

    public void delete(Long id) {
        deliveryDao.delete(id);
    }

    public void update(Delivery delivery) {
        deliveryDao.update(delivery);
    }

    public List<Delivery> getAll() {
        return deliveryDao.findAll();
    }
}
