# Crypto Trading System Testing Guide

This guide provides detailed steps to test the crypto trading system built with Spring Boot and an in-memory H2
database. The system supports Ethereum (ETHUSDT) and Bitcoin (BTCUSDT) trading pairs, with price aggregation from
Binance and Huobi, and includes APIs for trading, wallet balance, and transaction history.

## Technical

- **Spring Boot 3**
- **H2 Database**
- **Java 21**
- **Maven**

## Tool For Tests

**Swagger for testing:** http://localhost:8081/swagger-ui/index.html#/

![swagger](./environment/images/swagger.png)

**Postman Json:** [cryptotrading.postman_collection](./environment/postman/cryptotrading.postman_collection.json)

## Initial Setup

The application initializes mock data on startup:

- **User**: `test-user-1` with active status.
- **Wallets**:
    - 50,000 USDT
- **Coins**: ETHUSDT and BTCUSDT with initial prices of 0 (updated by scheduler).

## Testing Steps

## Start docker compose

**Docker compose:** [Docker-compose.yml](./environment/docker-compose.yml)

```bash
docker compose up -d
```

**Start application**

```
mvn clean install -DskipTests -Pdev
```

```
mvn spring-boot:run
```

### Step 0: Login to get access_token

**Login**

```bash
curl --location 'http://localhost:8081/api/v1/auth/access-token' \
--header 'Content-Type: application/json' \
--data '{
  "username": "test-user-1",
  "password": "123123",
  "platform": "web",
  "deviceToken": "x-token",
  "versionApp": "1.1.1"
}'
```

**Response**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOltdLCJzdWIiOiJ0ZXN0LXVzZXItMSIsImlhdCI6MTc0MjgyMzI5OCwiZXhwIjoxNzQyODU5Mjk4fQ.wJyJL-8bDKLhbbRD9FQPnzEMae5_vRvtJ05fS6fLdgk",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXVzZXItMSIsImlhdCI6MTc0MjgyMzI5OCwiZXhwIjoxNzQ1NDE1Mjk4fQ.sZkl3vCwIirz_lFtVyMcGAesn8yYsLFFzFMKTdwmb08",
  "userId": 1
}
```

### Step 1: Verify Price Aggregation (Task 1)

**Description**: A scheduler runs every 10 seconds to fetch and aggregate prices from Binance (
`https://api.binance.com/api/v3/ticker/bookTicker`) and Huobi (`https://api.huobi.pro/market/tickers`), storing the best
bid (highest) and ask (lowest) prices in `tbl_coins`.

**Steps**:

1. Start the application.
2. Wait 10-20 seconds for the scheduler to run at least once.
3. **Check Logs**: Look for messages like `Coin prices updated at 2025-03-23T10:00:00` in the console.
4. **Optional H2 Verification**:
    - **Query**: `SELECT * FROM tbl_coins`
    - **Expected**: Rows for ETHUSDT and BTCUSDT with non-zero `bid_price` and `ask_price` (e.g., 3000.00 and 3005.00).

**Expected Behavior**: Prices update every 10 seconds with real-time data from Binance and Huobi.

### Step 2: Retrieve Latest Best Aggregated Price (Task 2)

**API**: `GET /api/coins/aggregated-price`

**Description**: Retrieves the latest best aggregated price for a symbol (ask price for BUY, bid price for SELL).

**Endpoint**: `http://localhost:8081/api/v1/coins/aggregated-price?symbol={symbol}&type={type}`

**Test Cases**:

#### 2.1 Buy Price for ETHUSDT

**Request**:

```bash
curl --location 'http://localhost:8081/api/v1/coins/aggregated-price?symbol=ETHUSDT&type=BUY' \
--header 'Authorization: Bearer xxxxx'
```

**Expected Response**:

```json
{
  "status": 200,
  "message": "Aggregated price retrieved successfully",
  "data": 2080.31
  // Example ask price
}
```

**Verification**: Note the ask price for use in Step 3.

#### 2.2 Sell Price for BTCUSDT

**Request**:

```bash
curl --location 'http://localhost:8081/api/v1/coins/aggregated-price?symbol=BTCUSDT&type=SELL' \
--header 'Authorization: Bearer xxxxx'
```

**Expected Response**:

```json
{
  "status": 200,
  "message": "Aggregated price retrieved successfully",
  "data": 87386.50
  // Example bid price
}
```

**Verification**: Note the bid price.

#### 2.3 Invalid Symbol

**Request**:

```bash
curl --location 'http://localhost:8081/api/v1/coins/aggregated-price?symbol=XRPUSDT&type=SELL' \
--header 'Authorization: Bearer xxxxx'
```

**Expected Response**:

```json
{
  "status": 400,
  "message": "Failed to retrieve aggregated price: No price data for symbol: XRPUSDT"
}
```

**Verification**: Confirm error for unsupported pair.

### Step 3: Trade Crypto (Task 3)

**API**: `POST /api/v1/trading/trade`

