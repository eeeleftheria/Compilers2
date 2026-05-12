import java.lang.reflect.Method;
import java.util.Vector;

public class MethodSymbol{
    
    private String name;
    private String returnType;
    private int numOfArgs; // number of arguments   
    private Vector<String> parametersTypes; // type of each parameter
    private Vector<String> parametersList; // names of the parameters

    public MethodSymbol(){

        parametersTypes = new Vector<>();
        parametersList = new Vector<>();
    }

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
        return numOfArgs;
    }

    public void setNumOfArgs(int n){
        numOfArgs = n;
    }

    public String getTypeOfParAtPos(int i){
        return parametersTypes.get(i);
    }

    public void setTypeOfParAtPos(String type, int pos){
        parametersTypes.add(pos, type);
    }

    public String getParAtPos(int i){
        return parametersList.get(i);
    }

    public void setParAtPos(String par, int pos){
        parametersList.add(pos, par);
    }
}
