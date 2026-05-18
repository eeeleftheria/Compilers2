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

    public void addAllParameters(String className, String method, String pars){
        symbolTable.get(className).addAllParameters(method, pars);

    }
    // adds a local field to the method symbol
    public void addMethodLocal(String className, String method, String name, String type, String pars){
        symbolTable.get(className).addLocalField(method, type, name, pars);
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

    // FUNCTIONS USED FOR TYPE CHECKING

    public String getTypeOfField(String className, String var){
        return symbolTable.get(className).getTypeOfField(var);
    }

    public String getTypeOfLocal(String className, String var, String method, String args){
        return symbolTable.get(className).getTypeOfLocal(var, method, args);
    }

    public String getTypeOfParameter(String classn, String var, String method, String args){
        return symbolTable.get(classn).getTypeOfParameter(method, var, args);
    }


    // returns true if type b is sybtype of a, else false
    public boolean isSubtype(String a, String b){

        if(isTypeClass(a) && isTypeClass(b)){
            ClassSymbol aclass = symbolTable.get(a);
            ClassSymbol bclass = symbolTable.get(b);
            
            // if it is the same class
            if(a.equals(b)){
                return true;
            }
            
            // check inheritance:
            // if b extends a: true
            // if we have: class A, class B extends A, class C extends B
            // if A a = new C(): true
            else{
                ClassSymbol curr = bclass;
                ClassSymbol left = aclass;

                while(!curr.getParentClass().isEmpty()){
                    if(curr.getParentClass().equals(left.getName())){
                        return true;
                    }

                    // get parent of right class
                    curr = symbolTable.get(curr.getParentClass());
                }
            }

            return false;

        }

        // if one of the two is a class and the other not, then it is not a subclass
        else if(isTypeClass(a) && !isTypeClass(b) || isTypeClass(b) && !isTypeClass(a)){
            return false;
        }

        else if(isPrimitive(b) && isPrimitive(a)){
            if(a.equals(b))
                return true;

            return false;
        }

        return false;
    }

    private boolean isPrimitive(String type){

        if(type.equals("int") || type.equals("boolean") || type.equals("int[]"))
            return true;
       
        return false;
    }

    // returns true if the given type is a class name, else false
    public boolean isTypeClass(String var){

        return symbolTable.containsKey(var);
    }

    public boolean containsClass(String c){
        return symbolTable.containsKey(c);
    }

    public boolean containsMethodLocal(String classn, String methodn, String local, String pars){

        return symbolTable.get(classn).containsMethodLocal(methodn, local, pars);
    }

    public boolean containsMethod(String classn, String methodn, String pars){

        return symbolTable.get(classn).containsMethod(methodn, pars);
    }

    public boolean containsMethodWithTypes(String classn, String methodn, String pars){

        return symbolTable.get(classn).containsMethodWithTypes(methodn, pars);
    }

    public String getReturnTypeOfMethod(String classn, String method, String pars){
        return symbolTable.get(classn).getReturnTypeOfMethod(method, pars);
    }



}