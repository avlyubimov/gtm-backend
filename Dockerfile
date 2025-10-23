# ---------- build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -U -DskipTests=true dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests=true clean package

# ---------- run ----------
FROM eclipse-temurin:21-jre
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC" \
    SPRING_PROFILES_ACTIVE=dev \
    SERVER_PORT=8080
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=20s --timeout=5s --retries=5 CMD curl -fsS http://localhost:${SERVER_PORT}/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --server.port=${SERVER_PORT}"]