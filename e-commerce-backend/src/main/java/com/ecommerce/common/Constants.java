package com.ecommerce.common;

public class Constants {
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String JWT_TOKEN = "token";

    public static class UserStatus {
        public static final Integer NORMAL = 1;
        public static final Integer DISABLED = 0;
    }

    public static class ProductStatus {
        public static final Integer ON_SALE = 1;
        public static final Integer OFF_SALE = 0;
    }

    public static class OrderStatus {
        public static final Integer PENDING_PAYMENT = 0;
        public static final Integer PAID = 1;
        public static final Integer SHIPPED = 2;
        public static final Integer RECEIVED = 3;
        public static final Integer COMPLETED = 4;
        public static final Integer CLOSED = 5;
        public static final Integer REFUNDING = 6;
        public static final Integer REFUNDED = 7;
    }

    public static class PayType {
        public static final Integer WECHAT = 1;
        public static final Integer ALIPAY = 2;
        public static final Integer BANK_CARD = 3;
    }

    public static class CartSelected {
        public static final Integer SELECTED = 1;
        public static final Integer NOT_SELECTED = 0;
    }

    /** 用户角色：与 user.role 一致 */
    public static class UserRole {
        public static final int USER = 0;
        public static final int ADMIN = 1;
    }
}
