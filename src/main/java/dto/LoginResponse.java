package dto;

public class LoginResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String role;
    private String address;
    private String profileImageBase64;
    private BankInfoDTO bankInfo;

    public LoginResponse(entity.User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.phone = user.getMobile();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.address = user.getAddress();
        this.profileImageBase64 = user.getPhoto(); // Assuming this is already base64

        this.bankInfo = new BankInfoDTO(user.getBankInfo());
    }

    public static class BankInfoDTO {
        private String bankName;
        private String accountNumber;

        public BankInfoDTO(entity.BankInfo bankInfo) {
            this.bankName = bankInfo.getBankName();
            this.accountNumber = bankInfo.getAccountNumber();
        }
    }
}
