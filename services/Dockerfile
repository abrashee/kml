FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
ARG MODULE
COPY pom.xml .
COPY common ./common
COPY api-gateway ./api-gateway
COPY user-service ./user-service
COPY order-service ./order-service
COPY inventory-service ./inventory-service
COPY warehouse-service ./warehouse-service
COPY shipment-service ./shipment-service
RUN mvn -pl ${MODULE} -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine
RUN apk upgrade --no-cache \
    && addgroup -S app \
    && adduser -S -G app app
WORKDIR /app
ARG MODULE
COPY --from=build /workspace/${MODULE}/target/*.jar app.jar
RUN chown -R app:app /app
USER app
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=25 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java","-jar","/app/app.jar"]