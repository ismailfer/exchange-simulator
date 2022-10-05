# Exchange Simulator - Spring Boot, RestAPI, Websockets Feeds

This is a simulated exchange that accepts market and limit order types.

Users can submit orders using a Websocket feed; and receive order and trade events via Websockets too

Market data is also fed via websockets

Note: This is a work in progress

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

#### API Postman Collection

## Deployments
- Docker
- Kubernetes

### Docker

This application is dockerized with Dockerfile

Latest docker image is available at my dockerhub; built with jdk17, running on port 9080

[hub.docker.com/r/ismailfer/exchange-siulator](https://hub.docker.com/r/ismailfer/exchange-siulator)


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





