public class VisitorArgs {
    private String className;
    private String methodName;
    private String fieldName;
    private String type; // either the type of a field or the return type of a method
    private Boolean inMethod; // in case of a field, if true it indicates it is a local
    // field of a method. This way we know if have reached VarDecleration from a 
    // ClassDecleration or a MethodDecleration

    public VisitorArgs(String classn, String method, String field, String type){
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



}
