#!/bin/bash

# Authentication API Test Script
# Make sure the backend is running on http://localhost:7777

BASE_URL="http://localhost:7777/api/v1"

echo "=========================================="
echo "Authentication API Test Script"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Health Check
echo -e "${YELLOW}Test 1: Health Check${NC}"
echo "GET $BASE_URL/health"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/health")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')
echo "Status Code: $http_code"
echo "Response: $body"
echo ""

if [ "$http_code" != "200" ]; then
    echo -e "${RED}❌ Backend is not running!${NC}"
    echo "Please start the backend with: cd motive-crew-ws && ./gradlew bootRun"
    exit 1
fi

echo -e "${GREEN}✅ Backend is running!${NC}"
echo ""

# Test 2: Signup
echo -e "${YELLOW}Test 2: User Signup${NC}"
echo "POST $BASE_URL/auth/signup"
signup_response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test'$(date +%s)'@example.com",
    "phone": "1234567890",
    "password": "password123",
    "position": "Developer",
    "role": "member"
  }')
signup_http_code=$(echo "$signup_response" | tail -n1)
signup_body=$(echo "$signup_response" | sed '$d')
echo "Status Code: $signup_http_code"
echo "Response: $signup_body"
echo ""

# Extract token if signup was successful
TOKEN=""
if [ "$signup_http_code" == "201" ] || [ "$signup_http_code" == "200" ]; then
    TOKEN=$(echo "$signup_body" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    echo -e "${GREEN}✅ Signup successful!${NC}"
    echo "Token: ${TOKEN:0:50}..."
else
    echo -e "${RED}❌ Signup failed!${NC}"
    # Try login with existing user instead
    echo "Trying to login with existing user..."
    login_response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
      -H "Content-Type: application/json" \
      -d '{
        "username": "admin@example.com",
        "password": "admin123"
      }')
    login_http_code=$(echo "$login_response" | tail -n1)
    login_body=$(echo "$login_response" | sed '$d')
    if [ "$login_http_code" == "200" ]; then
        TOKEN=$(echo "$login_body" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
        echo -e "${GREEN}✅ Login successful!${NC}"
        echo "Token: ${TOKEN:0:50}..."
    fi
fi
echo ""

# Test 3: Login
echo -e "${YELLOW}Test 3: User Login${NC}"
echo "POST $BASE_URL/auth/login"
login_response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "password123"
  }')
login_http_code=$(echo "$login_response" | tail -n1)
login_body=$(echo "$login_response" | sed '$d')
echo "Status Code: $login_http_code"
echo "Response: $login_body"
echo ""

if [ "$login_http_code" == "200" ]; then
    TOKEN=$(echo "$login_body" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    echo -e "${GREEN}✅ Login successful!${NC}"
    echo "Token: ${TOKEN:0:50}..."
else
    echo -e "${RED}❌ Login failed!${NC}"
    echo "Note: This is expected if the user doesn't exist"
fi
echo ""

# Test 4: Get Current User (if we have a token)
if [ -n "$TOKEN" ]; then
    echo -e "${YELLOW}Test 4: Get Current User${NC}"
    echo "GET $BASE_URL/auth/me"
    me_response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/auth/me" \
      -H "Authorization: Bearer $TOKEN")
    me_http_code=$(echo "$me_response" | tail -n1)
    me_body=$(echo "$me_response" | sed '$d')
    echo "Status Code: $me_http_code"
    echo "Response: $me_body"
    echo ""
    
    if [ "$me_http_code" == "200" ]; then
        echo -e "${GREEN}✅ Get current user successful!${NC}"
    else
        echo -e "${RED}❌ Get current user failed!${NC}"
    fi
    echo ""
fi

# Test 5: Change Password (if we have a token)
if [ -n "$TOKEN" ]; then
    echo -e "${YELLOW}Test 5: Change Password${NC}"
    echo "PUT $BASE_URL/auth/password"
    change_pwd_response=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/auth/password" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "currentPassword": "password123",
        "newPassword": "newpassword123",
        "confirmPassword": "newpassword123"
      }')
    change_pwd_http_code=$(echo "$change_pwd_response" | tail -n1)
    change_pwd_body=$(echo "$change_pwd_response" | sed '$d')
    echo "Status Code: $change_pwd_http_code"
    echo "Response: $change_pwd_body"
    echo ""
    
    if [ "$change_pwd_http_code" == "200" ]; then
        echo -e "${GREEN}✅ Change password successful!${NC}"
    else
        echo -e "${RED}❌ Change password failed!${NC}"
    fi
    echo ""
fi

echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo -e "${GREEN}✅ Health Check: OK${NC}"
echo "All tests completed!"


