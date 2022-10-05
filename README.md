# Exchange Simulator - Spring Boot, RestAPI, Websockets Feeds

This is a simulated exchange that accepts market and limit order types.

Users can submit orders using a Websocket feed; and receive order and trade events via Websockets too

Market data is also fed via websockets

Note: This is a work in progress

[github.com/ismailfer/exchange-simulator](https://github.com/ismailfer/exchange-simulator)

#### Objectives
- Used for testing purposes of algorithmic trading systems

#### Main features:
- Rest API for order placement, status request, market data requests
- Native websockets API using Jetty Server
- Orders are placed via a websocket connection
- Order and trade events are fed through the websocket connection
- Market data is fed via websockets
- Market order type
- Limit order type
- Any instrument is supported
- Order book can be cleared via a command in RestAPI or websocket


#### Architecture
- Spring Boot framework for the execution client webpage
- Rest API
- Native WebSockets, using Jetty Server embedded in the spring boot app
- JSP/web3 to show market data, and orders in the simulator
- Uses Jackson RestAPI client
- Reactive programming architecture
- Concurrent / Multi-threading environment
- Zero Garbage Collection environment
- Java 17. However the code base can also run in Java 11 and above

#### API Postman Collection

#### Java 17

To run the application from the war file:

```text
java -jar target\exchange-simulator-springboot-1.0.war
```

To run the application with ZeroG garbage collector

```text
java -XX:+UseZGC -Xmx2g -jar target\exchange-simulator-springboot-1.0.war
```

## Deployments
- Docker
- Kubernetes

### Docker

This application is dockerized with Dockerfile

Latest docker image is available at my dockerhub; built with jdk17, running on port 9080

[hub.docker.com/r/ismailfer/exchange-simulator](https://hub.docker.com/r/ismailfer/exchange-simulator)


To build a docker image:

```text
docker build --tag exchange-simulator:1.0 .
```

To run the docker image:

```text
docker run --name exchange-simulator -d -p 9080:9080 -p 9081:9081 exchange-simulator:1.0
```

To tail the logs on the running docker image:

```text
docker logs -f exchange-simulator
```





