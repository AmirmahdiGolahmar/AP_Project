package entity;

public enum OrderStatus {
    submitted,
    accepted,
    unpaid_and_cancelled,
    waiting_vendor,
    cancelled,
    finding_courier,
    on_the_way,
    completed
}
