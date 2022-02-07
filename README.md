## Overview
This is a Demo Project for an E-commerce Microservices Application that demonstrates Event-driven architecture using Kafka.  
<p align="center">
  <img  src="inf/spring.svg" width="200" height="200"/>
  <img  src="inf/kafka.svg" width="200" height="200"/>
</p>  

## Specs  
The System consists of 3 microservices as shown in the next figure.  
-  ms-orders: a restful webservice & an orchestrator for the ordering process.  
-  ms-stock: management of product inventory.  
-  ms-payments: for payment processing.  


<p align="center">
  <img  src="inf/spring.svg" height="400"/>
</p>  

## Steps to deploy
- There are several ways to run a Spring Boot application on your local machine. 
One way is to execute the `main` method 
in the `com.raisin.recruiting.chanllenge.ChallengeApplication` class from your IDE.

- Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Testing
- A postman collections file is provided to test the system (to be added).  
- Load the file to postman and check the provided requests.

## Built With  
&nbsp;&nbsp;&nbsp;&nbsp;**Java (JDK 11)** - The Main Programming Language and Framework.  
&nbsp;&nbsp;&nbsp;&nbsp;**Spring Boot** - Software Platform for creating and delivering Web Applications.  
&nbsp;&nbsp;&nbsp;&nbsp;**Apache Kafka** - distributed data streaming platform.  
&nbsp;&nbsp;&nbsp;&nbsp;**MySQL** - A relational database management system [Optional].  
&nbsp;&nbsp;&nbsp;&nbsp;**Docker** - A containerization platform.  
&nbsp;&nbsp;&nbsp;&nbsp;**Maven** - Build tool & Dependency Management.  
