-- =============================================================================
-- 为「当前登录用户」批量插入演示订单（解决「我的订单」暂无数据）
--
-- 原因：订单列表只查当前 JWT 的 user_id。若你用自建账号登录，而数据只在 demo 上，
--       就会一直暂无数据。
--
-- 用法：
--   1. 修改下面 @login_name，改成你登录用的 username（与表 user.username 一致）。
--   2. 库中需有在售商品（可先执行 seed_demo_zh_cn.sql）。
--   3. 在 e_commerce 执行本脚本后，用该账号重新登录（或刷新页面）。
-- =============================================================================

USE `e_commerce`;

SET NAMES utf8mb4;

SET @login_name = 'demo';

SET @uid := (SELECT `id` FROM `user` WHERE `username` = @login_name AND `deleted` = 0 LIMIT 1);
SET @addr := (SELECT `id` FROM `address` WHERE `user_id` = @uid AND `deleted` = 0 ORDER BY `is_default` DESC, `id` ASC LIMIT 1);

SET @p1 := (SELECT `id` FROM `product` WHERE `deleted` = 0 AND `status` = 1 ORDER BY `id` DESC LIMIT 1 OFFSET 0);
SET @p2 := (SELECT `id` FROM `product` WHERE `deleted` = 0 AND `status` = 1 ORDER BY `id` DESC LIMIT 1 OFFSET 1);
SET @p3 := (SELECT `id` FROM `product` WHERE `deleted` = 0 AND `status` = 1 ORDER BY `id` DESC LIMIT 1 OFFSET 2);
SET @p4 := (SELECT `id` FROM `product` WHERE `deleted` = 0 AND `status` = 1 ORDER BY `id` DESC LIMIT 1 OFFSET 3);

SELECT IF(@uid IS NULL, '错误：找不到该 username，请修改脚本里的 @login_name', CONCAT('OK，将为 user_id=', @uid, ' 写入演示订单')) AS `检查`;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM `order_item` WHERE `id` BETWEEN 9201 AND 9300;
DELETE FROM `order`       WHERE `id` BETWEEN 8101 AND 8120;
SET FOREIGN_KEY_CHECKS = 1;

-- MySQL 不允许写成 SELECT ... WHERE ... UNION ALL ... WHERE ...；改为子查询外层统一 WHERE
INSERT INTO `order` (`id`, `order_no`, `user_id`, `address_id`, `total_amount`, `status`, `pay_type`, `pay_time`, `deliver_time`, `receive_time`, `close_time`, `deleted`)
SELECT * FROM (
  SELECT 8101 AS c1, 'DEMOORD8101001' AS c2, @uid AS c3, @addr AS c4, 199.00 AS c5, 0 AS c6, NULL AS c7, NULL AS c8, NULL AS c9, NULL AS c10, NULL AS c11, 0 AS c12
  UNION ALL SELECT 8102, 'DEMOORD8101002', @uid, @addr, 299.00, 0, NULL, NULL, NULL, NULL, NULL, 0
  UNION ALL SELECT 8103, 'DEMOORD8101003', @uid, @addr, 89.50,  0, NULL, NULL, NULL, NULL, NULL, 0
  UNION ALL SELECT 8104, 'DEMOORD8101004', @uid, @addr, 4599.00, 1, 2, '2026-02-01 10:00:00', NULL, NULL, NULL, 0
  UNION ALL SELECT 8105, 'DEMOORD8101005', @uid, @addr, 1299.00, 1, 1, '2026-02-02 14:20:00', NULL, NULL, NULL, 0
  UNION ALL SELECT 8106, 'DEMOORD8101006', @uid, @addr, 168.00,  1, 2, '2026-02-03 09:15:00', NULL, NULL, NULL, 0
  UNION ALL SELECT 8107, 'DEMOORD8101007', @uid, @addr, 899.00,  2, 2, '2026-02-04 11:00:00', '2026-02-05 08:30:00', NULL, NULL, 0
  UNION ALL SELECT 8108, 'DEMOORD8101008', @uid, @addr, 483.00,  2, 1, '2026-02-05 16:00:00', '2026-02-06 10:00:00', NULL, NULL, 0
  UNION ALL SELECT 8109, 'DEMOORD8101009', @uid, @addr, 1299.00, 4, 2, '2026-01-10 12:00:00', '2026-01-11 09:00:00', '2026-01-15 18:00:00', NULL, 0
  UNION ALL SELECT 8110, 'DEMOORD8101010', @uid, @addr, 388.00,  4, 1, '2026-01-12 10:00:00', '2026-01-13 11:00:00', '2026-01-20 12:00:00', NULL, 0
  UNION ALL SELECT 8111, 'DEMOORD8101011', @uid, @addr, 119.00,  4, 2, '2026-01-14 15:00:00', '2026-01-16 10:00:00', '2026-01-18 20:00:00', NULL, 0
  UNION ALL SELECT 8112, 'DEMOORD8101012', @uid, @addr, 99.00,   3, 2, '2026-01-08 09:00:00', '2026-01-09 10:00:00', '2026-01-10 11:00:00', NULL, 0
  UNION ALL SELECT 8113, 'DEMOORD8101013', @uid, @addr, 45.00,   5, NULL, NULL, NULL, NULL, '2026-01-05 12:00:00', 0
) AS `ord_rows`
WHERE @uid IS NOT NULL;

