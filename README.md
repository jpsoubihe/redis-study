# redis-study
A brief study of Redis DB and some analysis.

## The study

Our initial idea is to divide this study into 3 basic projects
1. Basic CRUD
2. Redis comparison with SQL DB
3. Redis as cache

During the setup of each one, we aim to describe our process, configurations, models and implementation. At the end of a project we can write some comments regarding their functionality, advantages and disavantages.

We also will use this opportunity to collect some metrics, using Prometheus and Grafana. We believe this will enrich our documentation and give more substance to the points we are making on this doc.

### Basic CRUD

To start with, we've add a docker-compose file with a redis image prepared to help us on development. 

Our application will be a simple CRUD based on spring framework (Spring Boot, Spring Data, ...), so we'll need also to add those dependencies to pom.xml file.

We will have a couple models to handle. A simple one with few attributes, called XXX, and other one, XXX , with a lot of attributes and complex abstractions.

This difference between both is intend to give us a broad perspective and exercise Redis operations in different scenarios.

After that we can start to develop our controller, based on EPs below:

- [POST] Endpoint 1
- [GET] Endpoint 2
- [PUT] Endpoint 2
- [DELETE] Endpoint 2

