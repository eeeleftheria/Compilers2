public class VisitorArgs {
    private String className;
    private String methodName;
    private String fieldName;
    private String type; // either the type of a field or the return type of a method

    public VisitorArgs(String classn, String method, String field, String type){
        className = classn;
        methodName = method;
        fieldName = field;
        this.type = type;
    }


    public String getClassName(){
        return className;
    }

    public String getMethodName(){
        return methodName;
    }



}
