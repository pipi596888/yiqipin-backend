# yiqipin 电商系统测试计划

## 1. 项目概述

yiqipin 是一个综合性的电商系统，包含：
- **后端**: Spring Boot 3.3.2 + Java 17 + MyBatis-Plus + MySQL + Redis
- **前端**: uni-app (Vue 3) - 移动端/H5/微信小程序
- **后台管理**: Vue 3 + Ant Design Vue 4.2.6
- **基础设施**: Docker Compose (MySQL, Redis, MinIO)

## 2. 测试目标

1. 验证系统功能符合需求规格
2. 确保代码质量，发现并记录缺陷
3. 保障系统稳定性和可靠性
4. 验证各模块之间的集成正确性

## 3. 测试范围

### 3.1 后端测试 (yiqipin-backend)

| 模块 | 测试类型 | 优先级 |
|------|----------|--------|
| 用户模块 (User) | 单元测试 + 集成测试 | 高 |
| 认证模块 (Auth) | 单元测试 + 集成测试 | 高 |
| 商品模块 (Product) | 单元测试 + 集成测试 | 高 |
| 购物车模块 (Cart) | 单元测试 + 集成测试 | 高 |
| 订单模块 (Order) | 单元测试 + 集成测试 | 高 |
| 成长体系 (Growth) | 单元测试 + 集成测试 | 中 |

**Service 层测试重点：**
- UserService: 用户注册、登录、信息更新、积分管理
- AuthService: JWT 令牌生成与验证
- ProductService: 商品查询、分类、搜索
- CartService: 购物车增删改查
- OrderService: 订单创建、支付、查询、取消
- GrowthService: 成长值计算、等级变更

### 3.2 API 端点测试

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/register` | POST | 用户注册 |
| `/api/user/info` | GET | 获取用户信息 |
| `/api/product/list` | GET | 商品列表 |
| `/api/product/{id}` | GET | 商品详情 |
| `/api/cart/add` | POST | 添加购物车 |
| `/api/cart/list` | GET | 购物车列表 |
| `/api/order/create` | POST | 创建订单 |
| `/api/order/list` | GET | 订单列表 |
| `/api/growth/records` | GET | 成长记录 |

### 3.3 前端测试 (yiqipin)

- 现有测试: `tests/growth-api.test.js`, `tests/growth-utils.test.js`
- 扩展 E2E 测试覆盖关键用户流程

### 3.4 后台管理测试 (mall-admin-web)

- 登录流程
- 商品管理 CRUD
- 订单管理
- 用户管理

## 4. 测试策略

### 4.1 单元测试 (后端 Service 层)

**工具:** JUnit 5 + Mockito + Spring Boot Test

**原则:**
- 每个 Service 方法至少有一个测试用例
- 测试应覆盖正常流程和异常流程
- 使用 Mockito 模拟依赖
- 保持测试独立性，不依赖外部服务

### 4.2 集成测试 (API 端点)

**工具:** Spring Boot Test (WebEnvironment.MOCK) + MockMvc

**原则:**
- 测试 Controller 层的 HTTP 请求/响应
- 使用 `@WebMvcTest` 或 `@SpringBootTest`
- 验证响应状态码、响应体结构

### 4.3 API 接口测试 (Postman/curl)

**原则:**
- 验证真实 API 响应
- 测试参数边界值
- 测试认证与授权

### 4.4 E2E 测试 (前端)

**工具:** 内置 Node.js test 或手动测试

**原则:**
- 覆盖关键用户路径
- 验证页面渲染和数据展示

## 5. 测试环境

由于 Docker 不可用，采用以下策略：

### 5.1 后端测试

- **单元测试**: 使用 H2 内存数据库或完全 Mock
- **集成测试**: 使用 `@MockBean` 模拟外部依赖

### 5.2 API 测试

- 启动后端服务后使用 curl/Postman 测试
- 需要先配置 MySQL 和 Redis 连接

### 5.3 前端/后台测试

- 使用浏览器手动测试或开发服务器

## 6. 缺陷管理

| 缺陷ID | 描述 | 严重程度 | 状态 | 发现日期 |
|--------|------|----------|------|----------|
| - | (待记录) | - | - | - |

**缺陷严重程度定义:**
- **Critical**: 系统崩溃、功能完全失效
- **High**: 核心功能不可用
- **Medium**: 功能异常但可绕过
- **Low**: UI/UX 问题或优化建议

**缺陷状态:**
- Open → In Progress → Resolved → Closed

## 7. 里程碑

| 阶段 | 任务 | 预计测试用例数 |
|------|------|----------------|
| 1 | 后端 Service 单元测试 | 30+ |
| 2 | 后端 Controller 集成测试 | 20+ |
| 3 | API 接口测试 (curl/Postman) | 15+ |
| 4 | 前端 E2E 测试扩展 | 10+ |
| 5 | 缺陷修复验证 | - |

## 8. 测试目录结构

```
yiqipin-backend/
├── src/test/java/com/yiqipin/backend/
│   ├── service/
│   │   ├── UserServiceTest.java
│   │   ├── AuthServiceTest.java
│   │   ├── ProductServiceTest.java
│   │   ├── CartServiceTest.java
│   │   ├── OrderServiceTest.java
│   │   └── GrowthServiceTest.java
│   └── controller/
│       ├── AuthControllerTest.java
│       ├── UserControllerTest.java
│       └── ...
```

## 9. 测试执行方式

### 9.1 运行后端单元测试
```bash
cd /mnt/c/Users/ZHI/yiqipin-backend
./mvnw test -Dtest=UserServiceTest
```

### 9.2 运行所有测试
```bash
./mvnw test
```

### 9.3 API 测试示例 (curl)
```bash
# 登录测试
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

---

**创建日期:** 2026-03-11
**版本:** 1.0
**测试工程师:** Claude Code
