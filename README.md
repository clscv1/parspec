Example API CURL:

1. Create Order:
curl --location 'http://13.60.40.37:4567/order/accept' \
--header 'Content-Type: application/json' \
--data '{
    "userId": "123",
    "itemIds": ["1","2","3"],
    "totalAmount": 100
   }'
2. Get Order Status
   curl --location --request GET 'http://13.60.40.37:4567/order/status/01JNQ12BFFVNJ61359YKW1J2DA' \
--header 'Content-Type: application/json' \
--data ''

3. Get Metrics:
curl --location 'http://13.60.40.37:4567/metrics' \
--data ''

# Design Decisions

- Order system consists of 3 components: Rest API Handler,PostGres db for persistent storage and  workerService to process orders asynchronously.
- **In memory queue:** Used for simple and fast processing.As scale increases,i would use scalable solutions like SNS > RabbitMQ > Kafka in the order of scale.
- **Worker Service:** Have kept it on same machine as our app-engine(api-handler) for now.As scale increases it can be made as a seperate Service.Have used **Executors** framework to take advantage of concurrent processing.
- SparkJava Framework: Have used sparkJava as it is a very lightweight framework and perfect for our usecase.As project grows we can add things like Google Guice for dependency Injection,ORM for object mapping and so on.
- Have designed classes to be thread-safe through ConcurrentDataStructures,Atomic values provided by Java.
- Have made MetricService as singleton, as only single instance of Metric is required for whole lifeCycle of app

# Assumptions made during development

One assumption i have made is i have focussed more on low level side of things as scale mentioned was limited.
On distributed-architecture of things,I have kept things simple for now.We can discuss on high level design in next rounds.
