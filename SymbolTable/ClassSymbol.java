
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

}
