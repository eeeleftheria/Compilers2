# MiniJava Static Checking (Semantic Analysis)

## Implementation

- semantic errors that ST generator visitor detects:
    - Double declaration of class
    - Double declaration of method's local or that exists as a parameter
    - Double declaration of a function with the EXACT same parameters

- tests to check:


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