**Description**: Allows users to buy or sell ETHUSDT/BTCUSDT using the latest aggregated price. Validates price within
1% threshold.

**Endpoint**: `http://localhost:8081/api/v1/trading/trade`

**Test Cases**:

#### 3.1 Buy 1.5 ETH

**Get Ask Price**: From Step 2.1 (e.g.,2080.31).

**Request**:

```bash
curl --location 'http://localhost:8081/api/v1/trading/trade' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxxx' \
--data '{
  "symbol": "ETHUSDT",
  "amount": "1.5",
  "tradeType": "BUY",
  "price": "2080.31"
}'
```

**Expected Response**:

```json
{
  "status": 201,
  "message": "Buy order completed successfully!",
  "data": {
    "orderId": 1,
    "status": "SUCCESS",
    "message": "BUY order completed successfully"
  }
}
```

**Verification**: Proceed to Step 4 to check wallet balance.

#### 3.2 Sell 1.0 ETH

**Get Bid Price**: `curl "http://localhost:8081/api/v1/coins/aggregated-price?symbol=ETHUSDT&type=SELL"` (e.g.,2012.90).

**Request**:

```bash
curl --location 'http://localhost:8081/api/v1/trading/trade' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxxx' \
--data '{
  "symbol": "ETHUSDT",
  "amount": "1.0",
  "tradeType": "SELL",
  "price": "2081.53"
}'
```

**Expected Response**:

```json
{
  "status": 201,
  "message": "Sell order completed successfully!",
  "data": {
    "orderId": 2,
    "status": "SUCCESS",
    "message": "SELL order completed successfully"
  }
}
```

**Verification**: Check wallet balance (Step 4) and transactions (Step 5).

#### 3.3 Insufficient Funds

**Request**: Attempt to buy 20 ETH (30 * 2080.31 = 62409.3 > 50,000 USDT).

```bash
curl --location 'http://localhost:8081/api/v1/trading/trade' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxxx' \
--data '{
  "symbol": "ETHUSDT",
  "amount": "30",
  "tradeType": "BUY",
  "price": "2080.31"
}'
```

**Expected Response**:

```json
{
  "status": 400,
  "message": "Insufficient funds for this transaction!"
}
```

#### 3.4 Price Mismatch

**Request**:

```bash
curl --location 'http://localhost:8081/api/v1/trading/trade' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer xxxxx' \
--data '{
  "symbol": "ETHUSDT",
  "amount": "30",
  "tradeType": "BUY",
  "price": "1080.31"
}'
```

**Expected Response**:

```json
{
  "status": 400,
  "message": "Price has changed significantly. Please retry with the updated price: 2081.03"
}
```

### Step 4: Retrieve Wallet Balance (Task 4)

**Endpoint**: `http://localhost:8081/api/v1/trading/wallet`
**Request**: Before any trades.

```bash
curl "http://localhost:8081/api/v1/trading/wallet"
```

**Expected Response**:

```json
{
  "status": 200,
  "message": "Get wallet balance successfully!",
  "data": {
    "currency": null,
    "balance": "48968.63"
  }
}
```

### Step 5: Retrieve Trading History (Task 5)

**Description**: Retrieves the user's trading history.

**Endpoint**: `http://localhost:8081/api/v1/trading/transactions`

**Request**: Before any trades.

```bash
curl --location 'http://localhost:8081/api/v1/trading/transactions?page=0&size=10' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOltdLCJzdWIiOiJ0ZXN0LXVzZXItMSIsImlhdCI6MTc0MjgyNjAyMywiZXhwIjoxNzQyODYyMDIzfQ.Dqmoosa1QvbAGxL_qIBZxazAOx-eI7Pcc_xfybsOW2E'
```

**Expected Response**:

```json
{
  "status": 200,
  "message": "Get transactions successfully!",
  "data": {
    "pageNo": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 6,
    "data": [
      {
        "id": 1,
        "cryptoPair": "ETHUSDT",
        "type": "BUY",
        "amount": 1.50,
        "price": 2076.36,
        "timestamp": "2025-03-24T20:46:34.473261",
        "coinSymbol": "ETHUSDT",
        "walletCurrency": null
      },
      {
        "id": 2,
        "cryptoPair": "ETHUSDT",
        "type": "SELL",
        "amount": 1.00,
        "price": 2083.17,
        "timestamp": "2025-03-24T20:47:27.343863",
        "coinSymbol": "ETHUSDT",
        "walletCurrency": null
      },
      {
        "id": 3,
        "cryptoPair": "ETHUSDT",
        "type": "BUY",
        "amount": 3.00,
        "price": 2086.37,
        "timestamp": "2025-03-24T21:28:57.590357",
        "coinSymbol": "ETHUSDT",
        "walletCurrency": null
      }
    ]
  }
}
```

---

## Author: Nguyen Hoang An

### 🥰 Thank you to my colleagues who took the time to look at my test, thank you for your valuable time.
