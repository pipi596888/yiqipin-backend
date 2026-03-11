#!/bin/bash

# yiqipin-backend API Test Script
# Usage: ./api-test.sh [base_url]
# Default base_url: http://localhost:8080

BASE_URL=${1:-http://localhost:8080}
TOKEN=""
USER_ID=""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "yiqipin Backend API Test Suite"
echo "Base URL: $BASE_URL"
echo "=========================================="

# Test function
test_api() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4

    echo -n "Testing: $description ... "

    if [ "$method" = "GET" ]; then
        if [ -n "$TOKEN" ]; then
            response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL$endpoint" -H "Authorization: Bearer $TOKEN")
        else
            response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL$endpoint")
        fi
    elif [ "$method" = "POST" ]; then
        if [ -n "$TOKEN" ]; then
            response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" -H "Content-Type: application/json" -d "$data")
        fi
    elif [ "$method" = "PUT" ]; then
        response=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL$endpoint" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "$data")
    elif [ "$method" = "DELETE" ]; then
        response=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL$endpoint" -H "Authorization: Bearer $TOKEN")
    fi

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}PASS${NC} (HTTP $http_code)"
        return 0
    else
        echo -e "${RED}FAIL${NC} (HTTP $http_code)"
        echo "  Response: $body"
        return 1
    fi
}

# ==========================================
# Test Suite 1: Authentication
# ==========================================
echo ""
echo "=========================================="
echo "Test Suite 1: Authentication"
echo "=========================================="

# Register a new user
test_api "POST" "/api/register" "Register new user" '{"username":"testuser_api","password":"test123456","phone":"13800138000","email":"api@test.com"}'

# Login
echo -n "Testing: Login and get token ... "
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/login" -H "Content-Type: application/json" -d '{"username":"testuser_api","password":"test123456"}')
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
    TOKEN=$(echo "$body" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    USER_ID=$(echo "$body" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
    echo -e "${GREEN}PASS${NC} (HTTP $http_code)"
    echo "  Token: ${TOKEN:0:20}..."
else
    echo -e "${RED}FAIL${NC} (HTTP $http_code)"
    echo "  Response: $body"
    # Try with a pre-existing user if registration failed
    TOKEN="mock-token-for-testing"
fi

# ==========================================
# Test Suite 2: Products
# ==========================================
echo ""
echo "=========================================="
echo "Test Suite 2: Products"
echo "=========================================="

test_api "GET" "/api/products" "Get product list"
test_api "GET" "/api/product/1" "Get product detail (ID=1)"

# ==========================================
# Test Suite 3: Cart
# ==========================================
echo ""
echo "=========================================="
echo "Test Suite 3: Cart"
echo "=========================================="

if [ -n "$TOKEN" ]; then
    test_api "GET" "/api/cart/list" "Get cart list"
    test_api "POST" "/api/cart/add" "Add item to cart" '{"productId":1,"quantity":2}'
    test_api "POST" "/api/cart/update?productId=1&quantity=3" "Update cart quantity"
    test_api "POST" "/api/cart/remove?productId=1" "Remove item from cart"
else
    echo -e "${YELLOW}SKIP${NC} - Cart tests require authentication"
fi

# ==========================================
# Test Suite 4: Orders
# ==========================================
echo ""
echo "=========================================="
echo "Test Suite 4: Orders"
echo "=========================================="

if [ -n "$TOKEN" ]; then
    test_api "GET" "/api/order/list" "Get order list"
    test_api "GET" "/api/order/ORDER123" "Get order detail"
else
    echo -e "${YELLOW}SKIP${NC} - Order tests require authentication"
fi

# ==========================================
# Test Suite 5: User Profile
# ==========================================
echo ""
echo "=========================================="
echo "Test Suite 5: User Profile"
echo "=========================================="

if [ -n "$TOKEN" ]; then
    test_api "GET" "/api/user/profile" "Get user profile"
    test_api "GET" "/api/user/address" "Get user addresses"
else
    echo -e "${YELLOW}SKIP${NC} - User tests require authentication"
fi

# ==========================================
# Summary
# ==========================================
echo ""
echo "=========================================="
echo "API Test Complete"
echo "=========================================="
