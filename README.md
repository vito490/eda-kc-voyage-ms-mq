# Quarkus Qpid JMS Quickstart

This project illustrates how you can use the AMQP JMS client from [Apache Qpid](https://qpid.apache.org/components/jms/) to interact with [IBM MQ](https://www.ibm.com/docs/en/ibm-mq) AMQP 1.0 servers in a [Quarkus](https://quarkus.io/) application using the [Quarkus Qpid JMS extension](https://github.com/amqphub/quarkus-qpid-jms).

##Anatomy

The project is composed by the following:
```VoyageRequestListener```: schedules a consumer that reacts upon receiving a message on a specific queue of the IBM MQ Server
```JMSQueueWriter```: Creates an ad hoc connection to the IBM MQ Server and writes the output of the processed message on a specific queue (specified in an environment variable) 
```doman.model``` package: maps business logic assets to POJOs

## Start the application

The application can be started using:

```bash
mvn quarkus:dev
```

## Running in native

You can compile the application into a native binary using:

`mvn clean package -Pnative`

Or, if you dont have GraalVM installed, you can instead use Docker to build the native executable using:

`mvn clean package -Pnative -Dquarkus.native.container-build=true`

and then run with:

`./target/jms-quickstart-1.0.0-SNAPSHOT-runner`
