# Simple Datase example

A simple persons CRUD exposed via rest that used a Service Catalog managed postgress.

## Preparation

### Permissions

Ensure that the service account that you are going to use has permissions to
- list service instances and bindings
- read secrets

This may vary from environment to environment.

### Provision and bind to the service

Using either svcat or the console create a postgres instance.

    svcat provision --class dh-postgresql-apb --plan dev mydb
    svcat bind mydb

### Build and deploy the application
From the command line:

    mvn clean package fabric8:deploy

This will create the docker image and kubernetes/openshift resources required and also deploy to cluster.

Once the application is deployed you can send http requests to it for example:

    curl -i -X POST -H "Content-Type:application/json" -d "{  \"firstName\" : \"John\",  \"lastName\" : \"Doe\" }" http://<service host>:8080/people
