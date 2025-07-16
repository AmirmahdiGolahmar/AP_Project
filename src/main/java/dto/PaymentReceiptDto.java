package dto;

import entity.Transaction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReceiptDto {
    Long id;
    Long order_id;
    Long user_id;
    String method;
    String status;

    public PaymentReceiptDto(Transaction transaction) {
        this.id = transaction.getId();
        this.order_id = transaction.getOrder().getId();
        this.user_id = transaction.getOrder().getCustomer().getId();
        this.method = transaction.getPaymentMethod().toString();
        this.status = transaction.getPaymentStatus().toString();
    }
}
