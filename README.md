# Spring Cloud Kubernetes Service Catalog Connector

A Spring Cloud Connector for Kubernetes Service Catalog.

## Features

- Easily retrieve info for Service Catalog managed services.
- Spring Cloud integration.

What it does behind the scenes is that it retrieves the service bindings of a managed instance and through that it obtains the connection information (which is stored as as a secret).

## Usage

To use this connector just follow these steps:

-add the connector to classpath
-create an instance of cloud
-get the service info from the cloud.

### Adding the connector to the classpath 

#### Using maven

For example using maven:

    <dependency>
      <groupId>me.snowdrop</groupId>
      <artifactId>servicecatalog-connector</artifactId>
      <version>0.0.1</version>
    </dependency>

### Create an instance of the cloud

To create an instance of `Cloud` and retrieve info about a service instance:

    Cloud cloud = new CloudFactory().getCloud();
    ServiceInfo info = cloud.getServiceInfo(serviceInstanceName);

Here's a full example of a program that prints service info in the console: [service info printer](examples/service-info-printer).

### Integrate with Spring Cloud

Spring cloud already supports connectors and it makes it very easy to consume services supported by the connector.
The user just needs to use a `ServiceConnectionfactory` and create the connections by referencing the service instance by name.

The easiest way to do so, is by extending `AbstractCloudConfig`.

    @Configuration
    public class CloudConfig extends AbstractCloudConfig {

        @Bean
        public DataSource dataSource() {
           return this.connectionFactory().service("dh-postgresql-apb", DataSource.class);
        }
    }

For a full example check: [simple database](examples/simple-database).

## Supported Services

At the moment the following services are supported:

### Relational datases

- Mysql
- Postgress
- SQL server
- Oracle

All of the services above are mapped using the following property names:

- DB_HOST
- DB_PORT
- DB_NAME
- DB_USER
- DB_PASS
