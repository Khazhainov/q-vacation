To test booking requests that can come through Kafka, you should run the following commands: 

0) Create network: docker network create kafkanet
1) Run Zookeeper: docker run -d --network=kafkanet --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_TICK_TIME=2000 -p 2181:2181 confluentinc/cp-zookeeper
2) Run Kafka: docker run -d --network=kafkanet --name=kafka -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -p 9092:9092 confluentinc/cp-kafka
3) Create topic: docker exec kafka kafka-topics --create --topic book-day-off-topic --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092
4) Open console producer: docker exec -it kafka /bin/kafka-console-producer --topic book-day-off-topic --bootstrap-server localhost:9092 --property parse.key=true --property key.separator=:
5) Create an Event: bobjohnson@email.com:2027-08-10