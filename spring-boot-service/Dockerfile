FROM gradle:jdk21-alpine
WORKDIR /opt
COPY . .
RUN gradle wrapper
EXPOSE 8080
CMD ["./gradlew", "bootRun", "--rerun-tasks"]