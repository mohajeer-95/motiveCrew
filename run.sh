#!/bin/bash

# Motive Crew - Quick Start Script
# This script helps you run the Spring Boot application

echo "=========================================="
echo "Motive Crew - Spring Boot Application"
echo "=========================================="
echo ""

# Check Java version
echo "Checking Java version..."
java -version
echo ""

# Check MySQL
echo "Checking MySQL connection..."
mysql -u root -e "SELECT 1" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✓ MySQL is accessible"
else
    echo "⚠ MySQL connection failed. Please check:"
    echo "  1. MySQL is running"
    echo "  2. Username/password in application-dev.properties"
    echo ""
fi

# Create database if it doesn't exist
echo "Creating database if needed..."
mysql -u root -e "CREATE DATABASE IF NOT EXISTS wikidenia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✓ Database 'wikidenia' is ready"
else
    echo "⚠ Could not create database. Please create it manually:"
    echo "  mysql -u root -p"
    echo "  CREATE DATABASE wikidenia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    echo ""
fi

# Navigate to project directory
cd "$(dirname "$0")/motive-crew-ws"

echo ""
echo "Building project..."
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Build successful!"
    echo ""
    echo "Starting application..."
    echo "Application will run on: http://localhost:7777"
    echo "Press Ctrl+C to stop"
    echo ""
    ./gradlew bootRun
else
    echo ""
    echo "✗ Build failed. Please check the errors above."
    exit 1
fi

