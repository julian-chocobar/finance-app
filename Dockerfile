# Utiliza OpenJDK 23 con una imagen ligera de Debian
FROM openjdk:23-jdk-slim

# Argumento para el nombre del JAR
ARG JAR_FILE=target/finance-app-0.0.1.jar

# Copiar el JAR al contenedor
COPY ${JAR_FILE} thames-app.jar

# Exponer el puerto en el que corre la aplicación
EXPOSE 8080

# Configurar el punto de entrada
ENTRYPOINT ["java", "-jar", "thames-app.jar"]
