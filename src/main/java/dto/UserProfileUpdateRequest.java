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

    @SerializedName("bank_info")
    private bankInfoDto bank_info = new bankInfoDto();

//    @Override
//    public String toString() {
//        return String.format("{\n" +
//                        "  \"full_name\": \"%s\",\n" +
//                        "  \"phone\": \"%s\",\n" +
//                        "  \"email\": \"%s\",\n" +
//                        "  \"address\": \"%s\",\n" +
//                        "  \"profileImageBase64\": \"%s\",\n" +
//                        "  \"bank_info\": {\n" +
//                        "    \"bank_name\": \"%s\",\n" +
//                        "    \"account_number\": \"%s\"\n" +
//                        "  }\n" +
//                        "}", full_name, phone, email, address, profileImageBase64,
//                bank_info != null ? bank_info.getBank_name() : "null",
//                bank_info != null ? bank_info.getAccount_number() : "null");
//    }
}