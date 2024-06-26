# Online Voting
The first version is still under development.

## What is Online Voting?
Online Voting is voting system that allows creating elections in simple way.
Admins can register a new users and create voting.
Register users can vote.

## Used technology stack
* Java 21
* Spring Boot 3.2.3

## Getting started
### Run locally
```
docker-compose up -d
./gradlew bootRun
```
### Run tests
```
./gradlew test
```
### Build
```
./gradlew bootJar
```
### Run executable jar
```
java -jar .\online-voting.jar
```

## REST endpoints
Postman collection with sample requests for all endpoints is available in file "Online Voting.postman_collection.json".

## What should be done next?
The following changes should be done next in this project:
* Change REST endpoints naming convention to use plural nouns.
* Change endpoints for create and update resource to return updated resources instead of returning just information about it.
* Implement CORS policy.
* Introduce Docker for a whole project.
* Update Java and dependencies version.
* Secure endpoint for removing users to avoid removing admin account if only one admin account exists.
* Add limit on the number of records returned by REST endpoints to secure application from DoS attacks.
* Replace deprecated functionalities from dependencies with the newest one.

## License
Online Voting is licensed under the MIT license.
