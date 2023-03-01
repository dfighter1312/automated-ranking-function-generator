ifndef JAVAC
JAVAC=javac
endif

all:
	${JAVAC} -g *.java
	mkdir -p classes
	mv *.class classes
	jar cfm MatrixTraverseAlt.jar MANIFEST.MF classes

clean:
	rm -rf classes
	rm -rf *.jar
