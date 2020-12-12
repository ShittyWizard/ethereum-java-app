FROM maven as builer
RUN mkdir /usr/app
WORKDIR  /usr/app
ADD . /usr/app
RUN sudo add-apt-repository ppa:ethereum/ethereum
RUN sudo apt-get update
RUN sudo apt-get install solc
RUN mvn clean install -U
RUN mvn package

FROM openjdk:8-jdk-alpine
COPY --from=builder /usr/app/target/*.jar ./target
ARG JAR_FILE=target/ethereum-java-app-1.0-SNAPSHOT-exec.jar
WORKDIR /opt/app
COPY ${JAR_FILE} ethereum-java-app-1.0-SNAPSHOT-exec.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","ethereum-java-app-1.0-SNAPSHOT-exec.jar"]
