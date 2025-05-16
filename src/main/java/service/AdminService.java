package service;

import dao.AdminDao;
import entity.*;
import jakarta.persistence.EntityManager;
import java.util.List;

public class AdminService {
    private final AdminDao adminDao;

    public AdminService() {
        this.adminDao = new AdminDao();
    }

    public void createAdmin(String username, String password, String firstName, String lastName,
                              String mobile, String email, String address, String photo, BankInfo bankInfo) {

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole(UserRole.ADMIN);

        Profile profile = new Profile();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setMobile(mobile);
        profile.setEmail(email);
        profile.setAddress(address);
        profile.setPhoto(photo);
        profile.setBankInfo(bankInfo);

        Admin admin = new Admin();
        admin.setAccount(account);
        admin.setProfile(profile);

        adminDao.save(admin);
    }

    public Admin getById(Long id) {
        return adminDao.findById(id);
    }

    public void delete(Long id) {
        adminDao.delete(id);
    }

    public void update(Admin admin) {
        adminDao.update(admin);
    }

    public List<Admin> getAll() {
        return adminDao.findAll();
    }
}
