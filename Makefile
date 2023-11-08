JC = javac

FILES = ./paxos/LamportClock.java ./paxos/Message.java ./paxos/PaxosClient.java ./paxos/Server.java

CONVERT = $(FILES:.java=.class)

all: $(CONVERT)

%.class: %.java 
	$(JC) $< 

tidy:
	rm -f *.class
	rm -f *.txt