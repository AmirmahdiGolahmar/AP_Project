package entity;

public enum OrderStatus {
    PENDING,                // در انتظار تایید
    CANCELLED_BY_CUSTOMER,  //لغو شده توسط مشتری
    ACCEPTED,               // تایید شده توسط فروشنده
    PREPARING,              // در حال آماده‌سازی
    CANCELLED_BY_SELLER,    // لغو شده
    OUT_FOR_DELIVERY,       // تحویل به پیک
    DELIVERED,              // تحویل داده شده
    CANCELLED_BY_DELIVERY   //لغو شده توسط پیک
}
