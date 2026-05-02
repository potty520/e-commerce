# 电商平台

Vue 3 + Spring Boot 电商演示项目。

## 结构

- `e-commerce-backend`：后端 API（MySQL、Redis、JWT）
- `e-commerce-frontend`：前端（Vite + Element Plus）

## 本地运行

1. MySQL 建库并执行 `e-commerce-backend/src/main/resources/schema.sql` 与所需 `sql/migration_*.sql`
2. 后端：`cp e-commerce-backend/src/main/resources/application.example.yml e-commerce-backend/src/main/resources/application.yml` 后修改数据库与 Redis
3. 启动 Redis；后端 `mvn spring-boot:run`；前端 `npm install && npm run dev`

## 许可证

私有/学习用途请自行约定。
