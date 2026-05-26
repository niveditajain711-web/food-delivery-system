# Food Delivery System

> Order food, reserve stock, and track delivery in real time — React UI, Spring Boot REST gateway, and gRPC microservices.

**Folder names:** Rename the project root to `food-delivery-system` and `orderflow-ui` to `food-delivery-ui` when nothing has the folder open (IDE/terminal). Until then, use the paths below as they exist on disk.

A **Java / Spring Boot / Maven** learning project with a **React** front end and **REST API gateway** over internal **gRPC** services.

| Component | Port | Role |
|-----------|------|------|
| `food-delivery-ui` | 5173 | React app (calls REST) |
| `api-gateway` | 8080 | REST → gRPC bridge |
| `order-service` | 9090 | Place / get orders (gRPC) |
| `inventory-service` | 9091 | Reserve / release stock (gRPC) |
| `tracking-service` | 9092 | Stream driver location (gRPC) |

Shared contracts: `orderflow-proto` (`.proto` → Java stubs; package `com.orderflow` unchanged).

## Prerequisites

- JDK 21+
- Maven 3.9+
- Node.js 18+ (for React UI)
- [grpcurl](https://github.com/fullstorydev/grpcurl) (optional)

## Build

```bash
cd food-delivery-system
mvn clean install
cd food-delivery-ui
npm install
```

## Run (full stack)

**1. Backend gRPC services** (3 terminals — inventory first):

```bash
mvn -pl inventory-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl tracking-service spring-boot:run
```

**2. REST API gateway** (4th terminal):

```bash
mvn -pl api-gateway spring-boot:run
```

**3. React UI** (5th terminal):

```bash
cd food-delivery-ui
npm run dev
```

Open **http://localhost:5173** — place an order, view details, and watch live tracking.

### Port already in use?

Stop leftover Java processes (Windows):

```powershell
netstat -ano | findstr ":9091"
Stop-Process -Id <PID> -Force
```

## Test with grpcurl

Proto package names use underscores in grpcurl (`orderflow.inventory` → paths below).

**1. Place an order** (order-service :9090):

```bash
grpcurl -plaintext -d "{\"customer_id\":\"cust-1\",\"restaurant_id\":\"restaurant-1\",\"items\":[{\"product_id\":\"burger-1\",\"quantity\":2},{\"product_id\":\"fries-1\",\"quantity\":1}]}" localhost:9090 orderflow.order.OrderService/PlaceOrder
```

Copy the `orderId` from the response.

**2. Get the order**:

```bash
grpcurl -plaintext -d "{\"order_id\":\"<ORDER_ID>\"}" localhost:9090 orderflow.order.OrderService/GetOrder
```

**3. Stream delivery location** (tracking-service :9092):

```bash
grpcurl -plaintext -d "{\"order_id\":\"<ORDER_ID>\"}" localhost:9092 orderflow.tracking.TrackingService/StreamDeliveryLocation
```

You should see ~5 location updates over ~10 seconds; the last one has `"delivered": true`.

**4. Error example** (unknown product):

```bash
grpcurl -plaintext -d "{\"customer_id\":\"cust-1\",\"restaurant_id\":\"restaurant-1\",\"items\":[{\"product_id\":\"unknown\",\"quantity\":1}]}" localhost:9090 orderflow.order.OrderService/PlaceOrder
```

## Sample inventory

| product_id | restaurant_id | stock |
|------------|---------------|-------|
| burger-1 | restaurant-1 | 50 |
| fries-1 | restaurant-1 | 100 |
| pizza-1 | restaurant-2 | 30 |

## REST API (api-gateway)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/menu?restaurantId=` | Menu items (static catalog) |
| POST | `/api/orders` | Place order → gRPC `PlaceOrder` |
| GET | `/api/orders/{orderId}` | Get order → gRPC `GetOrder` |
| GET | `/api/orders/{orderId}/track` | SSE stream → gRPC `StreamDeliveryLocation` |

## Project layout

```
food-delivery-system/
├── orderflow-proto/      # shared .proto (artifact name unchanged)
├── inventory-service/
├── order-service/
├── tracking-service/
├── api-gateway/
└── food-delivery-ui/     # React + Vite
```

## Flow

```
React (5173) --REST--> api-gateway (8080) --gRPC--> order-service (9090)
                                              |--> tracking-service (9092)
                         order-service --gRPC--> inventory-service (9091)
```
