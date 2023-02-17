docker exec -it kafka /bin/kafka-console-producer --topic book-day-off-topic --bootstrap-server localhost:9092 --property parse.key=true --property key.separator=:

bobjohnson@email.com:2027-08-10