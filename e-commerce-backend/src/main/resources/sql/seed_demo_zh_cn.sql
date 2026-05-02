-- =============================================================================
-- 电商演示数据（中文商品 / 图片 URL / 地址 / 订单）
-- 数据库：e_commerce（与 schema.sql 一致）
-- 登录演示账号：用户名 demo  密码 123456；短信登录手机号 13800138000（需先图形验证码再发短信，短信码见后端日志）
-- 密码算法与后端一致：MD5(UTF-8 字节 "ecommerce" + 明文 + "salt") 小写十六进制
-- =============================================================================
-- 使用方式（MySQL 8+）：
--   mysql -u root -p e_commerce < src/main/resources/sql/seed_demo_zh_cn.sql
-- 或在客户端中选中本文件执行。
-- =============================================================================

USE `e_commerce`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

/* ---------- 清理本脚本使用的固定 ID（可重复执行） ---------- */
DELETE FROM `order_item` WHERE `order_id` BETWEEN 6001 AND 6010;
DELETE FROM `order`       WHERE `id`         BETWEEN 6001 AND 6010;
DELETE FROM `address`    WHERE `id`         BETWEEN 5001 AND 5005;
DELETE FROM `product`    WHERE `id`         BETWEEN 200  AND 230;
DELETE FROM `category`   WHERE `id`         BETWEEN 101  AND 106;
DELETE FROM `user`       WHERE `id` = 1001;

SET FOREIGN_KEY_CHECKS = 1;

/* ---------- 演示用户（id=1001） ---------- */
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `nickname`, `avatar`, `gender`, `birthday`, `status`, `deleted`)
VALUES (
  1001,
  'demo',
  '8e585b735e8e9fa8903498f72619fff8',
  '13800138000',
  'demo@example.com',
  '演示用户',
  'https://picsum.photos/id/64/200/200',
  1,
  NULL,
  1,
  0
);

/* ---------- 中文分类（首页「商品分类」区块） ---------- */
INSERT INTO `category` (`id`, `name`, `parent_id`, `sort`, `status`, `deleted`) VALUES
(101, '数码家电', NULL, 1,  1, 0),
(102, '服饰鞋包', NULL, 2,  1, 0),
(103, '食品生鲜', NULL, 3,  1, 0),
(104, '家居生活', NULL, 4,  1, 0),
(105, '运动户外', NULL, 5,  1, 0),
(106, '图书文娱', NULL, 6,  1, 0);

/* ---------- 商品（main_image / sub_images 为可访问的占位图，首页精选列表会展示） ---------- */
INSERT INTO `product` (`id`, `name`, `subtitle`, `category_id`, `main_image`, `sub_images`, `description`, `price`, `original_price`, `stock`, `sales`, `status`, `deleted`) VALUES
(200, '无线降噪耳机 Pro', '40dB 深度降噪 · 30h 续航', 101,
  'https://picsum.photos/id/181/600/600',
  '["https://picsum.photos/id/181/800/600","https://picsum.photos/id/119/800/600"]',
  '蓝牙 5.3，双设备连接，支持通透模式。演示数据。', 899.00, 1299.00, 200, 1288, 1, 0),
(201, '轻薄笔记本电脑 14寸', '办公学习 · 全金属机身', 101,
  'https://picsum.photos/id/180/600/600',
  '["https://picsum.photos/id/180/800/600","https://picsum.photos/id/201/800/600"]',
  '16G 内存 / 512G SSD。演示数据。', 4599.00, 5299.00, 80, 356, 1, 0),
(202, '智能手表 Ultra', '血氧心率 · 50 米防水', 101,
  'https://picsum.photos/id/175/600/600',
  NULL,
  'AMOLED 屏，支持睡眠监测。演示数据。', 1299.00, 1599.00, 150, 902, 1, 0),
(203, '纯棉圆领短袖 T 恤', '多色可选 · 亲肤透气', 102,
  'https://picsum.photos/id/1060/600/600',
  '["https://picsum.photos/id/1060/800/600","https://picsum.photos/id/338/800/600"]',
  '200g 精梳棉。演示数据。', 79.00, 119.00, 800, 2340, 1, 0),
(204, '复古直筒牛仔裤', '高腰显瘦 · 弹力舒适', 102,
  'https://picsum.photos/id/535/600/600',
  NULL,
  '经典水洗蓝。演示数据。', 169.00, 229.00, 400, 567, 1, 0),
(205, '休闲运动鞋 Air', '缓震中底 · 日常通勤', 102,
  'https://picsum.photos/id/103/600/600',
  NULL,
  '透气网面。演示数据。', 299.00, 399.00, 320, 445, 1, 0),
(206, '每日坚果礼盒 750g', '7 种果仁 · 独立小包装', 103,
  'https://picsum.photos/id/312/600/600',
  NULL,
  '低温烘焙。演示数据。', 99.00, 139.00, 600, 1890, 1, 0),
(207, '高山乌龙茶 256g', '清香回甘 · 铁罐装', 103,
  'https://picsum.photos/id/225/600/600',
  NULL,
  '台湾高山茶区。演示数据。', 168.00, 218.00, 300, 334, 1, 0),
(208, '北欧风台灯', '三档调光 · 无频闪', 104,
  'https://picsum.photos/id/366/600/600',
  '["https://picsum.photos/id/366/800/600"]',
  '适合卧室与书桌。演示数据。', 189.00, 259.00, 220, 120, 1, 0),
