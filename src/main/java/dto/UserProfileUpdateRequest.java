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
    @SerializedName("full_name")
    private String full_name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("address")
    private String address;
    @SerializedName("profileImageBase64")
    private String profileImageBase64;
    @SerializedName("bank_name")
    private String bank_name;
    @SerializedName("account_number")
    private String account_number;
}
