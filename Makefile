JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		  Calculator.java \
		  CalculatorImplementation.java \
		  CalculatorClient.java \
		  CalculatorServer.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	
run: 