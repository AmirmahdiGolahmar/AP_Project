package entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class BankInfo {
    private String bankName;
    private String accountNumber;
    private String shebaNumber;
    private String accountHolder;

    public BankInfo() {}

    public BankInfo(String bankName, String accountNumber, String shebaNumber,String accountHolder) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.shebaNumber = shebaNumber;
        this.accountHolder = accountHolder;
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

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setIban(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    @Override
    public String toString() {
        return "BankInfo{" +
                "bankName='" + bankName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountHolder='" + accountHolder + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankInfo)) return false;
        BankInfo bankInfo = (BankInfo) o;
        return Objects.equals(bankName, bankInfo.bankName) &&
                Objects.equals(accountNumber, bankInfo.accountNumber) &&
                Objects.equals(accountHolder, bankInfo.accountHolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankName, accountNumber, accountHolder);
    }
}
