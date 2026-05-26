#!/bin/bash

make
outputFile="../Results/offsets-extra-results.txt"
mkdir -p ../Results
> "$outputFile"  # Clear the file
for file in ../testCases/minijava-extra/*.java; do
    echo "#####################" >> "$outputFile"
    echo "Test: $(basename "$file")" >> "$outputFile"
    echo "#####################" >> "$outputFile"
    java -cp .:../build Main "$file" >> "$outputFile"
done
