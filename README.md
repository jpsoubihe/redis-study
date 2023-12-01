# redis-study
A brief study of Redis DB and some analysis.

## The study

Our initial idea is to divide this study into 3 basic projects
1. Basic CRUD
2. Redis comparison with SQL DB
3. Redis as cache

During the setup of each one, we aim to describe our process, configurations, models and implementation. At the end of a project we can write some comments regarding their functionality, advantages and disavantages.

We also will use this opportunity to collect some metrics, using Prometheus and Grafana. We believe this will enrich our documentation and give more substance to the points we are making on this doc.

### Basic CRUD

To start with, we've add a docker-compose file with a redis image prepared to help us on development. 

Our application will be a simple CRUD based on spring framework (Spring Boot, Spring Data, ...), so we'll need also to add those dependencies to pom.xml file.

We will have a some models to handle.
To apply our analyzis to Redis application we thought on 2 different entities, that would allow us to prepare different environments and exercise our Redis in-memory storage in a variety of ways.

#### Account.java
```
{
    "accountId": "2275be7b-aca4-49fa-b3e5-2ddc93a63d90",
    "accountName": "Test account"
}
```

#### Food.java
```
{
    "foodId": "b136bcb3-5e44-41e8-80b2-dc554f222142",
    "foodName": "Milk Shake",
    "foodIngredients": ["Milk", "Ice-cream", "chocolate"],
    "foodType": "Sweet"
}
```

After that model is defined, we can start to develop our controller, based on EPs below:

- [POST] /v1/account/
- [GET] /v1/account/{id}
- [PUT] /v1/account/{id}
- [DELETE] /v1/account/{id}
- [POST] /v1/food/
- [GET] /v1/food/{id}
- [PUT] /v1/food/{id}
- [DELETE] /v1/food/{id}

## Redis Structure

To store information regarding foods, we applied a standard key format like
`food:{foodId}`

And to store accounts
`account:{accountId}`

We configured two different **RedisTemplate** for operations involving foods and accounts, respectively. The beans can be seen at `RedisConfiguration.java` class. With it we applied a RedisValueSerializer using **Jackson2JsonRedisSerializer** to allow our objects to be mapped to their abstractions, instead of handling it as strings.

## Tests

We can see tests made at ./src/test/java/com/example/redisStudy

IntegrationTests are located at `integrationTest` folder. All of them will have same suffix. 

Using surefire plugin, we were able to set it execution on a separate phase then unitTests, that are located at `unitTest` folder.

```
<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>**/integrationTest/*.java</exclude>
                    </excludes>
                </configuration>
                <executions>
                    <execution>
                        <id>integration-test</id>
                        <goals>
                            <goal>test</goal>
                        </goals>
                        <phase>integration-test</phase>
                        <configuration>
                            <excludes>
                                <exclude>none</exclude>
                            </excludes>
                            <includes>
                                <include>**/integrationTest/*.java</include>
                            </includes>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```
## How to run it

At root level we can find docker-compose.yaml file. Through it we can raise the container for Redis and allow our service to connect to it once the application is started.

```
docker-compose up
```

Then we can run it using Maven wrapper or through your preferred IDE. The application was developed for Java 17, instead of almost no use of its features, so pay attention to your JAVA version. At root level
```
./mvnw spring-boot:run
```

As for the tests, we attached `test-container` library. The configuration we set at `DockerConfigurationIntegrationTest` will raise a Redis container at port 6379 just for the integration tests context (so make sure this door is available at the moment!).

The **mvn test** task should run exclusively our unit tests, per task configuration at `pom.xml` file.

```
./mvnw test
```

To run both unit and integration tests we can opt for the mvn verify task

```
./mvnw verify
```

## Monitoring

On docker-compose file, found on src folder, we can see a setup for prometheus and grafana.

We are using spring-actuator tools to expose metrics and health indicators.

The micrometer registry dependency is the responsible to export data, seen at `/actuator/prometheus`, at an understadable format for prometheus server scraping.

Grafana will use a network bridge to connect to this prometheus server container and visualize data through its built Dashboards

### Dashboards

### Alarms

## Future developments

- Checkstyle task and configuration definition
- Add metrics
- Add dashboards for performance comparison

