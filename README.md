# Sample Credit Card application eco-system

This branch contains the new stack demo. To see the same demo with Spring-Cloud-Netflix stack, check out the branch `old-stack`.

After running all the apps execute POST at `localhost:9080/application` passing 
`cardApplication.json` as body.

```bash
http POST localhost:9080/application < cardApplication.json
```

- new card applications registered via `card-service`
- user registered via `user-service`
- `fraud-service` called by `card-service` and `user-service` to verify 
card applications and new users

If you want to run a bigger number of requests, you can use the `ab` benchmarking tool:

```bash
ab -p cardApplication.json -T application/json -c 10 -n 20000 http://localhost:9080/application
```

```bash
http GET localhost:9083/ignored/test
```
```bash
http GET localhost:9083/ignored/test/allowed
```
- `ignored` service with `test` endpoint returning 404 via Proxy and `/test/allowed` 
endpoint returning response from the service.

```
+-------+                         +-------------+       +-------------+          +-------+             +---------------+ +-----------------+ +-------+
| User  |                         | CardService |       | UserService |          | Proxy |             | FraudVerifier | | IgnoredService  | | PRoxy |
+-------+                         +-------------+       +-------------+          +-------+             +---------------+ +-----------------+ +-------+
    |                                    |                     |                     |                         |                  |              |
    | Register application               |                     |                     |                         |                  |              |
    |----------------------------------->|                     |                     |                         |                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    | Create new user     |                     |                         |                  |              |
    |                                    |------------------------------------------>|                         |                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    |                     |     Create new user |                         |                  |              |
    |                                    |                     |<--------------------|                         |                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    |                     | Verify new user     |                         |                  |              |
    |                                    |                     |-------------------->|                         |                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    |                     |                     | Verify new user         |                  |              |
    |                                    |                     |                     |------------------------>|                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    |                     |                     |           User verified |                  |              |
    |                                    |                     |                     |<------------------------|                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    |                     |       User verified |                         |                  |              |
    |                                    |                     |<--------------------|                         |                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    |        User created |                     |                         |                  |              |
    |                                    |<--------------------|                     |                         |                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    |                     |                     |                         |                  | User created |
    |                                    |<------------------------------------------------------------------------------------------------------|
    |                                    |                     |                     |                         |                  |              |
    |                                    | Verify card application                   |                         |                  |              |
    |                                    |-------------------------------------------------------------------->|                  |              |
    |                                    |                     |                     |                         |                  |              |
    |                                    |                     |                     Card application verified |                  |              |
    |                                    |<--------------------------------------------------------------------|                  |              |
    |                                    |                     |                     |                         |                  |              |
    |        Card application registered |                     |                     |                         |                  |              |
    |<-----------------------------------|                     |                     |                         |                  |              |
    |                                    |                     |                     |                         |                  |              |
```

```
+-------+                         +-------+         +-----------------+
| User  |                         | Proxy |         | IgnoredService  |
+-------+                         +-------+         +-----------------+
    |                                 |                      |
    | IgnoredService/Test             |                      |
    |-------------------------------->|                      |
    |                                 |                      |
    |                             404 |                      |
    |<--------------------------------|                      |
    |                                 |                      |
    | IgnoredService/Test/Allowed     |                      |
    |-------------------------------->|                      |
    |                                 |                      |
    |                                 | Get allowed          |
    |                                 |--------------------->|
    |                                 |                      |
    |                         Allowed |                      |
    |<--------------------------------|                      |
    |                                 |                      |
```
# Setup using new stack

## Client side load-balancing using LoadBalancerClient

- Ribbon used via `@LoadBalanced` `WebClient`
- Ribbon configuration modified via `@LoadBalancerClient`

## Apps communicating via Gateway:
- Routes have to be explicitly defined
- Possibility to configure routes either via properties or Java configuration
- All headers passed by default
- Routes matched using predicates, requests modified using filters

## Spring Cloud Circuit Breaker + Resilience4J
- Interactions defined using injected `CircuitBreakerFactory` via the `create()` method
- HTTP call and fallback method defined
- Circuit breaker configuration modified in `Customizer<CircuitBreaker` bean 
in a `@Configuration` class 
- Resilience4J used underneath																																

## Micrometer + Prometheus
- HTTP traffic monitoring using Micrometer + Prometheus
- Added a Micrometer's `Timer` to `VerificationServiceClient`
