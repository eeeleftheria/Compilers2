package SymbolTable;

import java.lang.reflect.Method;
import java.util.Vector;

public class MethodSymbol{
    
    private String name;
    private String returnType; 
    private Vector<String> parametersTypes; // type of each parameter
    private Vector<String> parametersList; // names of the parameters
    private Vector<FieldSymbol> localFields; // local fields of method

    public MethodSymbol(){

        parametersTypes = new Vector<>();
        parametersList = new Vector<>();
        localFields = new Vector<>();
    }

    //#### GETTERS AND SETTERS ####//

    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }

    public String getReturnType(){
        return returnType;
    }

    public void setReturnType(String n){
        returnType = n;
    }

    public int getNumOfArgs(){
        return parametersList.size();
    }
    
    public Vector<FieldSymbol> getLocals(){

        // !! return copy of vector (objects' value is copied to a reference in java)
        return new Vector<FieldSymbol>(localFields);
    }


    ////////////////////////////////////


    // adds a new parameters with name par and type 
    public void addParam(String par, String type){
        parametersList.add(par);
        parametersTypes.add(type);
    }

    public void addLocalField(String name, String type){
        FieldSymbol fs = new FieldSymbol();
        fs.setName(name);
        fs.setType(type);

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


}
