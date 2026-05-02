-- 增量：管理员角色、订单扩展字段（与当前 schema.sql 已内置内容一致）
--
-- 下一步怎么选：
--   • 全新库：只执行 resources/schema.sql 即可，不必再跑本文件。
--   • 已有旧库（建表时还没有 role / pay_trade_no 等）：在本库执行下面 ALTER + 管理员插入。

USE `e_commerce`;

ALTER TABLE `user` ADD COLUMN `role` TINYINT NOT NULL DEFAULT 0 COMMENT '0用户 1管理员' AFTER `status`;

ALTER TABLE `order` ADD COLUMN `pay_trade_no` VARCHAR(64) NULL COMMENT '支付流水号' AFTER `pay_type`;
ALTER TABLE `order` ADD COLUMN `logistics_company` VARCHAR(64) NULL COMMENT '物流公司' AFTER `close_time`;
ALTER TABLE `order` ADD COLUMN `logistics_no` VARCHAR(128) NULL COMMENT '运单号' AFTER `logistics_company`;
ALTER TABLE `order` ADD COLUMN `refund_reason` VARCHAR(500) NULL COMMENT '退款原因' AFTER `logistics_no`;

INSERT INTO `user` (`username`, `password`, `phone`, `email`, `nickname`, `status`, `role`, `deleted`)
SELECT 'admin', '8e585b735e8e9fa8903498f72619fff8', '13800000000', 'admin@example.com', '管理员', 1, 1, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE `username` = 'admin' AND `deleted` = 0);

UPDATE `user` SET `role` = 1, `password` = '8e585b735e8e9fa8903498f72619fff8' WHERE `username` = 'admin';
