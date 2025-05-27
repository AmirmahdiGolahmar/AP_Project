package dto;

import lombok.Getter;

@Getter
public class UserRegistrationRequest {
    String password;
    String full_name;
    String mobile, email, address, profileImageBase64;
    String bank_name, account_number;
    String role;
}