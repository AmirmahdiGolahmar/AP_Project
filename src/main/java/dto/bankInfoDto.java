package dto;

import entity.BankInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class bankInfoDto {
    String bank_name = null;
    String account_number = null;

    public bankInfoDto(BankInfo bankInfo) {
        this.bank_name = bankInfo.getBankName();
        this.account_number = bankInfo.getAccountNumber();
    }
    public bankInfoDto(String bank_name, String account_number) {
        this.bank_name = bank_name;
        this.account_number = account_number;
    }
    public bankInfoDto() {

    }
}
