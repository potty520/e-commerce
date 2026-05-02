-- E-commerce Platform Database Schema
--
-- 表已存在时：使用 IF NOT EXISTS 跳过建表，不再报 1050（不会自动改旧表结构）。
-- 旧库缺 role、pay_trade_no 等列时，请执行 sql/migration_v2_features.sql。
-- 管理端订单备注/关闭/拒绝退款、评价表等：sql/migration_v3_admin_order_review_pay.sql
-- 若库中已有数据，请勿重复执行下方「示例 INSERT」，否则会多出重复分类/商品；仅需演示数据请用 sql/seed_demo_zh_cn.sql。

CREATE DATABASE IF NOT EXISTS e_commerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE e_commerce;

-- User Table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
    `username` VARCHAR(50) NOT NULL COMMENT 'Username',
    `password` VARCHAR(100) NOT NULL COMMENT 'Password',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Phone',
    `email` VARCHAR(100) DEFAULT NULL COMMENT 'Email',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT 'Nickname',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT 'Avatar URL',
    `gender` TINYINT DEFAULT NULL COMMENT 'Gender: 0-Female, 1-Male, 2-Unknown',
    `birthday` DATETIME DEFAULT NULL COMMENT 'Birthday',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-Disabled, 1-Normal',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '0用户 1管理员',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Deleted: 0-Not Deleted, 1-Deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User Table';

-- Category Table
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
    `name` VARCHAR(50) NOT NULL COMMENT 'Category Name',
    `parent_id` BIGINT DEFAULT NULL COMMENT 'Parent Category ID',
    `sort` INT NOT NULL DEFAULT 0 COMMENT 'Sort Order',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-Off, 1-On',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Deleted: 0-Not Deleted, 1-Deleted',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Category Table';

-- Product Table
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Product ID',
    `name` VARCHAR(200) NOT NULL COMMENT 'Product Name',
    `subtitle` VARCHAR(255) DEFAULT NULL COMMENT 'Product Subtitle',
    `category_id` BIGINT NOT NULL COMMENT 'Category ID',
    `main_image` VARCHAR(255) DEFAULT NULL COMMENT 'Main Image',
    `sub_images` TEXT DEFAULT NULL COMMENT 'Sub Images (JSON)',
    `description` TEXT DEFAULT NULL COMMENT 'Product Description',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'Price',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT 'Original Price',
    `stock` INT NOT NULL DEFAULT 0 COMMENT 'Stock',
    `sales` INT NOT NULL DEFAULT 0 COMMENT 'Sales Count',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-Off, 1-On',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Deleted: 0-Not Deleted, 1-Deleted',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product Table';

-- Address Table
CREATE TABLE IF NOT EXISTS `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Address ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT 'Receiver Name',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT 'Receiver Phone',
    `province` VARCHAR(50) NOT NULL COMMENT 'Province',
    `city` VARCHAR(50) NOT NULL COMMENT 'City',
    `district` VARCHAR(50) NOT NULL COMMENT 'District',
    `detail_address` VARCHAR(255) NOT NULL COMMENT 'Detail Address',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT 'Is Default: 0-No, 1-Yes',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Deleted: 0-Not Deleted, 1-Deleted',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Address Table';

-- Cart Table
CREATE TABLE IF NOT EXISTS `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Cart ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `product_id` BIGINT NOT NULL COMMENT 'Product ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT 'Quantity',
    `selected` TINYINT NOT NULL DEFAULT 0 COMMENT 'Selected: 0-Not Selected, 1-Selected',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Deleted: 0-Not Deleted, 1-Deleted',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Cart Table';

-- Order Table
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT 'Order Number',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `address_id` BIGINT DEFAULT NULL COMMENT 'Address ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT 'Total Amount',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT 'Status: 0-Pending Payment, 1-Paid, 2-Shipped, 3-Received, 4-Completed, 5-Closed, 6-Refunding, 7-Refunded',
    `pay_type` TINYINT DEFAULT NULL COMMENT 'Pay Type: 1-WeChat, 2-Alipay, 3-Bank Card',
    `pay_trade_no` VARCHAR(64) DEFAULT NULL COMMENT '支付流水号',
    `pay_time` DATETIME DEFAULT NULL COMMENT 'Pay Time',
    `deliver_time` DATETIME DEFAULT NULL COMMENT 'Deliver Time',
    `receive_time` DATETIME DEFAULT NULL COMMENT 'Receive Time',
    `close_time` DATETIME DEFAULT NULL COMMENT 'Close Time',
    `logistics_company` VARCHAR(64) DEFAULT NULL COMMENT '物流公司',
    `logistics_no` VARCHAR(128) DEFAULT NULL COMMENT '运单号',
    `refund_reason` VARCHAR(500) DEFAULT NULL COMMENT '退款原因',
    `admin_remark` VARCHAR(500) DEFAULT NULL COMMENT '管理员备注',
    `status_before_refund` TINYINT DEFAULT NULL COMMENT '申请退款前状态',
    `refund_reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '拒绝退款说明',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Deleted: 0-Not Deleted, 1-Deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order Table';

-- Order Item Table
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order Item ID',
    `order_id` BIGINT NOT NULL COMMENT 'Order ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `product_id` BIGINT NOT NULL COMMENT 'Product ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT 'Product Name (Snapshot)',
    `product_image` VARCHAR(255) DEFAULT NULL COMMENT 'Product Image (Snapshot)',
    `unit_price` DECIMAL(10,2) NOT NULL COMMENT 'Unit Price (Snapshot)',
    `quantity` INT NOT NULL COMMENT 'Quantity',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT 'Total Price',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Deleted: 0-Not Deleted, 1-Deleted',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order Item Table';

-- Product Review
CREATE TABLE IF NOT EXISTS `product_review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `order_id` BIGINT DEFAULT NULL,
    `rating` TINYINT NOT NULL DEFAULT 5 COMMENT '1-5',
    `content` VARCHAR(1000) NOT NULL DEFAULT '',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价';

-- Insert Sample Categories
INSERT INTO `category` (`name`, `parent_id`, `sort`, `status`) VALUES
('Electronics', NULL, 1, 1),
('Clothing', NULL, 2, 1),
('Books', NULL, 3, 1),
('Home & Garden', NULL, 4, 1),
('Sports', NULL, 5, 1);

-- Insert Sample Products
INSERT INTO `product` (`name`, `subtitle`, `category_id`, `main_image`, `price`, `original_price`, `stock`, `sales`, `status`) VALUES
('Smartphone X Pro', 'Latest flagship smartphone', 1, 'https://via.placeholder.com/400', 3999.00, 4499.00, 100, 50, 1),
('Wireless Earbuds', 'Premium sound quality', 1, 'https://via.placeholder.com/400', 299.00, 399.00, 200, 120, 1),
('Cotton T-Shirt', 'Comfortable casual wear', 2, 'https://via.placeholder.com/400', 89.00, 129.00, 500, 300, 1),
('Java Programming', 'Complete guide to Java', 3, 'https://via.placeholder.com/400', 79.00, 99.00, 150, 80, 1),
('Garden Tools Set', 'Essential garden equipment', 4, 'https://via.placeholder.com/400', 199.00, 259.00, 80, 40, 1);

-- 管理员（登录后台：/admin ，密码 123456）
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `nickname`, `status`, `role`, `deleted`)
SELECT 'admin', '8e585b735e8e9fa8903498f72619fff8', '13800000000', 'admin@example.com', '管理员', 1, 1, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE `username` = 'admin' AND `deleted` = 0);
