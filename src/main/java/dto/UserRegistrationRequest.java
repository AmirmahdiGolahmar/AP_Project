package dto;

import lombok.Getter;

@Getter
public class UserRegistrationRequest {
    String password;
    String full_name;
    String phone, email, address, profileImageBase64;
    bankInfoDto bank_info;
    String role;
}