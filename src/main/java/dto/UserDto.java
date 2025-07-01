package dto;

import entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    int id;
    String full_name;
    String phone;
    String email;
    String role;
    String address;
    String profileImageBase64;
    bankInfoDto bank_info;

    public UserDto(User user) {
        this.id = user.getId().intValue();
        this.full_name = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getMobile();
        this.role = user.getRole().toString();
        this.address = user.getAddress();
        this.profileImageBase64 = user.getPhoto();
        this.bank_info = new bankInfoDto(user.getBankInfo());
    }
}
