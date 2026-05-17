# MiniJava Static Checking (Semantic Analysis)

## Implementation

- semantic errors that ST generator visitor detects:
 - Double declaration of class
 - Double declaration of method's local or that exists as a parameter
 - Double declaration of a function with the EXACT same parameters(todo: check only the types of the parameters, since foo(int a) and foo(int b) must also be considered equal)

## How to Run