(209, '记忆棉护颈枕', '慢回弹 · 可拆洗外套', 104,
  'https://picsum.photos/id/625/600/600',
  NULL,
  '贴合颈椎曲线。演示数据。', 129.00, 199.00, 500, 778, 1, 0),
(210, '瑜伽垫 TPE 10mm', '防滑加宽 · 附背带', 105,
  'https://picsum.photos/id/82/600/600',
  NULL,
  '环保材质。演示数据。', 89.00, 129.00, 450, 612, 1, 0),
(211, '快干运动毛巾', '吸汗抗菌 · 便携', 105,
  'https://picsum.photos/id/429/600/600',
  NULL,
  '健身房必备。演示数据。', 39.00, 59.00, 900, 1500, 1, 0),
(212, 'Java 核心技术 卷I（第12版）', '经典入门 · 中文译本', 106,
  'https://picsum.photos/id/24/600/600',
  NULL,
  '涵盖 Java 17 新特性。演示数据。', 119.00, 149.00, 200, 421, 1, 0),
(213, '极简主义生活指南', '整理收纳 · 图文版', 106,
  'https://picsum.photos/id/367/600/600',
  NULL,
  '畅销书。演示数据。', 45.00, 68.00, 350, 289, 1, 0);

/* ---------- 收货地址（user_id = 1001） ---------- */
INSERT INTO `address` (`id`, `user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`, `deleted`) VALUES
(5001, 1001, '张三', '13800138000', '上海市', '上海市', '浦东新区', '张江高科园区科苑路88号1号楼101室', 1, 0),
(5002, 1001, '李四', '13900139000', '浙江省', '杭州市', '西湖区', '文三路478号华星时代广场A座', 0, 0),
(5003, 1001, '王五', '13700137000', '广东省', '深圳市', '南山区', '科技园南区深南大道9988号', 0, 0);

/* ---------- 订单（不同状态：待付款/已付款/已发货/已完成） ---------- */
INSERT INTO `order` (`id`, `order_no`, `user_id`, `address_id`, `total_amount`, `status`, `pay_type`, `pay_time`, `deliver_time`, `receive_time`, `close_time`, `deleted`) VALUES
(6001, 'ORD202601151030001', 1001, 5001, 1798.00, 0, NULL, NULL, NULL, NULL, NULL, 0),
(6002, 'ORD202601161200002', 1001, 5001, 4599.00, 1, 2, '2026-01-16 12:05:00', NULL, NULL, NULL, 0),
(6003, 'ORD202601171430003', 1001, 5002, 483.00,  2, 1, '2026-01-17 14:35:00', '2026-01-18 09:00:00', NULL, NULL, 0),
(6004, 'ORD202601181800004', 1001, 5001, 1299.00, 4, 2, '2026-01-18 18:02:00', '2026-01-19 10:00:00', '2026-01-22 15:30:00', NULL, 0),
(6005, 'ORD202601191000005', 1001, 5003, 388.00,  1, 1, '2026-01-19 10:05:00', NULL, NULL, NULL, 0),
(6006, 'ORD202601201100006', 1001, 5001, 99.00,   5, NULL, NULL, NULL, NULL, '2026-01-20 11:30:00', 0);

/* ---------- 订单明细 ---------- */
INSERT INTO `order_item` (`id`, `order_id`, `user_id`, `product_id`, `product_name`, `product_image`, `unit_price`, `quantity`, `total_price`, `deleted`) VALUES
(7001, 6001, 1001, 200, '无线降噪耳机 Pro', 'https://picsum.photos/id/181/600/600', 899.00, 2, 1798.00, 0),
(7002, 6002, 1001, 201, '轻薄笔记本电脑 14寸', 'https://picsum.photos/id/180/600/600', 4599.00, 1, 4599.00, 0),
(7003, 6003, 1001, 203, '纯棉圆领短袖 T 恤', 'https://picsum.photos/id/1060/600/600', 79.00, 2, 158.00, 0),
(7004, 6003, 1001, 204, '复古直筒牛仔裤', 'https://picsum.photos/id/535/600/600', 169.00, 1, 169.00, 0),
(7005, 6003, 1001, 211, '快干运动毛巾', 'https://picsum.photos/id/429/600/600', 39.00, 4, 156.00, 0),
(7006, 6004, 1001, 202, '智能手表 Ultra', 'https://picsum.photos/id/175/600/600', 1299.00, 1, 1299.00, 0),
(7007, 6005, 1001, 205, '休闲运动鞋 Air', 'https://picsum.photos/id/103/600/600', 299.00, 1, 299.00, 0),
(7008, 6005, 1001, 210, '瑜伽垫 TPE 10mm', 'https://picsum.photos/id/82/600/600', 89.00, 1, 89.00, 0),
(7009, 6006, 1001, 206, '每日坚果礼盒 750g', 'https://picsum.photos/id/312/600/600', 99.00, 1, 99.00, 0);

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 说明：
-- 1. 首页「精选商品」接口按 id 倒序分页，新插入商品 id 较大，会优先出现在第一页。
-- 2. 若需与 schema.sql 原有英文分类并存，可保留 category id 1-5；本脚本使用 101-106。
-- 3. 图片域名 picsum.photos 需网络可访问；若环境受限，可将 main_image 批量替换为
--    你们 CDN 或本地 /uploads/ 路径后再导入。
-- 4. 订单归属 user_id=1001（用户名 demo）。若你用其它账号登录，「我的订单」会空，
--    请再执行 seed_orders_for_username.sql，并把脚本里的 @login_name 改成你的 username。
-- =============================================================================
