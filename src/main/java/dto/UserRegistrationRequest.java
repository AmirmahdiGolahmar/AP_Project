package dto;

import lombok.Getter;

@Getter
public class UserRegistrationRequest {
    String password;
    String fullName;
    String mobile, email, address, profileImageBase64;
    String bankName, accountNumber;
    String role;
}