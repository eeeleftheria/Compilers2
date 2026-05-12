all: compile

BUILD_DIR = ../build

compile:
	javac -d $(BUILD_DIR) ../SymbolTable/*.java
	java -jar ../jtb133di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac -cp .:$(BUILD_DIR) *.java

clean:
	rm -rf *.class syntaxtree visitor minijava-jtb.jj *~
	rm -rf $(BUILD_DIR)