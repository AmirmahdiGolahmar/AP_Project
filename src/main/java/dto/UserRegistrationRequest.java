package dto;

import entity.BankInfo;
import lombok.Getter;

@Getter
public class UserRegistrationRequest {
    String password;
    String full_name;
    String mobile, email, address, profileImageBase64;
    bankInfoDto bank_info;
    String role;
}