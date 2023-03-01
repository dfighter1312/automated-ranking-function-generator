all:
	${JAVAC} -g *.java
	mkdir -p classes
	mv *.class classes
	jar cfm GCD.jar MANIFEST.MF classes

clean:
	rm -rf classes
	rm -rf *.jar
