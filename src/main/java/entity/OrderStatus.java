package entity;

public enum OrderStatus {
    PENDING,        // در انتظار تایید
    ACCEPTED,       // تایید شده توسط فروشنده
    PREPARING,      // در حال آماده‌سازی
    OUT_FOR_DELIVERY, // تحویل به پیک
    DELIVERED,      // تحویل داده شده
    CANCELLED       // لغو شده
}
