package entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class BankInfo {
    private String bankName;
    private String accountNumber;


    public BankInfo() {}

    public BankInfo(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
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

    @Override
    public String toString() {
        return "BankInfo{" +
                "bankName='" + bankName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankInfo)) return false;
        BankInfo bankInfo = (BankInfo) o;
        return Objects.equals(bankName, bankInfo.bankName) &&
                Objects.equals(accountNumber, bankInfo.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankName, accountNumber);
    }
}
