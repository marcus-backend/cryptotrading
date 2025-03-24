# Crypto Trading System Testing Guide

This guide provides detailed steps to test the crypto trading system built with Spring Boot and an in-memory H2 database. The system supports Ethereum (ETHUSDT) and Bitcoin (BTCUSDT) trading pairs, with price aggregation from Binance and Huobi, and includes APIs for trading, wallet balance, and transaction history.
**Swagger for testing:* http://localhost:8081/swagger-ui/index.html#/
## Prerequisites
- **Environment**: Spring Boot application with H2 database running.
- **Start Command**: Run `CryptoTradingApplication.java` (e.g., via IDE or `mvn spring-boot:run`).
- **Tools**: Use curl, Postman, or any HTTP client.
- **API Prefix**: Assumes `${api.prefix}` is `/api` (adjust if different).
- **H2 Console**: Optional, access at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:cryptodb`, username: `sa`, password: empty).
- **Authentication**: Assumes user "test-user-1" is authenticated (mocked via `auth.getUsername()`).

## Initial Setup
The application initializes mock data on startup:
- **User**: "test-user-1" with active status.
- **Wallets**:
    - 50,000 USDT
- **Coins**: ETHUSDT and BTCUSDT with initial prices of 0 (updated by scheduler).

## Testing Steps

### Step 1: Verify Price Aggregation (Task 1)
**Description**: A scheduler runs every 10 seconds to fetch and aggregate prices from Binance (`https://api.binance.com/api/v3/ticker/bookTicker`) and Huobi (`https://api.huobi.pro/market/tickers`), storing the best bid (highest) and ask (lowest) prices in `tbl_coins`.

**Test Method**: No direct API; verify via logs or H2 database.

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
curl "http://localhost:8081/api/v1/coins/aggregated-price?symbol=ETHUSDT&type=BUY"
```
**Expected Response**:
```json
{
  "status": 200,
  "message": "Aggregated price retrieved successfully",
  "data": 2004.04  // Example ask price
}
```
**Verification**: Note the ask price for use in Step 3.

#### 2.2 Sell Price for BTCUSDT
**Request**:
```bash
curl "http://localhost:8081/api/v1/coins/aggregated-price?symbol=BTCUSDT&type=SELL"
```
**Expected Response**:
```json
{
  "status": 200,
  "message": "Aggregated price retrieved successfully",
  "data": 86224.73  // Example bid price
}
```
**Verification**: Note the bid price.

#### 2.3 Invalid Symbol
**Request**:
```bash
curl "http://localhost:8081/api/v1/coins/aggregated-price?symbol=XRPUSDT&type=BUY"
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
**API**: `POST /api/trading/trade`

**Description**: Allows users to buy or sell ETHUSDT/BTCUSDT using the latest aggregated price. Validates price within 1% threshold.

**Endpoint**: `http://localhost:8081/api/v1/trading/trade`

**Test Cases**:
#### 3.1 Buy 1.5 ETH
**Get Ask Price**: From Step 2.1 (e.g., 2007.44).

**Request**:
```bash
curl --location 'http://localhost:8081/api/v1/trading/trade' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOltdLCJzdWIiOiJ0ZXN0LXVzZXItMSIsImlhdCI6MTc0Mjc4MTgxNCwiZXhwIjoxNzQyODE3ODE0fQ.hpRN6vA_XcL-TdVn5e1YsLbPaicCvXPEgMaKaO5uuzs' \
--data '{
  "symbol": "ETHUSDT",
  "amount": "1.5",
  "tradeType": "BUY",
  "price": "2007.44"
}'
```

**Expected Response**:
```json
{
  "status": 201,
  "message": "Buy order completed successfully",
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
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOltdLCJzdWIiOiJ0ZXN0LXVzZXItMSIsImlhdCI6MTc0Mjc4MTgxNCwiZXhwIjoxNzQyODE3ODE0fQ.hpRN6vA_XcL-TdVn5e1YsLbPaicCvXPEgMaKaO5uuzs' \
--data '{
  "symbol": "ETHUSDT",
  "amount": "1.5",
  "tradeType": "SELL",
  "price": "2012.90"
}'
```

**Expected Response**:
```json
{
  "status": 201,
  "message": "Sell order completed successfully",
  "data": {
    "orderId": 2,
    "status": "SUCCESS",
    "message": "SELL order completed successfully"
  }
}
```
**Verification**: Check wallet balance (Step 4) and transactions (Step 5).

