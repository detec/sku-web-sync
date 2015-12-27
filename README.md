# SKU web synchronization #

## Business purpose ##

This JSF 2.2 web application is a customization of exchange between Oracle E-Business Suite (OEBS) 11 and an XLS-file with SKUs (Stock Keeping Unit). This is a corporate project in production. 

The process of synchronization has been split into such phases:

- user authentication (GlassFish security realm is used);
- XLS file upload and check;
- output of XLS-OEBS changes in SKUs, ability to add, remove XLS file records, rename SKUs in file;
- output of XLS-OEBS changes in SKUs, ability to modify SKU names in OEBS, according to new XLS file data;
- output of XLS-OEBS changes in SKU groups, ability to add, rename or remove SKU groups from groups hierarchy in database;
- output of XLS-OEBS changes, so that SKU can be placed to another SKU group in database;
- ability to download modified XLS file from server, if necessary.

## Technical details ##

The application was re-written from its old command-line predecessor. The aim was to avoid direct production DB connection from user's PC due to new corporate security policy. UI has been built as a step-by-step wizard with JavaServerFaces framework on top of GlassFish application server.

## Tests notice ##

As this application uses a large customized OEBS database with piles of interdependent tables, initially only automated superficial UI testing was used, where Maven cargo plugin raises GlassFish 4 instance and changes container configuration to desired one. From client side Selenium Firefox client tests login and logout, Excel file upload and download, ability to pass through wizard steps without changes made to database.
With DBUnit and H2 components had been added, it became possible to develop unit tests for JSF controllers apart from OEBS database. DBUnit made functional tests possible, where preliminarily prepared small Excel files are imported and processed, changes are made to H2 in-memory database and afterwards compared to verified XML datasets.

## System requirements ##

- GlassFish 4.1.1 and higher;
- Java 8;
- configured JDBC datasource named "Oracle";
- configured security realm named "LoginRealm", with group "USER";
- customized OEBS database.

## Technologies ##

- JSF 2.2;
- CDI 1.2;
- JDBC;
- Hibernate Validator 5.2;
- JUnit 4.12;
- Selenium;
- DBUnit 2.5;
- H2;
- Maven 3.3 with plugins compiler, surefire, resources, war, cargo
- GlassFish 4.1;
- Java 8.

The project can be built either with Maven (3.3 or higher) or Eclipse (4.5 or higher).