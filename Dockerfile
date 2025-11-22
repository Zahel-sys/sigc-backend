### Dockerfile para backend Spring Boot
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
RUN chmod +x mvnw || true
COPY src ./src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=build /workspace/target/*.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
