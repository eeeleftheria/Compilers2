all: compile

BUILD_DIR = ../build
ARGS = Example.java

compile:
	javac -d $(BUILD_DIR) ../SymbolTable/*.java
	java -jar ../jtb133di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac -cp .:$(BUILD_DIR) Main.java

run: compile
	java -cp .:$(BUILD_DIR) Main $(ARGS)

clean:
	find . -maxdepth 1 -name "*.java" ! -name "Example.java" ! -name "Main.java" ! -name "SymbolTableVisitor.java" ! -name "TypeCheckingVisitor.java" ! -name "VisitorArgs.java" -delete
	rm -f minijava_example.iml minijava.html
	rm -rf *.class syntaxtree visitor minijava-jtb.jj *~
	rm -rf $(BUILD_DIR)