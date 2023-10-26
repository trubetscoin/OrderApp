# OrderApp

## A simple spring java shopping cart demo application.
Uses four tables: `customer`, `product`, `orderline`, `order` to simulate an online shopping cart. <br>
Made with SpringBoot + SpringData.

### Features
* CRUD functionality.
* API endpoints.
* Liquibase database setup
* Mockaroo sample data generation
* Unit tests
* Many useful search queries.

### Notes
Change `application.properties` to your configuration. If you want the database to automatically create then add `?createDatabaseIfNotExist=true` to datasource.url parameter <br>
In case you want to create a random sample data from Mockaroo you have to create correct schemas before creating mock apis and change the API key in `application.properties`. <br>
Be aware if you run the tests the first 50 rows in every table will be replaced with the test data from /test/json/*.json and then deleted and auto-incrementing of each table would reset to 1 due to how Mockaroo works. <br>
For future testing it would be better either to create a clone of database schema or a clone of tables.