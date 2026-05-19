#!/bin/bash

make
outputFile="../Results/error-extra-results.txt"
mkdir -p ../Results
> "$outputFile"  # Clear the file
for file in ../testCases/minijava-error-extra/*.java; do
    echo "#####################" >> "$outputFile"
    echo "Test: $(basename "$file")" >> "$outputFile"
    echo "#####################" >> "$outputFile"
    java -cp .:../build Main "$file" >> "$outputFile"
done
