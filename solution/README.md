# Expenses Backend

[![Java Language](https://img.shields.io/badge/language-Java-blue.svg)][1]
[![REST Architecture](https://img.shields.io/badge/architecture-REST-5DADE2.svg)][2]
[![Spring Boot Framework](https://img.shields.io/badge/framework-Spring%20Boot-6DB33F.svg)][3]

This web-app backend is a simple Spring Boot REST application.

## Requirements

- [Java SE 8][4]

## Setup Instructions

- Download and unzip this `expenses` project source code.

- Navigate to the (newly unzipped) project folder, then to `/src/main/resources` and locate the `application.properties` file.

- Configure the following properties - replace all values in `<>` with your own:

````properties
## Configure server properties
server.address = <hostname>
server.port = <port>

## Configure application endpoints
## Example: <api> as 'app' and <endpoint> as 'expenses'
application.api.path = /<api>
application.api.expenses.endpoint = /<endpoint>

## Configure Spring datasource
spring.datasource.url = jdbc:postgresql://<db_server>:<db_port>/<db_name>?useSSL=false
spring.datasource.username = <db_username>
spring.datasource.password = <db_password>

## Configure spring user with a custom password
spring.security.user.name = <username>
spring.security.user.password = <password>
spring.security.user.roles = <ROLE>

````

## Run Instructions

- Once the above application properties have been set, from a command line window, navigate to the application root folder (`expenses`), then run the `./mvnw spring-boot:run` (or `mvnw spring-boot:run` if you're on Windows) command.

- When the server is up and running, the REST API will be accessible at the server address configured earlier (`<hostname>:<port>`) after authentication (`<username>` and `<password>`).


[1]: https://docs.oracle.com/en/java/
[2]: http://www.vogella.com/tutorials/REST/article.html
[3]: https://projects.spring.io/spring-boot/
[4]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
