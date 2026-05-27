#!/bin/bash

make
outputFile="../Results/offsets-results.txt"
mkdir -p ../Results
> "$outputFile"  # Clear the file
for file in ../testCases/*.java; do
    echo "#####################" >> "$outputFile"
    echo "Test: $(basename "$file")" >> "$outputFile"
    echo "#####################" >> "$outputFile"
    java -cp .:../build Main "$file" >> "$outputFile"
done
