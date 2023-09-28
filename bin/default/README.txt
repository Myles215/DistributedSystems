Distributed Systems Assignment 2

Comes packaged with Gradle and Maven jar files

To test:
    ./gradlew test

To play with:
    javac server/AggregationServer.java
    javac server/ContentServer.java
    javac client/GETClient.java

    or
    Makefile

    in seperate terminals:
        java server.AggregationServer port

        java server.ContentServer port ./inputFile

        java client.GETClient port