INSERT INTO `order_item` (`id`, `order_id`, `user_id`, `product_id`, `product_name`, `product_image`, `unit_price`, `quantity`, `total_price`, `deleted`)
SELECT * FROM (
  SELECT 9201 AS c1, 8101 AS c2, @uid AS c3, @p1 AS c4, (SELECT `name` FROM `product` WHERE `id` = @p1) AS c5, (SELECT `main_image` FROM `product` WHERE `id` = @p1) AS c6, 199.00 AS c7, 1 AS c8, 199.00 AS c9, 0 AS c10
  UNION ALL SELECT 9202, 8102, @uid, @p2, (SELECT `name` FROM `product` WHERE `id` = @p2), (SELECT `main_image` FROM `product` WHERE `id` = @p2), 299.00, 1, 299.00, 0
  UNION ALL SELECT 9203, 8103, @uid, @p3, (SELECT `name` FROM `product` WHERE `id` = @p3), (SELECT `main_image` FROM `product` WHERE `id` = @p3), 89.50,  1, 89.50,  0
  UNION ALL SELECT 9204, 8104, @uid, @p1, (SELECT `name` FROM `product` WHERE `id` = @p1), (SELECT `main_image` FROM `product` WHERE `id` = @p1), 4599.00, 1, 4599.00, 0
  UNION ALL SELECT 9205, 8105, @uid, @p4, (SELECT `name` FROM `product` WHERE `id` = @p4), (SELECT `main_image` FROM `product` WHERE `id` = @p4), 1299.00, 1, 1299.00, 0
  UNION ALL SELECT 9206, 8106, @uid, @p3, (SELECT `name` FROM `product` WHERE `id` = @p3), (SELECT `main_image` FROM `product` WHERE `id` = @p3), 168.00, 1, 168.00, 0
  UNION ALL SELECT 9207, 8107, @uid, @p1, (SELECT `name` FROM `product` WHERE `id` = @p1), (SELECT `main_image` FROM `product` WHERE `id` = @p1), 899.00, 1, 899.00, 0
  UNION ALL SELECT 9208, 8108, @uid, @p2, (SELECT `name` FROM `product` WHERE `id` = @p2), (SELECT `main_image` FROM `product` WHERE `id` = @p2), 158.00, 1, 158.00, 0
  UNION ALL SELECT 9209, 8108, @uid, @p3, (SELECT `name` FROM `product` WHERE `id` = @p3), (SELECT `main_image` FROM `product` WHERE `id` = @p3), 325.00, 1, 325.00, 0
  UNION ALL SELECT 9210, 8109, @uid, @p4, (SELECT `name` FROM `product` WHERE `id` = @p4), (SELECT `main_image` FROM `product` WHERE `id` = @p4), 1299.00, 1, 1299.00, 0
  UNION ALL SELECT 9211, 8110, @uid, @p1, (SELECT `name` FROM `product` WHERE `id` = @p1), (SELECT `main_image` FROM `product` WHERE `id` = @p1), 299.00, 1, 299.00, 0
  UNION ALL SELECT 9212, 8110, @uid, @p2, (SELECT `name` FROM `product` WHERE `id` = @p2), (SELECT `main_image` FROM `product` WHERE `id` = @p2), 89.00,  1, 89.00,  0
  UNION ALL SELECT 9213, 8111, @uid, @p3, (SELECT `name` FROM `product` WHERE `id` = @p3), (SELECT `main_image` FROM `product` WHERE `id` = @p3), 119.00, 1, 119.00, 0
  UNION ALL SELECT 9214, 8112, @uid, @p4, (SELECT `name` FROM `product` WHERE `id` = @p4), (SELECT `main_image` FROM `product` WHERE `id` = @p4), 99.00,  1, 99.00,  0
  UNION ALL SELECT 9215, 8113, @uid, @p3, (SELECT `name` FROM `product` WHERE `id` = @p3), (SELECT `main_image` FROM `product` WHERE `id` = @p3), 45.00,  1, 45.00,  0
) AS `item_rows`
WHERE @uid IS NOT NULL AND @p1 IS NOT NULL AND @p2 IS NOT NULL AND @p3 IS NOT NULL AND @p4 IS NOT NULL;
