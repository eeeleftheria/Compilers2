# MiniJava Static Checking (Semantic Analysis)

## Implementation

- semantic errors that ST generator visitor detects:
    - Double declaration of class

    - Double declaration of method's local or that exists as a parameter

    - Double declaration of a function with the EXACT same parameters

    - check subtype/supertype relation of args in second visitor so all methods will
    have already been added

    - getReturnTypeOfMethod: takes as input only the types of the parameters in order to find the method. 
    Initially i passed both the types and the names in the method decl, while in the message send it was correct.


## TODO

- Override functions

- implementation of identifier visit does not let me throw exception
in assignment statement when either of the two sides was not declared,
because it returns the name and not null

- offsets are only correct for fields and not methods:
for methods, overriden methods have the same offset as
the parent's method

## Tests:

- Classes.txt
- Main.txt
- ShadowBaseField
- test20
- test82
- test93
- test99: how can i know size of class which has a field of
the same type?  


- BinaryTree: wrong field offsets because it contains types of current class
- LinkedList: same
- TreeVisitor: same




    

    


## How to Run

```bash
cd minijava
```
### Compile
```bash
make
```

### Run
Runs with `Example.java` as input.
```bash
make run
```

Runs all the error cases in the directory `../testCases/minijava-error-extra`.
```bash
./runErrorTests.sh
```
The results can be viewed in `../Results/error-extra-results.txt`.

Runs all the extra offset examples in the directory `../testCases/minijava-extra`.
```bash
./runOffsetsExtra.sh
```
The results can be viewed in `../Results/offsets-extra-results.txt`.

### Clean
```bash
make clean
```
