FROM maven:3.9.5 as builder

WORKDIR /app

COPY pom.xml .

RUN mvn clean install -DskipTests

COPY src src

COPY target/marketplace-0.0.1-SNAPSHOT.war /app/marketplace.war


FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
