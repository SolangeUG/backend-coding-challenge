# Expenses Backend

[![Java Language](https://img.shields.io/badge/language-Java-blue.svg)][1]
[![REST Architecture](https://img.shields.io/badge/architecture-REST-5DADE2.svg)][2]
[![Spring Boot Framework](https://img.shields.io/badge/framework-Spring%20Boot-6DB33F.svg)][3]
[![PostgreSQL Database](https://img.shields.io/badge/database-PostgreSQL-0092C2.svg)][5]

This web-app backend is a simple Spring Boot REST application.

## Requirements

- Download and install [Java SE 8][4]
- Download and install [PostgreSQL 10][5], and then create a database `<db_name>` for our application.

## Setup Instructions

- Download and unzip this `expenses` project source code.

- Navigate to the (newly unzipped) project folder, then to `/src/main/resources` and locate the `application.properties` file.

- Configure the following properties - replace all values in `<>` with your own:

````properties
## Configure server properties
server.address = <hostname>
server.port = <port>

## Configure application endpoints
## Example: <api> as app and <endpoint> as expenses would give this route /app/expenses
application.api.path = /<api>
application.api.expenses.endpoint = /<endpoint>

## Configure Spring datasource
spring.datasource.url = jdbc:postgresql://<db_server>:<db_port>/<db_name>?useSSL=false
spring.datasource.username = <db_username>
spring.datasource.password = <db_password>

## Configure Spring user with a custom password
spring.security.user.name = <username>
spring.security.user.password = <password>
spring.security.user.roles = <ROLE>

## Configure time zone and locale globally
# <timezone> can be something like Europe/London for example
# <locale> can be something like en-UK
spring.jackson.time-zone = <timezone>
spring.jackson.locale = <locale>

````

For currency conversion, we're using [Fixer][6] as our foreign exchange rates and currency conversion API provider.  

## Run Instructions

- Once the above application properties have been set, from a command line window, navigate to the application root folder (`expenses`), then run the `./mvnw spring-boot:run` (or `mvnw spring-boot:run` if you're on Windows) command.

- When the server is up and running, the REST API will be accessible at the server address configured earlier (`<hostname>:<port>`) after authentication (`<username>` and `<password>`).

- The **frontend configuration should be updated** to access the backend API at the above server address. For example, in this case, the `apiRoot` property (in `config.js`) will be set to `http://<hostname>:<port>/app`.


## Changes to Frontend Client

- As the backend is only accessible after authentication (Spring user), the **frontend must be configured** to allow session based authentication with the `Access-Control-Allow-Credentials: true` header. In `main.js`, adding the following lines will do the trick.

````JavaScript
app.config(['$httpProvider', function ($httpProvider) {
    // Configure $http requests to allow session based authentication
	$httpProvider.defaults.withCredentials = true;
}]);
````

- In calculating the VAT amount client-side, a **configuration point** is used to set the current sales tax (20%). So files `config.js` and `configuration.js` were modified as follows:

````JavaScript
module.exports = {
	// ...
	// The root directory for all api calls
	apiroot: "http://<hostname>:<port>/app",
	// Current VAT rate
	vatRate: "20",
	//...
};
````

````JavaScript
//...
app.constant("config", {
	apiroot: gulpEnvConfig.apiroot,
	staticRoot: gulpEnvConfig.staticRoot,
	vatRate: gulpEnvConfig.vatRate
});
````



[1]: https://docs.oracle.com/en/java/
[2]: http://www.vogella.com/tutorials/REST/article.html
[3]: https://projects.spring.io/spring-boot/
[4]: http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
[5]: https://www.postgresql.org/download/
[6]: https://fixer.io/
