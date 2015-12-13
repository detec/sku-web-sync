# SKU web synchronization #

This JSF 2.2 web application is a customization of exchange between Oracle E-Business Suite (OEBS) 11 and an XLS-file with SKUs (Stock Keeping Unit). The application was re-written from its old command-line predecessor, written by a Java newbie; the aim was to avoid direct production DB connection from user's PC due to new corporate security policy. This is a corporate project in production. 

The process of synchronization has been split into such phases:

- user authentication (GlassFish security realm is used);
- XLS file upload and check;
- output of XLS-OEBS changes in SKUs, ability to add, remove XLS file records, rename SKUs;
- output of XLS-OEBS changes in SKUs, ability to modify SKU names in OEBS, according to new XLS file data;
- output of XLS-OEBS changes in SKU groups, ability to add, rename or remove SKU groups in OEBS;
- output of XLS-OEBS changes, so that SKU can be placed to another SKU group in DB;
- ability to download modified XLS file from server, if necessary.

## Tests notice ##

As this application uses a large customized OEBS database with piles of interdependent tables, there are no JUnit tests. Only automated functional testing is used, where Maven cargo plugin raises GlassFish 4 instance and changes configuration to desired one. From client side Selenium Firefox client tests login and logout, .xls file upload and download, ability to pass through wizard steps without changes made to database.

## System requirements ##

- GlassFish 4.1.1 and higher;
- Java 8;
- configured JDBC datasource named "Oracle";
- configured security realm named "LoginRealm", with group "USER";
- customized OEBS database.

## Technologies ##

- JSF 2.2;
- CDI 1.2;
- Hibernate Validator 5.2.1;
- JUnit 4.12;
- Selenium WebDriver;
- JDBC;
- GlassFish 4.1;
- Java 8.

The project can be built either with Maven (3.3 or higher) or Eclipse (4.5 or higher).




