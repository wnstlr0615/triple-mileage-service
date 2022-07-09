FROM openjdk:11-jdk

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} triple-mileage-service.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev",
"-Dspring.config.location=classpath:/application.yml",
"-Dspring.batch.job.names=sampleDataInitJob",
"-jar", "/triple-mileage-service.jar"]