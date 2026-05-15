package SymbolTable;

import java.util.HashMap;

public class SymbolTable{
    
    private HashMap<String, ClassSymbol> symbolTable;

    public SymbolTable(){
        symbolTable = new HashMap<>(100);
    }

    // inserts a new class to the symbol table, along with its parent if it is a subclass
    public void addClass(String name){

        ClassSymbol cs = new ClassSymbol();
        cs.setName(name);

        symbolTable.put(name, cs);
    }

    // if the class extends another one, store its parent
    public void addParentClass(String className, String parent){
        symbolTable.get(className).setParentClass(parent);
    }

    // inserts a field to the class with name className
    public void addClassField(String className, String field, String type){
        symbolTable.get(className).addField(field, type);
    }

    // inserts a method to the class with name className
    public void addClassMethod(String className, String method, String returnType){
        symbolTable.get(className).addMethod(method, returnType);
    }

    // adds a parameter to the method symbol
    public void addMethodParam(String className, String method, String name, String type){
        symbolTable.get(className).addParam(method, type, name);
    }
    // adds a local field to the method symbol
    public void addMethodLocal(String className, String method, String name, String type){
        symbolTable.get(className).addLocalField(method, type, name);
    }

    public void printSymbolTable(){
        
        System.out.println("========SYMBOL TABLE========");
        
        for(String key: symbolTable.keySet()){

            ClassSymbol temp = symbolTable.get(key);
            String parent = temp.getParentClass();

            System.out.println("Class: " + key + " " + (parent.isEmpty() ? "" : "extends ") + parent);
            System.out.println("--------------");
            temp.printClassSymbol();

            System.out.println("");
        }
    }



}