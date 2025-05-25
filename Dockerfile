FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Cambiamos la ruta porque el build se hace desde ./pokedex-api
COPY pokedex-api/target/pokedex-api-0.0.1-SNAPSHOT.jar pokedex-api-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "pokedex-api-0.0.1-SNAPSHOT.jar"]
