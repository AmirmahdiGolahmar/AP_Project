//import dao.UserDao;
//import entity.Bank_info;
//import entity.User;
//import org.junit.jupiter.api.*;
//import service.UserService;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class UserServiceTest {
//
//    private static UserService userService;
//
//    @BeforeAll
//    public static void setup() {
//        userService = new UserService();
//    }
//
//    @Test
//    @Order(1)
//    public void testAddUser() {
//        Bank_info bank_info = new Bank_info("TestBank", "99999999", "John Tester");
//        User user = new User("John", "Tester", "09110000000", "john@test.com", "photo.jpg", "Test City", bank_info);
//
//        assertDoesNotThrow(() -> userService.createUser(user));
//    }
//
//    @Test
//    @Order(2)
//    public void testGetAllUsers() {
//        List<User> users = userService.getAllUsers();
//        assertNotNull(users);
//        assertTrue(users.size() > 0);
//    }
//
//    @Test
//    @Order(3)
//    public void testFindByMobileNumber() {
//        UserDao userDao = new UserDao();
//        User user = userDao.getByMobile("09110000000");
//        assertNotNull(user);
//        assertEquals("John", user.getFirstName());
//    }
//
//    @Test
//    @Order(4)
//    public void testDeleteUser() {
//        UserDao userDao = new UserDao();
//        User user = userDao.getByMobile("09110000000");
//        assertNotNull(user);
//
//        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
//    }
//}
