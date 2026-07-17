# MiniJava Static Checking (Semantic Analysis)

This project consists of building a parser and semantic analyzer for MiniJava, a subset of Java. MiniJava is designed so that its programs can be compiled by a full Java compiler like javac.

Here is a partial, textual description of the language. Much of it can be safely ignored (most things are well defined in the grammar or derived from the requirement that each MiniJava program is also a Java program):

MiniJava is fully object-oriented, like Java. It does not allow global functions, only classes, fields and methods. The basic types are int, boolean, and int [] which is an array of int. You can build classes that contain fields of these basic types or of other classes. Classes contain methods with arguments of basic or class types, etc.

MiniJava supports single inheritance but not interfaces. It supports limited function overloading: multiple methods with the same name may coexist in a class (or across a class and its parent) provided they are unambiguously distinguishable. Specifically:

Methods with the same name but different numbers of arguments are always allowed.
Methods with the same name and the same number of arguments are allowed only if there is at least one argument position for which the type in one method is neither a subtype nor a supertype of the corresponding type in the other. If all argument positions have a subtype/supertype relationship between the two methods, it is an error — unless all argument types are exactly the same and the two methods are defined in different classes (one a superclass, the other a subclass), in which case the name match is treated as an override and the subclass method must have the same return type as in the ancestor class.
All methods are inherently polymorphic (i.e., “virtual” in C++ terminology). This means that a method foo can be defined in a subclass with the same return type and argument types (ordered) as in the parent (an override). Otherwise a definition of a method with the same name should respect the overloading rules (as described above) and is considered an entirely different method. Overloaded methods may have different return types. All methods must have a return type — there are no void methods. Fields in the base and derived class are allowed to have the same names, and are essentially different fields.
All MiniJava methods are “public” and all fields “protected”. A class method cannot access fields of another class, with the exception of its superclasses. Methods are visible, however. A class’s own methods can be called via this. E.g., this.foo(5) calls the object’s own foo method, a.foo(5) calls the foo method of object a. Local variables are defined only at the beginning of a method. A name cannot be repeated in local variables (of the same method) and cannot be repeated in fields (of the same class). A local variable x shadows a field x of the surrounding class.

In MiniJava, constructors and destructors are not defined. The new operator calls a default void constructor. In addition, there are no inner classes and there are no static methods or fields. By exception, the pseudo-static method main is handled specially in the grammar. A MiniJava program is a file that begins with a special class that contains the main method and specific arguments that are not used. The special class has no fields. After it, other classes are defined that can have fields and methods.
Notably, an A class can contain a field of type B, where B is defined later in the file. But when we have class B extends A, A must be defined before B. As you’ll notice in the grammar, MiniJava offers very simple ways to construct expressions and only allows < comparisons. There are no lists of operations, e.g., 1 + 2 + 3, but a method call on one object may be used as an argument for another method call. In terms of logical operators, MiniJava allows the logical and (&&) and the logical not (!). For int arrays, the assignment and [] operators are allowed, as well as the a.length expression, which returns the size of array a. We have while and if code blocks. The latter are always followed by an else. Finally, the assignment A a = new B(); when B extends A is correct, and the same applies when a method expects a parameter of type A and a B instance is given instead.

The MiniJava grammar in BNF can be downloaded here. You can make small changes to grammar, but you must accept everything that MiniJava accepts and reject anything that is rejected by the full Java language. Making changes is not recommended because it will make your job harder in subsequent homework assignments. Normally you won’t need to touch the grammar.

The MiniJava grammar in JavaCC form is here. You will use the JTB tool to convert it into a grammar that produces class hierarchies. Then you will write one or more visitors who will take control over the MiniJava input file and will tell whether it is semantically correct, or will print an error message. It isn’t necessary for the compiler to report precisely what error it encountered and compilation can end at the first error. But you should not miss errors or report errors in correct programs.

The visitors you will build should be subclasses of the visitors generated by JTB, but they may also contain methods and fields to hold information during static checking, to transfer information from one visitor to the next, etc. In the end, you will have a Main class that runs the semantic analysis initiating the parser that was produced by JavaCC and executing the visitors you wrote. You will turn in your grammar file, if you have made changes, otherwise just the code produced by JavaCC and JTB alongside your own classes that implement the visitors, etc. and a Main. The Main should parse and statically check all the MiniJava files that are given as arguments.

Also, for every MiniJava file, your program should store and print some useful data for every class such as the names and the offsets of every field and method this class contains. For MiniJava we have only three types of fields (int, boolean, and pointers). Ints are stored in 4 bytes, booleans in 1 byte and pointers in 8 bytes (we consider functions and int arrays as pointers). Corresponding offsets are shown in the example below:

Input:

```java
 class A{
      int i;
      boolean flag;
      int j;
      public int foo() {}
      public boolean fa() {}
  }

  class B extends A{
      A type;
      int k;
      public int foo() {}
      public boolean bla() {}
  }
```
Output:

```bash
  A.i : 0
  A.flag : 4
  A.j : 5
  A.foo : 0
  A.fa: 8
  B.type : 9
  B.k : 17
  B.bla : 16
```
  
## Implementation

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

### Offsets

#### Method offsets
To implement the offset logic for the methods, I implemented the following function in the `SymbolTable.java` file:

   The `calculateMethodOffset` function is called inside `addMethodOffset()` function of the symbol table visitor as as the final step for the method's declaration. The function detects the root of the hierarchy, meaning it reaches the class with no parent. Then, starting from the root and going down, it checks if the current class contains the method we are trying to calculate the offset for. The first time it finds the method, it keeps that offset and returns it. If no method was found in the parent classes it returns -1, meaning the offset should be calculated in a cumulative way. 

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


- Assignment statement: more specific errors, like throwing an exception
when either the lvalue or rvalue was not declared. Current implementation of identifier node
makes it unfeasible.