#### 3.3 Insufficient Funds
**Request**: Attempt to buy 20 ETH (30 * 3005.50 = 60223.2 > 50,000 USDT).
```bash
curl --location 'http://localhost:8081/api/v1/trading/trade' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOltdLCJzdWIiOiJ0ZXN0LXVzZXItMSIsImlhdCI6MTc0Mjc4MTgxNCwiZXhwIjoxNzQyODE3ODE0fQ.hpRN6vA_XcL-TdVn5e1YsLbPaicCvXPEgMaKaO5uuzs' \
--data '{
  "symbol": "ETHUSDT",
  "amount": "30",
  "tradeType": "BUY",
  "price": "2007.44"
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
**Request**: Use outdated price (e.g., 107.44 vs. 2009.11).
```bash
curl --location 'http://localhost:8081/api/v1/trading/trade' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOltdLCJzdWIiOiJ0ZXN0LXVzZXItMSIsImlhdCI6MTc0Mjc4MTgxNCwiZXhwIjoxNzQyODE3ODE0fQ.hpRN6vA_XcL-TdVn5e1YsLbPaicCvXPEgMaKaO5uuzs' \
--data '{
  "symbol": "ETHUSDT",
  "amount": "1",
  "tradeType": "BUY",
  "price": "107.44"
}'
```
**Expected Response**:
```json
{
  "status": 400,
  "message": "Price has changed significantly. Please retry with the updated price: 2009.11"
}
```












### Step 4: Retrieve Wallet Balance (Task 4)
**API**: `GET /api/trading/wallet`

**Description**: Retrieves the user's wallet balances for USDT, ETH, and BTC.

**Endpoint**: `http://localhost:8081/api/v1/trading/wallet`

**Test Cases**:
#### 4.1 Initial Balance
**Request**: Before any trades.
```bash
curl "http://localhost:8081/api/v1/trading/wallet"
```
**Expected Response**:
```json
{
  "status": 200,
  "message": "Wallet balance retrieved successfully",
  "data": [
    {"currency": "USDT", "balance": "50000"},
    {"currency": "ETH", "balance": "0"},
    {"currency": "BTC", "balance": "0"}
  ]
}
```

#### 4.2 After Buying 1.5 ETH
**Request**: After Step 3.1.
```bash
curl "http://localhost:8081/api/v1/trading/wallet"
```
**Expected Response**:
```json
{
  "status": 200,
  "message": "Wallet balance retrieved successfully",
  "data": [
    {"currency": "USDT", "balance": "45491.75"},  // 50,000 - (1.5 * 3005.50)
    {"currency": "ETH", "balance": "1.5"},
    {"currency": "BTC", "balance": "0"}
  ]
}
```

#### 4.3 After Selling 1.0 ETH
**Request**: After Step 3.2.
```bash
curl "http://localhost:8081/api/v1/trading/wallet"
```
**Expected Response**:
```json
{
  "status": 200,
  "message": "Wallet balance retrieved successfully",
  "data": [
    {"currency": "USDT", "balance": "48491.75"},  // 45491.75 + 3000
    {"currency": "ETH", "balance": "0.5"},
    {"currency": "BTC", "balance": "0"}
  ]
}
```

### Step 5: Retrieve Trading History (Task 5)
**API**: `GET /api/trading/transactions`

**Description**: Retrieves the user's trading history.

**Endpoint**: `http://localhost:8081/api/v1/trading/transactions`

**Test Cases**:
#### 5.1 Initial History
**Request**: Before any trades.
```bash
curl "http://localhost:8081/api/v1/trading/transactions"
```
**Expected Response**:
```json
{
  "status": 200,
  "message": "Transactions retrieved successfully",
  "data": []
}
```

#### 5.2 After Buy and Sell
**Request**: After Steps 3.1 and 3.2.
```bash
curl "http://localhost:8081/api/v1/trading/transactions"
```
**Expected Response**:
```json
{
  "status": 200,
  "message": "Transactions retrieved successfully",
  "data": [
    {
      "id": 1,
      "cryptoPair": "ETHUSDT",
      "type": "BUY",
      "amount": "1.5",
      "price": "3005.50",
      "timestamp": "2025-03-23T10:00:00"
    },
    {
      "id": 2,
      "cryptoPair": "ETHUSDT",
      "type": "SELL",
      "amount": "1.0",
      "price": "3000.00",
      "timestamp": "2025-03-23T10:01:00"
    }
  ]
}
```

## Verification Checklist
- **Price Aggregation**: Prices update every 10 seconds in `tbl_coins`.
- **Get Price**: Returns correct ask (BUY) and bid (SELL) prices.
- **Trade**:
    - Buy reduces USDT, increases ETH/BTC.
    - Sell reduces ETH/BTC, increases USDT.
    - Fails on insufficient funds or price mismatch.
- **Wallet Balance**: Reflects trades accurately.
- **Transaction History**: Lists all trades with correct details.

## Notes
- **Timestamp**: Actual timestamps will vary; adjust expectations based on test time.
- **Price Values**: Prices depend on Binance/Huobi data; use Step 2 to get current values before trading.
- **Error Handling**: Test edge cases (invalid symbols, insufficient balance) to ensure robustness.

Save this as `TESTING_GUIDE.md` and follow the steps to validate your system! Let me know if you need adjustments or additional test cases.