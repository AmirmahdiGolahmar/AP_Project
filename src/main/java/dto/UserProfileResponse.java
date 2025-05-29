package dto;

import entity.Bank_info;
import entity.Bank_info;
import entity.User;
import entity.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.full_name = user.getFull_name();
        this.phone = user.getPhoto();
        this.email = user.getEmail();
        this.role =  user.getRole();
        this.address = user.getAddress();
        this.profileImageBase64 = user.getPhoto();
        this.bank_info = user.getBank_info();
    }

    public UserProfileResponse() {}

    private Long id;
    private String full_name;
    private String phone;
    private String email;
    private UserRole role;
    private String address;
    private String profileImageBase64;
    private Bank_info bank_info;

}
