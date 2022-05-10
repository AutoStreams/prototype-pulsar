## About The Project
This is a Pulsar producer prototype that uses a Netty server to receive messages from Netty clients. The received messages are delegated to a Pulsar broker.
## Getting Started
First acquire this project by cloning the repository. Cloning can be done by downloading [Git](https://git-scm.com/), then executing the command:
```bash
git clone https://github.com/AutoStreams/prototype-pulsar.git
```
The following section explains how to build and run the project.
### Option 1: Build and run with Maven
**Prerequisites**
* Download the latest version of [Maven](https://maven.apache.org/).
* Download a Java JDK of version 17
* Set the working directory to the root of this Pulsar prototype producer project which is **`prototype-pulsar/producer`**

To build the project with its dependencies to a single jar file, execute the command:
```bash
mvn package
```
To run the application, execute the command:
```bash
java -jar target/pulsar-producer.jar 
```
### Option 2: Build and run with Docker
**Prerequisites**
* Make sure you have downloaded [Docker](https://www.docker.com/) on your system.
* Set the working directory to the root of this Pulsar prototype producer project i.e. **`prototype-pulsar/producer`**
 
To build the docker image, execute the command:
```bash
docker build -t producer .
```

To start a container from the built image, execute the command:
```bash
docker run -it producer
```