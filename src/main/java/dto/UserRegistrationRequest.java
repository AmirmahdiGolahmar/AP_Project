package dto;

import lombok.Getter;

@Getter
public class UserRegistrationRequest {
    String password;
    String full_name;
    String mobile, email, address, photo;
    String bank_name, account_number;
    String role;
}