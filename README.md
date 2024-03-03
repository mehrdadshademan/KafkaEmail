How Could Run the project
==========================
first of all for running the docker machine and user kafka and kafka ui, you should run this script in terminal :
**docker-compose up -d**

**Note:** you can do not put -d but when you put it that means, command is used to run the containers in the background,
detached from the terminal.

After run the docker compose, our image and container is created and run into the docker. you can check the containers with put the commend "docker ps" into the terminal

How to interact with Application
==================

There are 2 below endpoints:  
/api/v1/email/generate-email/{topic}
/api/v1/email/consume/{topic}

both of endpoints are Post.

**Regarding the "generate-email(/api/v1/email/generate-email/{topic})" endpoint:**

This endpoint generates an email with a random domain defined in the EmailDomainEnum and random content.
The generated email is then sent to Kafka as a message.
The method includes a validation check for the topic, ensuring it is not null or empty.
The receivers are defined in the application properties, although in a real system, a separate table connected to the email entity might be used.
This project done as a simple project

**what is to consume ("/api/v1/email/consume/{topic}") endpoint:**
This endpoint, following the validation of the message topic, returns all consumed emails that match with this topic(message topic like email subject).

**How consumer and producer work:**
We have configured Kafka for both consuming and producing messages.
Although options like Avro and schema could be employed to send messages in binary format, and Kafka Connect could be utilized for direct database insertion from the Kafka topic, we have opted for a simpler and more understandable approach in this project.
Here, we serialize both the produced and consumed messages using JsonSerializer and JsonDeserializer.
In the configuration, we specify the class address to define partitions based on keys. These keys are defined in the application properties.

**What is the purpose of EmailProducer?**
The EmailProducer class is responsible for sending messages to Kafka. This class verifies the input email entity, ensuring it has a valid sender. It then extracts the domain from the sender to use as the key. Based on this key, the partition is defined in the configuration.


**What does the consumer (Email Listener) do?**
In this instance, we have kept the system uncomplicated, so we have not implemented a Dead-Letter Queue (DLQ) for our messages. After consuming the message, the method processing to store data in the database. Although Kafka Connect could be an alternative for this purpose, we have opted for a simpler approach given the straightforward nature of our project.

What is the DLQ: 
=============
In the scenario where our message cannot be processed (due to various reasons), Kafka attempts to process it up to 10 times. However, if the processing still fails after these attempts, the offset is changed, resulting in the loss of the message.
To address this issue, we redirect the problematic failed message to another queue. We include a header indicating the count of retries, and additional retry attempts are made. If the message fails again after several retries, we place it in yet another queue. This allows the monitoring team to review and investigate the failed messages.

What is the kafka ui
=====
This is a UI for Kafka that enables you to view your topics and various details within Kafka. Additionally, you can send messages and create topics using Kafka UI. The port for Kafka UI is specified in the Docker Compose file (49100).

Exception Handler : 
=======
In this project, ExceptionHandlerApi is employed for exception handling. I define the necessary exception classes, utilize them in ExceptionHandlerApi, and handle them appropriately.

How can we test : 
===============
In the test, I utilize EmbeddedKafka with configurations specified within the test itself. Consequently, there is no need to run Docker separately on our machine. By simply executing the tests, these unit tests assess whether the partitioner is functioning correctly, verify the methods supporting the endpoints, and confirm if Kafka consumption is accurate.
moreover you can use this command line in terminal : **mvn test -Dfail-fast**

