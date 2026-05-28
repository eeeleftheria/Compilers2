# MiniJava Static Checking (Semantic Analysis)

## Implementation
The following implementation was based mainly on last year's lecture suggestions on the project.

### Symbol Table
The symbol table was implemented using a `Linked Hash Map`, so the order of insertion is preserved.

This data structure maps `Class names` to `ClassSymbols` that represent a class and also stores
the name of the class that corresponds to main. 

```java
public class ClassSymbol{
    private String name; // name of class
    private String parentClass; // name of parents or "" if none
    private Vector<FieldSymbol> fields; // vector with all the fields 
    private Vector<MethodSymbol> methods; // vector with all the methods 
    private int totalFieldBytes; 
    private int totalMethodBytes;
}
```

```java
public class MethodSymbol{
    private String name; // name of method
    private String returnType; // return type of method
    private int offset; 
    private Vector<String> parametersTypes; // type of each parameter
    private Vector<String> parametersList; // names of the parameters
    private Vector<FieldSymbol> localFields; // local fields of method
}
```

The following class can represent either a field of a class or a local field of a method.
```java
public class FieldSymbol{   
    private String type; // type of field
    private String name; // name of field
    private int offset;
}
```

### Visitors
The visitors return a `String` value and get as input a `VisitorArgs` class.
```java
public class VisitorArgs {
    private String className;
    private String methodName;
    private String fieldName;
    private String type; 
    private Boolean inMethod;
    private String parameters;
}
```
- The `inMethod` boolean indicates if a field comes from a `ClassDeclaration` (inMethod = false) or a `MethodDeclaration` (inMethod = true). This way, in the `VarDeclaration` node we can simply check the value of the boolean and add the field as part of a class or a method respectively.
- The `parameters` variable stores the parameters of a method in format `type1 var1 type2 var2 ...` which is used in most operations.
    

#### Symbol table generator visitor
The first visitor builds the symbol table from scratch. Moreover it performs, some basic semantic error checks:
- Double declaration of class
- Double declaration of method's local variable
- Double declaration of a method's parameter.
- Double method declaration with exact same parameters.

#### Type Checking visitor
The TC visitor returns in most cases the type of the evaluated node. 

Some of the most important checks are:
- Double method declaration: unlike the check in the 1st visitor that only checks for methods with the exact same types, the TC visitor checks if the method can be overloaded. Specifically, it checks if there is at least one argument position between the two methods to not have a subtype/supertype relation.
- Mismatch between return value type and expected return type.
- Assignment error: checks if the rvalue is a subtype of the lvalue.
- Array Assignment errors: checks whether the variable is indeed an array type and whether the index and the assignment's value are integers.
- Array Lookup: lookup can only be performed to array types and an integer index.
- Print statement only accepts integer value.
- MessageSend `a.b()`: checks if `a`is a valid class name and if it contains `b()` method.


## How to Run

```bash
cd minijava
```
### Compile
```bash
make
```

### Run
1. Runs with `Example.java` as input.
```bash
make run
```

2. Runs all the error cases in the directory `../testCases/minijava-error-extra`.
```bash
./runErrorTests.sh
```
The results can be viewed in `../Results/error-extra-results.txt`.

3. Runs all the extra offset examples in the directory `../testCases/minijava-extra`.
```bash
./runOffsetsExtra.sh
```
The results can be viewed in `../Results/offsets-extra-results.txt`.

4. Runs all the offset examples in the directory `../testCases/minijava`.
```bash
./runOffsets.sh
```
The results can be viewed in `../Results/offsets-results.txt`.

5. Runs on all given files as arguments.
```bash
java -cp .:../build Main Example.java Example2.java ...
```

### Clean
```bash
make clean
```



## ToDo / Future improvements


- Method offsets:
To implement the offset logic for the methods, I implemented the following function in the `SymbolTable.java` file:

    The `calculateMethodOffset` function is called inside `addAllParameters()` as the final step for the method's declaration. More specifically, it detects the root of the hierarchy, meaning it reaches the class with no parent. Then, starting from the root and going down, it checks if the current class contains the method we are trying to calculate the offset for. The first time it finds the method, it keeps that offset and returns it. If no method was found in the parent classes it returns -1, meaning the offset should be calculated in a cumulative way. The above functions seem to function properly, after a few tests, meaning the issue is later.

- Field offsets: 
field offsets mostly function properly, however I have not handled the case in which a class has a field of the same type (the type of the class).
Therefore, in those cases the final offsets for the fields may also appear wrong.

- Assignment statement: more specific errors, like throwing an exception
when either the lvalue or rvalue was not declared. Current implementation of identifier node
makes it unfeasible.

## Tests with known issues:

- Classes.txt
- Main.txt
- ShadowBaseField
- test20
- test82
- test93
- test99: 


- BinaryTree: wrong field offsets because it contains types of current class
- LinkedList: same
- TreeVisitor: same
