# Usa una imagen base de Java
FROM openjdk:11-jre-slim

# Añade un argumento para especificar el archivo JAR
ARG JAR_FILE=target/*.jar

# Copia el archivo JAR a la imagen de Docker
COPY ${JAR_FILE} app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "/app.jar"]
