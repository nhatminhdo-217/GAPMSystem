FROM eclipse-temurin:21-jdk AS buidler

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime

ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app

COPY --from=buidler /app/target/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
