# Этап сборки
FROM maven:3.9.4-eclipse-temurin-21 as build

COPY /src /app/src
COPY /pom.xml /app/pom.xml

WORKDIR /app

RUN mvn clean package -DskipTests

# Этап запуска
FROM bellsoft/liberica-openjdk-debian:21

RUN adduser --system spring-boot && addgroup --system spring-boot && adduser spring-boot spring-boot

USER spring-boot

WORKDIR /app

COPY --from=build /app/target/SberProject-0.0.1-SNAPSHOT.jar ./application.jar
COPY --from=build /app/src/main/resources /app/resources

ENTRYPOINT ["java", "-jar", "./application.jar"]
