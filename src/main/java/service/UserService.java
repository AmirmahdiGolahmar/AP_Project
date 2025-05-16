//package service;
//
//import dao.UserDao;
//import entity.User;
//
//import java.util.List;
//
//public class UserService {
//    private final UserDao userDao = new UserDao();
//
//    public void createUser(User user) {
//        userDao.add(user);
//    }
//
//    public void deleteUser(Long id) {
//        userDao.remove(id);
//    }
//
//    public void updateUser(User user) {
//        userDao.update(user);
//    }
//
//    public List<User> getAllUsers() {
//        return userDao.getAllUsers();
//    }
//
//}
