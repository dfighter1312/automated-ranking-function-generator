ifndef JAVAC
JAVAC=javac
endif

all:
	${JAVAC} -g *.java
	mkdir -p classes
	mv *.class classes
	jar cfm MatrixTraverse.jar MANIFEST.MF classes

clean:
	rm -rf classes
	rm -rf *.jar
