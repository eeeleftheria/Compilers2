
package SymbolTable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

public class ClassSymbol{
    
    private String name;
    private String parentClass;

    private Vector<FieldSymbol> fields; // vector with all the fields of a specific class
    private Vector<MethodSymbol> methods; // vector with all the methods of a specific class

    private int totalFieldBytes;
    private int totalMethodBytes;
    
    // default constructor
    public ClassSymbol(String n){
        // default initial capacity is 10       
        fields = new Vector<>(); 
        methods = new Vector<>(); 
        name = n;
        parentClass = "";
        totalFieldBytes = 0;
        totalMethodBytes = 0;
    }

    public void setParentClass(String parent){
        parentClass = parent;
    }

    public String getParentClass(){
        return parentClass;
    }

    public int getTotalFieldBytes(){
        return totalFieldBytes;
    }

    public int getTotalMethodBytes(){
        return totalMethodBytes;
    }

    // if the class extends another one then
    // the initial offset is the parent's size of fields
    public void setTotalFieldBytes(int s){
        totalFieldBytes += s;
    }

    public void setTotalMethodBytes(int s){
        totalMethodBytes = s;
    }


    // pars = type var type var ...
    public void setMethodOffset(String meth, String types, int o){
        MethodSymbol m = getMethodWithTypes(meth, types);
        if(m != null){
            m.setOffset(o);
        }
    }

    public void addField(String name, String type, int typeSize){

        FieldSymbol fs = new FieldSymbol(name, type, totalFieldBytes);
        fields.add(fs);

        totalFieldBytes += typeSize;

    }

    public Vector<MethodSymbol> getMethods(){
        return new Vector<MethodSymbol>(methods);
    }

    public void addMethod(String name, String returnType){

        MethodSymbol ms = new MethodSymbol(name, returnType, totalMethodBytes);
        methods.add(ms);
    }


    // Given a method name and a string with all of its parameters it adds them one by one to the ST.
    // Returns true if the parameters were added successfully, else false
    public boolean addAllParameters(String method, String pars){
        
        // we need to search for the method with the corresponding name
        // and which has no assigned parameters yet.
        MethodSymbol foundMethod = getMethodByName(method);
        
        if (foundMethod != null){
            
            if(pars != null){
                
                // split the string 
                String[] parts = pars.trim().split(" "); // this contains [type1, var1, type2, var2, ...]
                
                for(int i = 0; i < parts.length - 1; i += 2){
                    
                    foundMethod.addParam(parts[i+1], parts[i]);
                }
                
            }
            return true;
        }
        else{
            return false;
        }
    }

    public boolean addLocalField(String method, String type, String name, String pars){
        MethodSymbol foundMethod = getMethod(method, pars);
       
        if(foundMethod != null){
            foundMethod.addLocalField(name, type);
            return true;
        } 
        else{
            return false;
        }
    }

    // searches for the specific method with the specific parameters (both name and types with format: type par type par ...)
    // in case of overloading and returns the methodSymbol if found, else null
    public MethodSymbol getMethod(String methodName, String parameters){

        for(MethodSymbol m : methods){
            if(m.getName().equals(methodName)){
                
                String pars = m.getParametersString2();
                
                // handle null parameters from symbol table
                if(pars == null){
                    pars = "";
                }

                // both strings containing types have the format: "type var type var ..."
                String[] parts1 = pars.split(" ");
                String[] parts2 = parameters.split(" ");

                boolean allTypesEqual = true;

                if(parts1.length == parts2.length){
                    
                    for(int i = 0; i < parts1.length; i=i+2){

                        // if there is at least one type not equal, then the methods are different
                        if(!parts1[i].equals(parts2[i])){
                            allTypesEqual = false;
                            break;
                        }
                    }

                    if(allTypesEqual == true){
                        return m;
                    }
                }
            }
        }
        return null;
    }

