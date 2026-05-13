
package SymbolTable;

import java.lang.reflect.Field;
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
    }

    public void setParentClass(String parent){
        parentClass = parent;
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

    // returns true if the methods was found and the parameter was inserted successfully
    public boolean addParam(String method, String type, String name){
       
        MethodSymbol foundMethod = getMethod(method);
       
        if (foundMethod != null) {
            foundMethod.addParam(name, type);
            return true;
        } else {
            return false;
        }
    }

    // returns MethodSymbol with name methodName if found, else null
    public MethodSymbol getMethod(String methodName){
        for(MethodSymbol m : methods){
            if(m.getName().equals(methodName)){
                return m;
            }
        }
        return null;
    }

    public void printClassSymbol(){
        System.out.println("Fields: ");

        for(FieldSymbol item: fields){
            System.out.println("  "+ item.getName() + " " + item.getType());
        }

        System.out.println("Methods: ");

        for(MethodSymbol item: methods){
            System.out.println("  " + item.getReturnType() + " " + item.getName() + item.getParametersString());
           
        }

    }

}
