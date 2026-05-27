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

- If statement

- While statement

- Block

- Override functions

- implementation of identifier visit does not let me throw exception
in assignment statement when either of the two sides was not declared,
because it returns the name and not null

- in TC visitor in messageSend when checking if a method exists, it checks
for the EXACT same types and not for subtypes. I dont know how to handle it for now
since i have to move the whole logic in the symbol table file so i can have access to 
all kinds of types.

- offsets are only correct for fields and not methods:
for methods, overriden methods have the same offset as
the parent's method

## Tests:

- derivedCall: MessageSend error: class F does not contain method foo( B) because foo takes A which is the parent. This is described
above

- test99: how can i know size of class which has a field of
the same type?  




    

    


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
