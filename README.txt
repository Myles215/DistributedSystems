Distributed Systems Assignment 2

Comes packaged with Gradle and Maven jar files

Lamport clock:
    - My lamport clock is simply an object with functionality to increment the lamport time and check it against an incoming timestamp
    - My design is to have 1 central lamport clock with a queue for timestamp events being completed

    The way the queue works in steps:
        1. Lamport time = 0
            Aggregation server starts 
        2. Lamport time = 0
            Content server connects
            Aggregation server increments lamport time
            Aggregation server sends lamport timestamp 1 to content server
            (This timestamp is now the content servers designated start timestamp)
        3. Lamport time = 1
            Client connects
            Aggregation server increments lamport time
            Aggregation server sends lamport timestamp 2 to client
        4. Lamport time = 2
            Client sends a GET request with timestamp 2
            Aggregation server places timestamp 2 in queue
            queue = [2]
            Aggregation server increments lamport time
        5. Lamport time = 3
            Content server sends a PUT request with timestamp 1
            Aggregation server places timestamp 1 in queue
            queue = [1 , 2]
            Aggregation server increments lamport time
        6. Lamport time = 4
            Aggregation server handles first item in queue
            Content server PUT request is handled as it has lowest time in queue
        7. Lamport time = 4
            Aggregation server handles next item in queue
            Client GET request is handled as it has lowest time in queue

    As we can see, this means if multiple servers and clients connect, they're requests will be handled in order events
    if there is some delay in the ordering of messages

Test Methodology:
    To run all automated tests do './gradlew test'

    Test Harness
    - I use a number of test harness members
    - These include MockServers and clients that extend threads, allowing them to be run in the background while actual components are tested

    Aggregation server
    - Testing interaction of one or multiple clients with Aggregation server
    - on restart do temporary files get loaded to data
    - can a content server send data and a client can read the same data

    Client 
    - Can a client send a formatted get request
    - Can a client read a response from a server
    - Client retries connection on failure

    Content server
    - Can the content server read data from file and send it to the aggregation server
    - Test reading strings, ints and the two types together
    - Content server retries connection on failure

    File parser
    - Can the file parser read JSON data from a file
    - Can the file parser place JSON data in a file
    - Can the file parser delete daa older than 30 seconds

    HTTP parser
    - Can the HTTP parser detect malformed requests
    - Can the HTTP parser detect internal server errors
    - Can the http parser read a proper request and get the correct data
    - All HTTP codes and error codes reported appropriately

    Integration test
    - Adding everything together
    - 1 client, 1 content server, 1 aggregation server
    - Multiple clients, multiple content servers, 1 aggregation server

    Json parser
    - Can we read JSON from a string to a mapped object
    - Can we transform JSON to a string

    Lamport clock
    - Do we do things in the order specified by the lamport timestamp
    - Sending multiple requests to the server in a mixed up order and seeing if they get completed in order

To play with:
    make Makefile

    in seperate terminals:
        java server.AggregationServer hostname:port

        java server.ContentServer hostname:port ./inputFile.txt

        java client.GETClient port
