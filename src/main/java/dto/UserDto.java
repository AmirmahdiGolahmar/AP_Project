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
    bankInfoDto bank_info = new bankInfoDto();

    public UserDto(User user) {
        this.id = user.getId().intValue();
        if(user.getFullName() != null) this.full_name = user.getFullName();
        if(user.getEmail() != null) this.email = user.getEmail();
        if(user.getMobile() != null) this.phone = user.getMobile();
        if(user.getRole() != null) this.role = user.getRole().toString();
        if(user.getAddress() != null) this.address = user.getAddress();
        if(user.getPhoto() != null) this.profileImageBase64 = user.getPhoto();
        if(user.getBankInfo() != null) {
            this.bank_info = new bankInfoDto();
            if(user.getBankInfo().getBankName() != null)
                this.bank_info.setBank_name(user.getBankInfo().getBankName());
            if(user.getBankInfo().getAccountNumber() != null)
                this.bank_info.setAccount_number(user.getBankInfo().getAccountNumber());
        }
    }
}
