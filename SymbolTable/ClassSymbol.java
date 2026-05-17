
package SymbolTable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

public class ClassSymbol{
    
    private String name;
    private String parentClass;

    private Vector<FieldSymbol> fields; // vector with all the fields of a specific class
    private Vector<MethodSymbol> methods; // vector with all the methods of a specific class
    
    // default constructor
    public ClassSymbol(){
        // default initial capacity is 10       
        fields = new Vector<>(); 
        methods = new Vector<>(); 
        name = "";
        parentClass = "";
    }

    public void setParentClass(String parent){
        parentClass = parent;
    }

    public String getParentClass(){
        return parentClass;
    }

    public void addField(String name, String type){

        FieldSymbol fs = new FieldSymbol();
        fs.setName(name);
        fs.setType(type);

        fields.add(fs);
    }

    public void addMethod(String name, String returnType){

        MethodSymbol ms = new MethodSymbol();
        ms.setName(name);
        ms.setReturnType(returnType);

        methods.add(ms);
    }

    public void setName(String n){
        name = n;
    }

    // returns true if the methods was found and the parameter was inserted successfully.
    // pars corresponds to the current parameters that have been already added to the Symbol Table
    private boolean addParam(String method, String type, String name, String pars){
       
        MethodSymbol foundMethod = getMethod(method, pars);
       
        if (foundMethod != null){
            foundMethod.addParam(name, type);
            return true;
        }
        else{
            return false;
        }
    }

    // Given a method name and a string with all of its parameters it adds them one by one to the ST.
    // Returns true if the parameters were added successfully, else false
    public boolean addAllParameters(String method, String pars){
        
        // we need to search for the method with the corresponding name
        // and which has no assigned parameters yet.
        MethodSymbol foundMethod = getMethodByName(method);
        
        if (foundMethod != null){

            // split the string 
            String[] parts = pars.trim().split(" "); // this contains [type1, var1, type2, var2, ...]
           
            for(int i = 0; i < parts.length - 1; i += 2){

                foundMethod.addParam(parts[i+1], parts[i]);
            }

            return true;
        }
        else{
            return false;
        }
    }

    public boolean addLocalField(String method, String type, String name, String pars){
        MethodSymbol foundMethod = getMethod(method, pars);
       
        if (foundMethod != null){
            foundMethod.addLocalField(name, type);
            return true;
        } 
        else{
            return false;
        }
    }

    // searches for the specific method with the specific parameters (format: type par type par ...)
    // in case of overloading and returns the methodSymbol if found, else null
    public MethodSymbol getMethod(String methodName, String parameters){
      
        for(MethodSymbol m : methods){
            if(m.getName().equals(methodName)){
                
                String pars = m.getParametersString2();

                // if a method with the exact same parameters was found, return it
                if(pars.equals(parameters)){
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

     // FUNCTIONS USED FOR TYPE CHECKING
     
    public String getTypeOfField(String var){
        
        for(FieldSymbol f: fields){
            if(f.getName().equals(var)){
                return f.getType();
            }
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
}
