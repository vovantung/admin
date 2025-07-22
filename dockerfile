FROM eclipse-temurin:17-jdk-alpine
COPY main-app/target/admin.jar /admin.jar
ENTRYPOINT ["java","-jar","/admin.jar"]
EXPOSE 8080