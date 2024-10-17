# Retry and Idempotency
[Manual for Retries](https://docs.datastax.com/en/developer/java-driver/4.17/manual/core/retries/index.html) | [Manual for Idempotency](https://docs.datastax.com/en/developer/java-driver/4.17/manual/core/idempotence/index.html)

A retry policy determines what to do when a request failed on a node: retry (same or other node), rethrow, or ignore.
In most cases, it is recommended to mark the idempotency of your statements and use the driver's default retry-policy settings.

You can mark the idempotency of your statement by `Statement.setIdempotent` or `StatementBuilder.setIdempotent`. 
Marking idempotency will also inform speculative executions, too.
```java
SimpleStatement statement =
    SimpleStatement.newInstance("SELECT first_name FROM user WHERE id=1")
        .setIdempotent(true);
```