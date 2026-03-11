# yiqipin-backend

Spring Boot backend for the UniApp frontend under `E:\一起拼\一起拼`.

## Tech

- Java 17
- Spring Boot 3.3.x
- Maven

## Run

1. Install JDK 17+
2. Install Maven 3.9+
3. Start service:

```bash
mvn spring-boot:run
```

Server starts on `http://localhost:8080`.

## Implemented APIs

- `POST /api/login`
- `GET /api/products`
- `GET /api/product/{id}`
- `POST /api/cart/add`
- `POST /api/order/create`
- `POST /api/order/pay`
- `GET /api/user/profile`
- `GET /api/user/address`
- `GET /api/user/orders`
- `GET /api/marketing/campaigns`
- `GET /api/recommendation/home`
- `GET /api/analytics/overview`

## Response Shape

All endpoints use:

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

## Notes

- Current implementation uses in-memory mock data.
- No database integration yet.
- CORS is open for local integration.
