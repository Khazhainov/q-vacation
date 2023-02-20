## The app has the following functionality:

#### Check Availability:
To check if a specific day is available for booking, the user can make a GET /employee/date/{date?} request to the API endpoint with the date as a parameter. 
The API will then check the database to see if the day is already booked by any user. If the day is free, the API will return a response stating that the day is available for booking. 
If the day is already booked, the API will return the name of the user who has booked the day.

#### Book a Day:
To book a day for a specific user, the user can make a POST /day-off request to the API endpoint JSON payload as body request parameters. The API will then check the database to see if the day is already booked. 
If the day is free, the API will create a new booking in the database with the user information and return a success message. 
If the day is already booked, the API will return an error message stating that the day is not available for booking.

#### Receive Booking Requests via Kafka:
The app is set up to receive booking requests via Kafka. When a new booking request is received, the app will check the database to see if the requested day is available. 
If the day is free, the app will create a new booking in the database with the user information. 
If the day is already booked, the app will ignore the booking request.

### Restrictions:

1) 5 registered users:
    - johndoe@email.com
    - janesmith@email.com
    - bobjohnson@email.com
    - sarahwilliams@email.com
    - davidlee@email.com


2) Only one user can book a day, and if the day is already booked, the API will not allow another user to book the same day.

### Test information:

1) Endpoint: GET `/employee/date/{date?}`

Description:
This endpoint is used to get information about the employee who has a day off on a particular date. The date should be provided in the format "YYYY-MM-DD".
If an employee has a day off on the specified date, their information will be returned in the response.
If no employee has a day off on the specified date, a message indicating that the date is free to book will be returned.

Request Parameters:

`date` (string): the date for which to retrieve employee information in "YYYY-MM-DD" format.

Response. The endpoint returns a JSON payload with the following parameters:

`email` (string): the email address of the employee. Also has role as ID of Employee.
`firstName` (string): first name of the employee.
`lastName` (string): last name of the employee.

2) Endpoint: POST `/day-off`

Description:
This endpoint is used to add a day off request for an employee. The request should include the employee's ID and the date they would like to take off. 
If the request is successful, a confirmation response will be returned along with an HTTP status code of 201 (Created). 
If the request is unsuccessful, an error message will be returned along with an HTTP status code of 400 (Bad Request).

Request. The endpoint accepts a JSON payload with the following parameters:

`employeeId` (string): the unique ID (email) of the employee making the day off request.
`date` (string): the date the employee would like to take off in "YYYY-MM-DD" format.

Response. The endpoint returns a JSON payload with the following parameters:

`success` (boolean): whether the request was successful or not.
`message` (string): a message indicating the reason for success or failure.

### Kafka:

To test booking requests that can come through Kafka, you should use Docker and run the following commands:
1) Create network: `docker network create kafkanet`
2) Run Zookeeper: `docker run -d --network=kafkanet --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_TICK_TIME=2000 -p 2181:2181 confluentinc/cp-zookeeper`
3) Run Kafka: `docker run -d --network=kafkanet --name=kafka -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -p 9092:9092 confluentinc/cp-kafka`
4) Create topic: `docker exec kafka kafka-topics --create --topic book-day-off-topic --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092`
5) Open console producer: `docker exec -it kafka /bin/kafka-console-producer --topic book-day-off-topic --bootstrap-server localhost:9092 --property parse.key=true --property key.separator=:`
6) Create an Event: `bobjohnson@email.com:2027-08-10` 