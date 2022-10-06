# Exchange Simulator - Spring Boot, RestAPI, Websockets Stream

This is a simulated exchange that accepts market and limit order types.

RestAPI is used to submit order requests, get order status, get static data, and market data.

ًWebsocket stream is used to submit order requests,  receive order and trade events. This is usually much faster than using a RestAPI

Market data is also fed via websockets

Note: This is a work in progress

[github.com/ismailfer/exchange-simulator](https://github.com/ismailfer/exchange-simulator)

### Objectives
- Used for testing purposes of algorithmic trading systems

## Main features:
- Rest API for order placement, status request, market data requests
- Native websockets API using Jetty Server
- Orders are placed via a websocket connection
- Order and trade events are fed through the websocket connection
- Market data is fed via websockets
- Market order type
- Limit order type
- Any instrument is supported
- Order book can be cleared via a command in RestAPI or websocket


## Architecture
- Spring Boot framework 
- Rest API
- Native WebSockets, using Jetty Server embedded in the spring boot app
- JSP/web3 to show market data, and orders in the simulator
- Jackson json marshalling/unmarshalling
- Reactive programming architecture
- Concurrent / Multi-threading environment
- Zero Garbage Collection
- Java 17. However the code base can also run in Java 11 and above
- Maven build

## RestAPI and Websockets API - Postman Collections

### API URLs

URL for Order Placement Rest API:
- [http://localhost:9080/api/v1/order](http://localhost:9080/api/v1/order)

URL for Order Cancellation Rest API:
- [http://localhost:9080/api/v1/cancelOrder](http://localhost:9080/api/v1/cancelOrder)

URL for Order Stream Websocket:
- [ws://localhost:9081/api/v1/order](ws://localhost:9081/api/v1/order)

URL for Top Of Book Stream Websocket:
- [ws://localhost:9081/api/v1/tob](ws://localhost:9081/api/v1/tob)

URL for Order Book Websocket:
- [ws://localhost:9081/api/v1/orderBook](ws://localhost:9081/api/v1/orderBook)


### API Test Cases

Test cases of RestAPIs and Websocket APIs are found in the following Postman collection

- [Postman - Exchange Simulator Workspace](https://www.postman.com/restless-satellite-277762/workspace/exchange-simulator-workspace)

## Building the project

The build process will produce a war file (instead of a jar file); because there is an embedded jetty server; and JSP pages within the project.

To build the project; run the command:

```text
mvn clean package
```

## Running the Application

To run the application from the war file:

```text
java -jar target\exchange-simulator-1.0.war
```

To run the application with ZeroG garbage collector

```text
java -XX:+UseZGC -Xmx2g -jar target\exchange-simulator-1.0.war
```

The app doesn't require much memory; unless you want to load it with thousands of orders



## Deployments
- Docker
- Kubernetes

### Docker

This application is dockerized with Dockerfile

Latest docker image is available at my dockerhub; built on Alpine linux distribution; and open jdk17, running on port 9080 and 9081

- [hub.docker.com/r/ismailfer/exchange-simulator](https://hub.docker.com/r/ismailfer/exchange-simulator)


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





