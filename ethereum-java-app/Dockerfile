FROM ubuntu:20.04 AS eth-build
RUN mkdir /usr/app
WORKDIR  /usr/app
ADD . /usr/app
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    rm -rf /var/lib/apt/lists/*
RUN add-apt-repository ppa:ethereum/ethereum
RUN apt-get update
RUN apt-get install -y solc
RUN apt-get install -y openjdk-8-jdk
RUN apt-get install -y maven
RUN mvn clean install -U
RUN mvn clean package

FROM openjdk:8-jdk-alpine
WORKDIR /usr/app/target
COPY --from=eth-build /usr/app/target/*.jar ./
#ARG JAR_FILE=target/ethereum-java-app-1.0-SNAPSHOT-exec.jar
#COPY ${JAR_FILE} ethereum-java-app-1.0-SNAPSHOT-exec.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","ethereum-java-app-1.0-SNAPSHOT-exec.jar"]
