# Service Info Printer

An example that prints service info into the console.

## Preparation

### Provision and bind to the service

Using either svcat or the console create a postgres instance.

    svcat provision --class dh-postgresql-apb --plan dev mydb
    svcat bind mydb

## Build the project

    mvn clean install 
    
## Running the project

    mvn exec:java -Dexec.mainClass="me.snowdrop.servicecatalog.connector.examples.GetServiceInfo" -Dexec.args="mydb"
    
The command above should display in the console the service info object for `mydb`.
