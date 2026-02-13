FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar arquivos de dependência
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Baixar dependências (cache em camada separada)
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte e compilar
COPY src src
RUN ./mvnw package -DskipTests -B

# Imagem final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Criar usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar artefato do build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
