FROM openjdk:17-jdk-alpine
EXPOSE 9080
ARG JAR_FILE=target/exchange-simulator-1.0.war
ADD ${JAR_FILE} exchange-simulator.war
ENTRYPOINT ["java","-jar","exchange-simulator.war",""]