# ---------- STAGE 1: Build (Maven embarqué) ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copier les descripteurs d'abord pour tirer profit du cache Docker
COPY pom.xml .
RUN mvn -q -e -B -DskipTests dependency:go-offline

# Copier le code et builder
COPY src ./src
RUN mvn -q -e -B -DskipTests package

# ---------- STAGE 2: Run (JRE léger) ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# utilisateur non-root
RUN useradd -ms /bin/bash appuser
USER appuser

# Copier le jar depuis l'étape de build
COPY --from=build /app/target/*.jar /app/app.jar

# Port d'écoute Spring Boot
EXPOSE 8080

# Variables d'env avec valeurs par défaut (override dans docker compose)
ENV SPRING_PROFILES_ACTIVE=prod \
    DB_URL=jdbc:postgresql://db:5432/mydb \
    DB_USER=myuser \
    DB_PASS=mypassword \
    JWT_SECRET=change-me-please

ENTRYPOINT ["java","-jar","/app/app.jar"]
