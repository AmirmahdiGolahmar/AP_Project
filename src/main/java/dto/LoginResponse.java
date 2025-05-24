package dto;

public class LoginResponse {
    private Long id;
    private String full_name;
    private String phone;
    private String email;
    private String role;
    private String address;
    private String profileImageBase64;
    private BankInfoDTO bank_info;

    public LoginResponse(entity.User user) {
        this.id = user.getId();
        this.full_name = user.getFullName();
        this.phone = user.getMobile();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.address = user.getAddress();
        this.profileImageBase64 = user.getPhoto(); // Assuming this is already base64

        this.bank_info = new BankInfoDTO(user.getBankInfo());
    }

    public static class BankInfoDTO {
        private String bank_name;
        private String account_number;

        public BankInfoDTO(entity.BankInfo bankInfo) {
            this.bank_name = bankInfo.getBankName();
            this.account_number = bankInfo.getAccountNumber();
        }
    }
}
