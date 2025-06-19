FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /task

ENV DB_URL=""\
DB_USERNAME=""\
DB_PASSWORD=""

COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]