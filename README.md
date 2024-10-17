# Apache Cassandra Drivers Best Practices and Examples
## Category
1. Retry and Idempotency

    [Java](./java/retry-and-idempotency) | [Python] | [Go] | [C#]

## How to run the examples
Ensure your docker daemon is running
```bash
cd docker
docker compose up
```
For Java examples, run the following command in the respective directory
```bash
mvn exec:java -Dexec.mainClass="com.datastax.examples.Main"
```