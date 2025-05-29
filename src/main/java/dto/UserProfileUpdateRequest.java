package dto;

import entity.BankInfo;
import entity.User;
import entity.UserRole;
import lombok.Getter;
import lombok.Setter;
import com.google.gson.annotations.SerializedName;

@Getter
@Setter
public class UserProfileUpdateRequest {
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("address")
    private String address;
    @SerializedName("profileImageBase64")
    private String profileImageBase64;
    @SerializedName("bankName")
    private String bankName;
    @SerializedName("accountNumber")
    private String accountNumber;
}
