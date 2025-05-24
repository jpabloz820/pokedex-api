FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY pokedex-api/target/pokedex-api-0.0.1-SNAPSHOT.jar pokedex-api-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java","-jar","pokedex-api-0.0.1-SNAPSHOT.jar"]
