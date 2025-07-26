package dto;

import entity.Transaction;
import lombok.Getter;
import lombok.Setter;
import util.LocalDateTimeAdapter;

@Getter
@Setter
public class TransactionDto {
    Long id;
    Long order_id;
    Long user_id;
    String method;
    String status;
    String timestamp;

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.order_id = transaction.getOrder().getId();
        this.user_id = transaction.getSender().getId();
        this.method = transaction.getPaymentMethod().toString();
        this.status = transaction.getPaymentStatus().toString();
        this.timestamp = LocalDateTimeAdapter.TimeToString(transaction.getTimestamp());
    }
}
