-- v3：管理端订单字段、商品评价表（可重复执行：已存在则跳过）
USE `e_commerce`;

-- ---------- order 列（逐个检测，避免 Duplicate column） ----------
SET @db = DATABASE();

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'order' AND COLUMN_NAME = 'admin_remark') > 0,
  'SELECT ''skip admin_remark'' AS _m',
  'ALTER TABLE `order` ADD COLUMN `admin_remark` VARCHAR(500) NULL COMMENT ''管理员备注'' AFTER `refund_reason`'
);
PREPARE _st FROM @sql; EXECUTE _st; DEALLOCATE PREPARE _st;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'order' AND COLUMN_NAME = 'status_before_refund') > 0,
  'SELECT ''skip status_before_refund'' AS _m',
  'ALTER TABLE `order` ADD COLUMN `status_before_refund` TINYINT NULL COMMENT ''申请退款前订单状态'' AFTER `admin_remark`'
);
PREPARE _st FROM @sql; EXECUTE _st; DEALLOCATE PREPARE _st;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'order' AND COLUMN_NAME = 'refund_reject_reason') > 0,
  'SELECT ''skip refund_reject_reason'' AS _m',
  'ALTER TABLE `order` ADD COLUMN `refund_reject_reason` VARCHAR(500) NULL COMMENT ''拒绝退款说明'' AFTER `status_before_refund`'
);
PREPARE _st FROM @sql; EXECUTE _st; DEALLOCATE PREPARE _st;

-- ---------- product_review 表（已存在则跳过，避免 1050） ----------
SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.TABLES
   WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'product_review') > 0,
  'SELECT ''skip product_review table'' AS _m',
  'CREATE TABLE `product_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `order_id` BIGINT DEFAULT NULL COMMENT ''关联订单（可选）'',
  `rating` TINYINT NOT NULL DEFAULT 5 COMMENT ''1-5'',
  `content` VARCHAR(1000) NOT NULL DEFAULT '''',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''商品评价'''
);
PREPARE _st FROM @sql; EXECUTE _st; DEALLOCATE PREPARE _st;
