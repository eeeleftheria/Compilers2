public class VisitorArgs {
    private String className;
    private String methodName;
    private String fieldName;
    private String type; // either the type of a field or the return type of a method
    private Boolean inMethod; // in case of a field, if true it indicates it is a local
    // field of a method. This way we know if have reached VarDecleration from a 
    // ClassDecleration or a MethodDecleration

    private String parameters; // a string with the parameters of a method in format: type var type var ...

    public VisitorArgs(String classn, String method, String field, String type, String pars){
        this.className = classn;
        this.methodName = method;
        this.fieldName = field;
        this.type = type;
        this.inMethod = false;
    }


    public String getClassName(){
        return className;
    }

    public String getMethodName(){
        return methodName;
    }

    // returns true if we are inside a method decl, else false
    public Boolean inMethod(){
        return this.inMethod;
    }

    // the flag is set to true in case of a local field of a method
    public void setInMethod(){
        inMethod = true;        
    }

    // resets flag back to false
    public void resetInMethod(){
        inMethod = false;
    }

    // returns all paraneters of a method as one string
    public String getParameters(){
        return parameters;
    }

    // sets the parameters of a method as a string of format: type var type var
    public void setParameters(String pars){
        parameters = pars;
    }



}
