FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY motive-crew-ws/ ./motive-crew-ws/

WORKDIR /app/motive-crew-ws

RUN chmod +x gradlew && ./gradlew build -x test

EXPOSE 8080

CMD ["./gradlew", "bootRun"]

