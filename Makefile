JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		  server/AggregationServer.java \
		  server/ContentServer.java \
		  client/GETClient.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	
run: 