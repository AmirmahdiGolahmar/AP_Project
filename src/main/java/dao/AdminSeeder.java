package dao;

import entity.User;
import entity.UserRole;
import util.PasswordHasher;

public class AdminSeeder {

    private final UserDao userDao;

    public AdminSeeder(UserDao userDao) {
        this.userDao = userDao;
    }

    public void seedAdmin() {
        String adminMobile = "09125350828";
        String plainPassword = "admin";

        User existing = userDao.findByMobile(adminMobile);
        if (existing == null) {
            User admin = new User();
            admin.setMobile(adminMobile);
            admin.setPassword(PasswordHasher.hash(plainPassword));
            admin.setRole(UserRole.ADMIN);

            userDao.save(admin);
        }
    }
}
