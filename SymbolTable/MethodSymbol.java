package SymbolTable;

import java.lang.reflect.Method;
import java.util.Vector;

public class MethodSymbol{
    
    private String name;
    private String returnType; 
    private int offset;
    private Vector<String> parametersTypes; // type of each parameter
    private Vector<String> parametersList; // names of the parameters
    private Vector<FieldSymbol> localFields; // local fields of method

    public MethodSymbol(String n, String retType, int o){

        parametersTypes = new Vector<>();
        parametersList = new Vector<>();
        localFields = new Vector<>();
        name = n;
        returnType = retType;
        offset = o;
    }

    //#### GETTERS AND SETTERS ####//

    public String getName(){
        return name;
    }

    public String getReturnType(){
        return returnType;
    }

    public int getNumOfArgs(){
        return parametersList.size();
    }

    public int getOffset(){
        return offset;
    }

    public void setOffset(int o){
        offset = o;
    }

    public Vector<FieldSymbol> getLocals(){

        // !! return copy of vector (objects' value is copied to a reference in java)
        return new Vector<FieldSymbol>(localFields);
    }


    // adds a new parameters with name par and type 
    public void addParam(String par, String type){
        parametersList.add(par);
        parametersTypes.add(type);
    }

    public void addLocalField(String name, String type){
        FieldSymbol fs = new FieldSymbol(name, type, 0);

        localFields.add(fs);
    }

    // returns a string of format: (type1 par1, type2 par2 ...)
    public String getParametersString(){

        String res = "(";

        for(int i = 0; i < parametersList.size(); i++){
            String par = parametersList.get(i);
            String type = parametersTypes.get(i);

            res += (type + " " + par);
            
            if(i < parametersList.size() - 1){
                res += ", ";
            }
        }

        res += ")";
        return res;
    }

    // returns a string of format: type1 par1 type2 par2 ...
    public String getParametersString2(){

        String res = "";

        for(int i = 0; i < parametersList.size(); i++){
            String par = parametersList.get(i);
            String type = parametersTypes.get(i);

            res += (type + " " + par);
            
            if(i < parametersList.size() - 1){
                res += " ";
            }
        }

        return res;
    }


    // FUNCTIONS USED FOR TYPE CHECKING

    // returns the type of a parameter
    public String getTypeOfField(String var){
        
        for(int i = 0; i < parametersList.size(); i++){
            if(parametersList.get(i).equals(var)){
                return parametersTypes.get(i);
            }
        }
        return null;
    }   

    // returns the type of a local variable 
    public String getTypeOfLocal(String var){
        
        for(FieldSymbol f: localFields){
            if(var.equals(f.getName()))
                return f.getType();
        }
        return null;
    }   

    // returns the type of a parameter 
    public String getTypeOfParameter(String var){
        
        for(int i = 0; i < parametersList.size(); i++){
            if(parametersList.get(i).equals(var)){
                return parametersTypes.get(i);
            }
        }
        return null;
    }   

    // returns true if the method contains a local field or a parameter with name var
    public boolean containsLocal(String var){
        
        for(int i = 0; i < parametersList.size(); i++){
            if(parametersList.get(i).equals(var)){
                return true;
            }
        }

        for(FieldSymbol f: localFields){
            if(f.getName().equals(var)){
                return true;
            }
        }
        return false;
    }

}
