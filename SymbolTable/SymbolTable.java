package SymbolTable;

import java.util.HashMap;

public class SymbolTable{
    
    private HashMap<String, ClassSymbol> symbolTable;

    public SymbolTable(){
        symbolTable = new HashMap<>(100);
    }

    // inserts a new class to the symbol table
    public void addClass(String name){

        ClassSymbol cs = new ClassSymbol();
        cs.setName(name);

        symbolTable.put(name, cs);
    }

    // inserts a field to the class with name className
    public void addClassField(String className, String field, String type){
        symbolTable.get(className).addField(field, type);
    }

    // inserts a method to the class with name className
    public void addClassMethod(String className, String method, String returnType){
        symbolTable.get(className).addMethod(method, returnType);
    }

    public void addMethodParam(String className, String method, String name, String type){
        symbolTable.get(className).addParam(method, type, name);
    }

    public void printSymbolTable(){
        for(String key: symbolTable.keySet()){

            ClassSymbol temp = symbolTable.get(key);

            System.out.println("Class: " + key);
            System.out.println("--------------");
            temp.printClassSymbol();

            System.out.println("");
        }
    }



}