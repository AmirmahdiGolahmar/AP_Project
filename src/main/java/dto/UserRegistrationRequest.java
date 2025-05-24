package dto;

import lombok.Getter;

@Getter
public class UserRegistrationRequest {
    String password;
    String fullName;
    String mobile, email, address, photo;
    String bankName, accountNumber;
    String role;
}