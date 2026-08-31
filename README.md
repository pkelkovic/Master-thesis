# Master thesis
This repository contains five directories with source code for my master's thesis "Event-Driven Smart Warehouse Architecture"
There are four applications:
- sensor application - simulates the sensor's work and sends sensor readings to the MQTT broker
- aggregator application - collects sensors' data through MQTT, does basic data processing, and sends the gathered statistics to the main server through the Kafka broker
- main server's backend - receives the data through the Kafka broker, communicates with the main database, and exposes important endpoints
- main server's frontend - presents the data gathered from the backend

The first three applications are written in Java, using the SpringBoot framework.
The frontend app is written in JavaScript, using the React framework. 

The folder k8s contains YAML files for running these applications in Kubernetes.
