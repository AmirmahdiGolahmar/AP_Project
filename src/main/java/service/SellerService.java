package service;

import dao.SellerDao;
import entity.*;

import java.util.List;

import jakarta.persistence.EntityManager;

public class SellerService {
    private final SellerDao sellerDao;

    public SellerService() {
        this.sellerDao = new SellerDao();
    }

    public void createSeller(String username, String password, String firstName, String lastName,
                               String mobile, String email, String address, String photo, BankInfo bankInfo,
                               String restaurantDescription) {

        // Account
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(UserRole.SELLER);

        // SellerProfile
        SellerProfile profile = new SellerProfile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setMobile(mobile);
        profile.setEmail(email);
        profile.setAddress(address);
        profile.setPhoto(photo);
        profile.setBankInfo(bankInfo);
        profile.setRestaurantDescription(restaurantDescription);

        // Seller
        Seller seller = new Seller();
        seller.setAccount(account);
        seller.setProfile(profile);

        sellerDao.save(seller);
    }

    public Seller getById(Long id) {
        return sellerDao.findById(id);
    }

    public void delete(Long id) {
        sellerDao.delete(id);
    }

    public void update(Seller seller) {
        sellerDao.update(seller);
    }

    public List<Seller> getAll() {
        return sellerDao.findAll();
    }
}
