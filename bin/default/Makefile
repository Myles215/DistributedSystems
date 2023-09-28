JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		  client/GETClient.java \
		  server/AggregationServer.java \
		  server/ContentServer.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class 
	
run: 