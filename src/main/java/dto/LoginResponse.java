package dto;

public class LoginResponse {
    private Long id;
    private String full_name;
    private String phone;
    private String email;
    private String role;
    private String address;
    private String profileImageBase64;
    private Bank_infoDTO bank_info;

    public LoginResponse(entity.User user) {
        this.id = user.getId();
        this.full_name = user.getFull_name();
        this.phone = user.getMobile();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.address = user.getAddress();
        this.profileImageBase64 = user.getPhoto(); // Assuming this is already base64

        this.bank_info = new Bank_infoDTO(user.getBank_info());
    }

    public static class Bank_infoDTO {
        private String bank_name;
        private String account_number;

        public Bank_infoDTO(entity.Bank_info bank_info) {
            this.bank_name = bank_info.getBank_name();
            this.account_number = bank_info.getAccount_number();
        }
    }
}
