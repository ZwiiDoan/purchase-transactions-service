# Purchase Transactions Service

### Requirements

1. The Application can store a purchase transaction with a description, transaction date, and a purchase amount in
   United States dollars. When the transaction is stored, it will be assigned a unique
   identifier. Field requirements:
    * Description: must not exceed 50 characters
    * Transaction date: must be a valid date
    * Purchase amount: must be a valid amount rounded to the nearest cent
    * Unique identifier: must uniquely identify the purchase
2. The Application can retrieve the stored purchase transactions converted to currencies supported by the [Treasury
   Reporting Rates of Exchange API](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange)
   based upon the exchange rate active for the date of the purchase. The retrieved purchase should include:
    * The identifier
    * The description
    * The transaction date
    * The original US dollar purchase amount
    * The exchange rate used
    * The converted amount based upon the specified currency’s exchange rate for the date of the purchase.

   Currency conversion requirements:
    * Must use a currency conversion rate less than or equal to the purchase date from within the last 6 months.
    * If no currency conversion rate is available within 6 months equal to or before the purchase date, an error should
      be returned stating the purchase cannot be converted to the target currency.
    * The converted purchase amount to the target currency should be rounded to two decimal places (i.e., cent).
3. The application repository should be runnable without installing additional software stack components, such as
   databases, web servers, or servlet containers (e.g., Jetty, Tomcat, etc).

### Assumptions

1. Dates in Treasury Reporting Rates of Exchange API are in UTC timezone
2. Purchase Transaction dates are also in UTC timezone.
3. The application requires a transaction id and "Country-Currency" (supported by the [Treasury
   Reporting Rates of Exchange API](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange))
   to retrieve a Purchase Transaction in a Specified Country’s Currency
4. Requirement #3 prevents Docker Engine Installation and Usage but not Java 17 Installation and Embedded/In-memory
   database (H2) anc cache (Redis) which requires no extra installation.

### Solution

The Application is built as a Web Service that exposes 2 endpoints to store and retrieve a single Purchase Transaction.
For more information about the endpoints, please open [pts-swagger.yml](pts-swagger.yml). "Store a Purchase Transaction"
endpoint is straight-forward: the incoming Purchase Transaction is validated and assigned
a UUID before getting persisted into Database. The diagram below depicts solution for the "Retrieve a stored purchase
transaction" endpoint.

![Purchase Tranasction Service - Solution.PNG](diagrams%2FPurchase%20Tranasction%20Service%20-%20Solution.PNG)

* The application uses a cache to store exchange rate fetched from Treasury Reporting Rates of Exchange API so that it
  will not need to make the downstream call for every user request. Exchange rate is cached by "Country-Currency" (
  supported by the [Treasury
  Reporting Rates of Exchange API](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange))
* When sending requests to Treasury Reporting Rates of Exchange API, the application always asks for the
  latest exchange rate that is not older than 6 months from current time to satisfy Requirement #2. This is achieved by
  using the API query
  params: `&filter=country_currency_desc:eq:{Requested-Country-Currency},record_date:gte:{Current-Date-6-Months-Ago}&sort=-record_date&page[number]=1&page[size]=1`.
  This approach minimises the implementation required and the amount of data needs to be fetched.
* The application utilises [Resilience4j](https://resilience4j.readme.io/docs/getting-started-3) to implement best
  practice patterns when interacting with downstream services such as: circuit-breaker, rate-limiting (throttling),
  bulkhead and retry
* For local development and test, the repo uses Embedded/In-memory database (H2) anc cache (Redis) which requires no
  extra installation. In remote and production environments, these dependencies are expected to be production-grade
  systems such as AWS RDS, AWS ElasticCache. The application is also expected to be deployed as multiple Docker
  containers/computing instances in a High Availability and Secured Infrastructure with production-grade systems such as
  AWS ECS exposed via AWS API Gateway.

### Build

Execute from repo's root directory

```
/.gradlew clean build
```

The build pipeline integrates different tools to verify code quality and security such as:

* [Checkstyle] (https://checkstyle.sourceforge.io/) to ensure coding standard using Google Checkstyle
  at [config/checkstyle/checkstyle.xml](config/checkstyle/checkstyle.xml)
* [Spotbugs] (https://spotbugs.github.io/) with plugin [FindSecBugs](https://find-sec-bugs.github.io/) that do static
  analysis to look for common bugs and security bugs in Java code
* [Jacoco] (https://github.com/jacoco/jacoco) to ensure code and test coverage. The minimum instruction
  and branch coverage of this repo is 95% which is satisfied by unit tests and integration tests.

The application uses [Flyway](https://flywaydb.org/) to manage [DB migrations](src/main/resources/db/migration). Flyway
is enabled at application start-up in local Development and Test environments but will be executed in a separated CD
Pipeline in remote environments.

### Run

Execute from repo's root directory

```
/.gradlew bootRun
```

* In Local Development (default) and Local Test environments, the application integrates with embedded/in-memory H2
  Database and Redis Cache.
* For demo purpose, it also connects to the real [Treasury
  Reporting Rates of Exchange API](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange)
  in Local Development environment
* In Local Test environment, the application uses Wiremock to stimulate the [Treasury
  Reporting Rates of Exchange API](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange)

User can store a Purchase Transaction with this curl command

```
curl --location 'http://localhost:8080/purchase-transaction' \
--header 'Correlation-Id: 08ef44c8-5477-44d1-a61c-f0931abf37bf' \
--header 'Content-Type: application/json' \
--data '{
    "description": "test-description",
    "transactionDate": "2023-09-01",
    "purchaseAmount": 9999.0312789000
}'
```

Then retrieve the transaction using id in previous response and curl command

```
curl --location 'http://localhost:8080/purchase-transaction/{transaction-id}?currency=Australia-Dollar' \
--header 'Correlation-Id: 08ef44c8-5477-44d1-a61c-f0931abf37bf'
```

### Future Improvements & Considerations

1. Error response schema can be adjusted to include more fields and show/hide details if required by enterprise
   standards or business requirements.
2. More robust observability solutions can be integrated such as distributed tracing, logging, metrics
   with [Spring Boot 3 Observability](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3).
