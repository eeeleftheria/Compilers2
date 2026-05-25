# MiniJava Static Checking (Semantic Analysis)

## Implementation

- semantic errors that ST generator visitor detects:
    - Double declaration of class
    - Double declaration of method's local or that exists as a parameter
    - Double declaration of a function with the EXACT same parameters

- tests to check:


-TODO
    - offsets
    - If statement
    - While statement
    - Block
    - Override functions and fields

    - check for same names of parameters

- notes:
    - getReturnTypeOfMethod: takes as input only the types of the parameters in order to find the method. 
    Initially i passed both the types and the names in the method decl, while in the message send it was correct.

    - check subtype/supertype relation of args in second visitor so all methods will
    have already been added

    

    


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

### Clean
```bash
make clean
```
