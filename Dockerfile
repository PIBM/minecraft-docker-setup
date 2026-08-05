# Build stage
FROM gradle:jdk25 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Output stage
FROM alpine:latest
WORKDIR /output
COPY --from=builder /app/build/libs/usersettings.jar /output/usersettings.jar
CMD ["cp", "/output/usersettings.jar", "/plugins/usersettings.jar"]
