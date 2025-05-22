package dto;

public class UserRegistrationRequest {
    String password;
    String fullName;
    String mobile, email, address, photo;
    String bankName, accountNumber;
    String role;

    public String getPassword() {return password;}
    public String getFullName() {return fullName;}
    public String getMobile() {return mobile;}
    public String getEmail() {return email;}
    public String getAddress() {return address;}
    public String getPhoto() {return photo;}
    public String getBankName() {return bankName;}
    public String getAccountNumber() {return accountNumber;}
    public String getRole() {return role;}
}