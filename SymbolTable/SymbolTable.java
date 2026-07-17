package SymbolTable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class SymbolTable{
    
    private LinkedHashMap<String, ClassSymbol> symbolTable;
    private String mainClass; 

    public SymbolTable(){
        symbolTable = new LinkedHashMap<>(100);
    }


    // inserts a new class to the symbol table
    public void addClass(String name){

        ClassSymbol cs = new ClassSymbol(name);
        symbolTable.put(name, cs);
    }

    // sets the name of the main class
    public void setMainClass(String name){
        mainClass = name;
    }

    // if the class extends another one, store its parent
    public void addParentClass(String className, String parent){
        
        symbolTable.get(className).setParentClass(parent);
        
        // the fields of the child class, should have as offset
        // the fields' size of the parent classes
        int bytes = symbolTable.get(parent).getTotalFieldBytes();
        symbolTable.get(className).setTotalFieldBytes(bytes);
       
        // ???? not implemented correctly yet
        int bytes2 = symbolTable.get(parent).getTotalMethodBytes();
        symbolTable.get(className).setTotalMethodBytes(bytes2);
        
    }

    // inserts a field to a class
    public void addClassField(String className, String field, String type, int typeSize){
        symbolTable.get(className).addField(field, type, typeSize);
    }

    // inserts a method to a class
    public void addClassMethod(String className, String method, String returnType){
        symbolTable.get(className).addMethod(method, returnType);
    }

    // adds the parameters of a class' method and stores the appropriate offset for the method
    public void addAllParameters(String className, String method, String pars){
        symbolTable.get(className).addAllParameters(method, pars);
    }

    public void addMethodOffset(String className, String method, String pars){

        // now that the method to add has parameters
        // check if it is an override: if yes
        // keep the offset of the original function
        int off = calculateMethodOffset(className, method, pars);

        // no overriden method
        if(off == -1){
            off = symbolTable.get(className).getTotalMethodBytes();
            symbolTable.get(className).setTotalMethodBytes(off + 8);
        }

        String[] parts = pars.split(" ");
        String types = "";
        for(int i = 0; i < parts.length; i=i+2){
            types += parts[i];
            if(i < parts.length - 1){
                types += " ";
            }
        }
        symbolTable.get(className).setMethodOffset(method, types, off);
    }

    // adds a local field to a method
    public void addMethodLocal(String className, String method, String name, String type, String pars){
        symbolTable.get(className).addLocalField(method, type, name, pars);
    }


    public void printSymbolTable(){
        
        if(symbolTable.size() > 1)
            System.out.println("========SYMBOL TABLE========\n");

        for(String key: symbolTable.keySet()){

            if(key.equals(mainClass))
                continue;

            ClassSymbol temp = symbolTable.get(key);
            String parent = temp.getParentClass();

            System.out.println("Class: " + key + " " + (parent.isEmpty() ? "" : "extends ") + parent);
            System.out.println("--------------");
            temp.printClassSymbol();
            System.out.println("\n");

        }
        if(symbolTable.size() > 1)
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
        return symbolTable.get(classn).getTypeOfParameter(var, method, args);
    }

    private String getParentClass(String classn){
        return symbolTable.get(classn).getParentClass();
    }

    // Check recursively the type of the variable:
    // 1) check if it is a method local or a parameter
    // 2) if not, check if it is a field of the current class
    // 3) if not, check parent
    // args format: type var type var ...
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
                ClassSymbol superClass = aclass;

                while(!curr.getParentClass().isEmpty()){
                    if(curr.getParentClass().equals(superClass.getName())){
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

    public int getSizeOfField(String type){

        if(isPrimitive(type)){
            if(type.equals("int"))
                return 4;
            
            else if(type.equals("boolean"))
                return 1;
            
            else if(type.equals("int[]"))
                return 8;
        }
        // object reference (pointer 8 bytes)
        else if(isTypeClass(type)){
            return 8;
        }

        return 0;
    }

    public boolean containsClass(String c){
        return symbolTable.containsKey(c);
    }

    public boolean containsMethodLocal(String classn, String methodn, String local, String pars){

        return symbolTable.get(classn).containsMethodLocal(methodn, local, pars);
    }

    public boolean containsClassField(String classn, String var){

        return symbolTable.get(classn).containsClassField(var);
    }

    public boolean containsMethod(String classn, String methodn, String pars){

        return symbolTable.get(classn).containsMethod(methodn, pars);
    }

    
    // searches for the specific method with the specific parameter types(format: type1 type2 ...)
    // in case of overloading and returns the methodSymbol if found, else null
    public MethodSymbol getMethodWithTypes(String classn, String methodName, String parameters){
      
        for(MethodSymbol m : symbolTable.get(classn).getMethods()){
            if(m.getName().equals(methodName)){
                
                // contains: type var type var ...
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
                
                // if a method with the exact same parameter types/or subtypes was found, return it
                boolean equal = true;
                int j = 0;
                for(int i = 0; i < parts_original.length; i=i+2){
                   
                   // all parameters have to have the same type or the type
                   // of the argument we are calling the function with must be 
                   // a subtype of the original parameter
                    if(!isSubtype(parts_original[i], parts_tocheck[j])){
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


    // check if the current class contains the method,
    // if not, check parent classes recursively
    public boolean containsMethodWithTypes(String classn, String methodn, String pars){

        MethodSymbol m = getMethodWithTypes(classn, methodn, pars);
        if(m == null){
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


    // Checks recursively for the method, and returns its return type.
    // If it does not exist in current class, it checks its parent classes
    public String getReturnTypeOfMethod(String classn, String method, String pars){
        
        MethodSymbol m = getMethodWithTypes(classn, method, pars);
        if(m == null){
            String parent = symbolTable.get(classn).getParentClass();

            // check recursively for the method inside the parent class
            if(!parent.equals(""))
                return getReturnTypeOfMethod(parent, method, pars);
            else
                return null;
        }
        else
            return m.getReturnType();
    }

    // check if new method can overload: must differ from existing methods by at least one argument position
    // returns true if the method can be overloaded, else false
    public boolean checkOverloadedMethod(String classn, String methodn, String pars){

        // a vector with all methods of the same name and same number of arguments:
        // this kind of overloading must satisfy the need for at least one argument position
        // to have a different type between the two methods
        Vector<MethodSymbol> funcs = symbolTable.get(classn).checkOverloadedMethods(methodn, pars);
        
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

    
    public int calculateMethodOffset(String classn, String methodn, String pars){

        String parent = symbolTable.get(classn).getParentClass();
        Vector<String> hierarchy = new Vector<String>(5);

        String[] parts = pars.split(" ");
        String types = "";
        for(int i = 0; i < parts.length; i = i + 2){
            if(i > 0) 
                types += " ";
            types += parts[i];
        }

        // first we need to find the root of the hierarchy
        while(!parent.equals("")){
            hierarchy.add(parent);
            classn = parent;
            parent = symbolTable.get(classn).getParentClass();
        }
        
        // now we should start searching for the method, 
        // from the root and below until we find it 
        for(int i = hierarchy.size() - 1; i >= 0; i--){
            String curr = hierarchy.get(i);

            // if we find the method return its offset
            if(containsMethodWithTypes(curr, methodn, types)){
                return symbolTable.get(curr).getMethodWithTypes(methodn, types).getOffset();
            }
        }
        
        // in this case we do not have any overridden method
        return -1;
    }

    public void printOffsets(){
        if(symbolTable.size() > 1)
            System.out.println("======== OFFSETS ========\n");
        
        for(String key: symbolTable.keySet()){

            ClassSymbol temp = symbolTable.get(key);

            if(key.equals(mainClass))
                continue;

            System.out.println("-----------Class: " + key + "-----------");
            System.out.println("---Variables---");
            temp.printOffsetVariables();
            System.out.println("---Methods---");
            temp.printOffsetMethods();

            System.out.println("\n");

        }
        if(symbolTable.size() > 1)
            System.out.println("============================");
    }




}
