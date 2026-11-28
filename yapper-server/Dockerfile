# Multi-stage build

#------------------------------------------------------------#
# 1. stage: Build and package the yapper server
#------------------------------------------------------------#
FROM maven:3.10-eclipse-temurin-21-noble AS build

WORKDIR /build

# dependencies
COPY pom.xml ./
COPY yapper-common/pom.xml yapper-common/
COPY yapper-server/pom.xml yapper-server/
COPY yapper-client/pom.xml yapper-client/

RUN mvn -B -pl yapper-server -am dependency:go-offline

# source code
COPY yapper-common/src/main yapper-common/src/main
COPY yapper-server/src/main yapper-server/src/main

# package
RUN mvn -B -pl yapper-server -am package -Dmaven.test.skip=true

#------------------------------------------------------------#
# 2. stage: Run the packaged yapper server from stage 1
#------------------------------------------------------------#
FROM eclipse-temurin:21-jre-noble
WORKDIR /app

# run as an unprivileged user, not root
RUN groupadd --system yapper && useradd --system --gid yapper yapper

# copy the jar from stage 1
COPY --from=build /build/yapper-server/target/yapper-server.jar app.jar

USER yapper

# on container start, run the jar (start the server)
ENTRYPOINT ["java", "-jar", "app.jar"]