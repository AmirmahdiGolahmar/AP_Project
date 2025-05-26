package entity;

import jakarta.persistence.*;

@Embeddable
public class BankInfo {
    private String bankName;
    private String accountNumber;
    private Double walletBalance;

    public BankInfo() {}
    public BankInfo(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.walletBalance = 0.0;
    }

    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }


    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }
    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }
}
