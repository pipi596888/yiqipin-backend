# yiqipin 电商系统缺陷记录

## 缺陷列表

### 2026-03-11

| 缺陷ID | 模块 | 描述 | 严重程度 | 状态 | 备注 |
|--------|------|------|----------|------|------|
| BUG-001 | 后端-UserService | 用户注册时未检查用户名是否已存在，可能导致重复注册 | High | Open | 建议在 register 方法中添加用户名唯一性检查 |
| BUG-002 | 后端-OrderService | createOrder 方法中商品库存不足时未回滚已扣减的库存 | Medium | Open | 需要在事务中处理库存回滚 |
| BUG-003 | 后端-Controller | Controller 层未对用户输入进行完整校验 | Medium | Open | 建议添加更完善的输入验证 |
| BUG-004 | 前端-Store | 购物车数量为0时未自动移除商品 | Low | Open | 建议在 store 中处理 |
| BUG-005 | 后端-Security | JWT 令牌未设置过期时间 | High | Open | JwtUtil 中需要配置过期时间 |

---

## 缺陷详细说明

### BUG-001: 用户注册重复

**位置:** `UserServiceImpl.java:68-96`

**描述:** 用户注册方法 `register()` 未检查用户名是否已存在于数据库中，可能导致重复用户。

**当前代码:**
```java
public User register(String username, String password, String phone, String email) {
    User user = new User();
    user.setUsername(username);
    // ... 未检查用户名是否存在
    this.save(user);
}
```

**建议修复:**
```java
public User register(String username, String password, String phone, String email) {
    // 检查用户名是否已存在
    User existingUser = findByUsername(username);
    if (existingUser != null) {
        throw new RuntimeException("Username already exists");
    }
    // ...
}
```

---

### BUG-002: 订单创建库存问题

**位置:** `OrderServiceImpl.java:40-103`

**描述:** `createOrder` 方法在循环扣减库存时，如果中间某个商品失败，已扣减的库存不会回滚。

**当前代码:**
```java
for (CartItem cartItem : cartItems) {
    // 扣减库存
    productService.reduceStock(product.getId(), cartItem.getQuantity());
    // 如果后面抛出异常，前面已扣减的库存不会回滚
}
```

**建议修复:** 使用分布式锁或在事务中统一处理库存扣减。

---

### BUG-003: Controller 输入验证不足

**位置:** 各 Controller 文件

**描述:** 部分 Controller 方法未对必填参数进行完整的校验。

**示例:**
- `register` 方法虽然检查了 username/password，但未检查格式（长度、特殊字符等）
- `addItem` 方法未校验 quantity 是否为正数

---

### BUG-004: 购物车数量为0处理

**位置:** 前端 store/cart.js

**描述:** 当用户将商品数量更新为0时，应该自动移除该商品，而非保留在购物车中。

---

### BUG-005: JWT 令牌无过期时间

**位置:** `JwtUtil.java`

**描述:** JWT 令牌生成时未设置过期时间，导致令牌永久有效，存在安全隐患。

**建议修复:**
```java
// 添加过期时间配置
Duration expiration = Duration.ofHours(24); // 24小时
return Jwts.builder()
    .setSubject(username)
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + expiration.toMillis())) // 添加过期时间
    .signWith(key)
    .compact();
```

---

## 修复状态更新

| 缺陷ID | 修复日期 | 修复人 | 备注 |
|--------|----------|--------|------|
| BUG-001 | - | - | 待修复 |
| BUG-002 | - | - | 待修复 |
| BUG-003 | - | - | 待修复 |
| BUG-004 | - | - | 待修复 |
| BUG-005 | - | - | 待修复 |

---

*创建日期: 2026-03-11*
*测试工程师: Claude Code*
