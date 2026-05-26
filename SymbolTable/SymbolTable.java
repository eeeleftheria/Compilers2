package SymbolTable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class SymbolTable{
    
    private LinkedHashMap<String, ClassSymbol> symbolTable;

    public SymbolTable(){
        symbolTable = new LinkedHashMap<>(100);
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
        
        System.out.println("========SYMBOL TABLE========\n");
        
        for(String key: symbolTable.keySet()){

            ClassSymbol temp = symbolTable.get(key);
            String parent = temp.getParentClass();

            System.out.println("Class: " + key + " " + (parent.isEmpty() ? "" : "extends ") + parent);
            System.out.println("--------------");
            temp.printClassSymbol();
            System.out.println("\n");

        }
        System.out.println("============================");
    }

    // FUNCTIONS USED FOR TYPE CHECKING

    private String getTypeOfField(String className, String var){
        return symbolTable.get(className).getTypeOfField(var);
    }

    private String getTypeOfLocal(String className, String var, String method, String args){
        return symbolTable.get(className).getTypeOfLocal(var, method, args);
    }

    private String getTypeOfParameter(String classn, String var, String method, String args){
        return symbolTable.get(classn).getTypeOfParameter(method, var, args);
    }

    private String getParentClass(String classn){
        return symbolTable.get(classn).getParentClass();
    }

    // Check recursively the type of the variable:
    // 1) check if it is a method local or a parameter
    // 2) if not, check if it is a field of the current class
    // 3) if not, check parent
    public String getType(String classn, String var, String method, String args, Boolean firstClass){
        
        String type = null;

        // if we are checking a parent class, we should only check
        // its fields, and not its methods
        if(firstClass == true){

            // in case of a method local 
            if(method != null && !method.isEmpty())
                type = getTypeOfLocal(classn, var, method, args);
    
            // in case of a method parameter 
            if(type == null && method != null && !method.isEmpty()){
                type = getTypeOfParameter(classn, var, method, args);
            }
        }

        // in case of a class field
        if(type == null){
        
            type = getTypeOfField(classn, var);
        }
        
        // end of recursion if the variable was found
        if(type != null){
            return type;
        }

        // if type is still null, it was not found inside of the current class
        // so check parent recursively
        String parent = getParentClass(classn);
        // if the class has no parent, we have reached end of recursion
        if(parent.equals("")){
            return null;
        }

        return getType(parent, var, method, args, false);

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

    // check if the current class contains the method,
    // if not, check parent class recursively
    public boolean containsMethodWithTypes(String classn, String methodn, String pars){

        if(symbolTable.get(classn).containsMethodWithTypes(methodn, pars) == false){
            String parent = symbolTable.get(classn).getParentClass();
            
            // if the class has a parent, check recursively
            // for the method
            if(!parent.equals(""))
                return containsMethodWithTypes(parent, methodn, pars);
            else
                return false;

        }
        else{

            return true;
        }
    }

    public String getReturnTypeOfMethod(String classn, String method, String pars){
       
        String res = symbolTable.get(classn).getReturnTypeOfMethod(method, pars);

        if(res == null){
            String parent = symbolTable.get(classn).getParentClass();

            // check recursively for the method inside the parent class
            if(!parent.equals(""))
                return getReturnTypeOfMethod(parent, method, pars);
            else
                return null;
        }
        else
            return res;
    }

    // check if new method can overload: must differ from existing methods by at least one argument position
    public boolean checkOverloadedMethod(String classn, String methodn, String pars){
        Vector<MethodSymbol> funcs = symbolTable.get(classn).checkOverloadedMethods(methodn, pars);
        
        // check each existing method with same name and number of parameters
        for(MethodSymbol m: funcs){
            String currPars = m.getParametersString2();
            if(currPars == null){
                currPars = "";
            }
            
            // Format: "type var type var ..."
            String[] parts1 = currPars.split(" ");
            String[] parts2 = pars.split(" ");

            // check if at least one argument position is not related (subtype/supertype)
            boolean hasUnrelatedPosition = false;
            for(int i = 0; i < parts1.length; i=i+2){
               
                if((!isSubtype(parts1[i], parts2[i])) && (!isSubtype(parts2[i], parts1[i]))){
                    hasUnrelatedPosition = true;
                    break;
                }
            }
            
            // if all positions are related, this method cannot be overloaded
            if(hasUnrelatedPosition == false){
                return false;
            }
        }

        // this point is reached only if there is no 
        // function that creates a conflict with the one we want to add
        return true;
    }




}