    // searches for the specific method with the specific parameter types(format: type1 type2 ...)
    // in case of overloading and returns the methodSymbol if found, else null
    public MethodSymbol getMethodWithTypes(String methodName, String parameters){
      
        for(MethodSymbol m : methods){
            if(m.getName().equals(methodName)){
                
                String pars = m.getParametersString2();
                
                // handle null parameters from both sources
                if(pars == null){
                    pars = "";
                }
                if(parameters == null){
                    parameters = "";
                }
                
                // if both are empty, we found a method with no parameters
                if(pars.isEmpty() && parameters.isEmpty()){
                    return m;
                }
                
                // skip if one is empty and the other is not
                if(pars.isEmpty() || parameters.isEmpty()){
                    continue;
                }

                String[] parts_original = pars.split(" ");
                String[] parts_tocheck = parameters.split(" ");
                
                // if the number of types are not equal for both then 
                // the method is not the desired one
                if(parts_original.length/2 != parts_tocheck.length){
                    continue;
                }
                
                // if a method with the exact same parameter types was found, return it
                boolean equal = true;
                int j = 0;
                for(int i = 0; i < parts_original.length; i=i+2){
                   
                   // all parameters have to have the same type
                    if(!parts_original[i].equals(parts_tocheck[j])){
                        equal = false;
                        break;
                    }
                    j++;
                }

                if(equal == true){
                    return m;
                }
                
            }
        }
        return null;
    }

    // searches for the method with the given name and with no parameters yet
    public MethodSymbol getMethodByName(String methodName){
    
        for(MethodSymbol m : methods){
            if(m.getName().equals(methodName)){
                
                String pars = m.getParametersString2();

                // return the method with no assigned parameters
                if(pars.isEmpty()){
                    return m;
                }
                
            }
        }
        return null;
    }


     // FUNCTIONS USED FOR TYPE CHECKING
     
    public String getTypeOfField(String var){
        
        for(FieldSymbol f: fields){
            if(f.getName().equals(var)){
                return f.getType();
            }
        }
        return null;
    }   

    public String getTypeOfLocal(String var, String method, String args){
        MethodSymbol m = getMethod(method, args);
        if(m != null){
            return m.getTypeOfLocal(var);
        }
        return null;
    }

    public String getTypeOfParameter(String var, String method, String args){
        MethodSymbol m = getMethod(method, args);
        if(m != null){
            return m.getTypeOfParameter(var);
        }
        return null;
    }

    public String getName(){
        return name;
    }

    // returns true if the class contains a method with parameters pars and the given local field
    public boolean containsMethodLocal(String methodn, String local, String pars){
        return getMethod(methodn, pars).containsLocal(local);
    }

    public boolean containsMethod(String methodn, String pars){

        MethodSymbol m = getMethod(methodn, pars);
        if(m != null){
            return true;
        }

        return false;
    }


    // returns a vector with all methods with the same name and the same number of parameters
    public Vector<MethodSymbol> checkOverloadedMethods(String methodName, String parameters){

        Vector<MethodSymbol> overloadedFuncs = new Vector<MethodSymbol>();
        
        for(MethodSymbol m: methods){

            // 1. find methods with the same name
            if(m.getName().equals(methodName)){
                
                String pars = m.getParametersString2();
                
                // handle null parameters from symbol table
                if(pars == null){
                    pars = "";
                }
                
                // both strings containing types have the format: "type var type var ..."
                String[] parts1 = pars.split(" ");
                String[] parts2 = parameters.split(" ");
                
                // add the method to the vector if both have the same num of arguments
                // If the parameters are the exact same, dont calculate as an overloaded
                // func because the method is refering to the one we are checking, since
                // it has already been added to the symbol table from the 1st visitor
                if((parts1.length == parts2.length) && !(pars.equals(parameters))){
                    overloadedFuncs.add(m);
                }
            }
        }
    
        return overloadedFuncs;
    }

    public void printClassSymbol(){
        System.out.println("Fields: ");

        // print class fields
        for(FieldSymbol item: fields){
            System.out.println("  "+ item.getName() + " " + item.getType());
        }

        System.out.println("Methods: ");

        // print class methods and their parameters
        for(MethodSymbol item: methods){
            System.out.println("  " + item.getReturnType() + " " + item.getName() + item.getParametersString());

            System.out.println("    Local fields:");

            // print the local fields of each method
            for(FieldSymbol f: item.getLocals()){
                System.out.println("       " + f.getName() + " " + f.getType());
            }
        }

    }


    public void printOffsetVariables(){
        for(FieldSymbol item: fields){
            System.out.println(name + "."+ item.getName() + " : " + item.getOffset());
        }
        
    }

    public void printOffsetMethods(){
        for(MethodSymbol item: methods){
            System.out.println(name + "."+ item.getName() + " : " + item.getOffset());
        }
        
    }
    
}


