import syntaxtree.*;
import visitor.*;
import SymbolTable.*;


class TypeCheckingVisitor extends GJDepthFirst<String, VisitorArgs>{

    SymbolTable symboltable;

    // constructor so the symbol table can be passed properly
    public TypeCheckingVisitor(SymbolTable table){
        symboltable = table;
    }
    
    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    // use default visitor for now
    @Override 
    public String visit(Goal n, VisitorArgs argu) throws Exception{
        n.f0.accept(this, null); // Goal.MainClass.accept(MyVisitor) -> MyVisitor.visit(MainClass)
        n.f1.accept(this, null);   
    

        return "";
    }
   
    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, VisitorArgs argu) throws Exception {

        String classname = n.f1.accept(this, argu);
        
        VisitorArgs args = new VisitorArgs(classname, "", "", "", "");
        
        // visit the rest of the main class with the correct class name
        n.f14.accept(this, args); // VarDeclarations
        n.f15.accept(this, args); // Statements

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, VisitorArgs argu) throws Exception {
        
        n.f0.accept(this, argu);
        
        String classname = n.f1.accept(this, argu);

        VisitorArgs args = new VisitorArgs(classname, "", "", "", "");

        n.f2.accept(this, args);
        n.f3.accept(this, args);
        n.f4.accept(this, args);
        n.f5.accept(this, args);

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, VisitorArgs argu) throws Exception {
        n.f0.accept(this, argu);

        String classname = n.f1.accept(this, null);
        
        VisitorArgs args = new VisitorArgs(classname, "", "", "", "");
        
        n.f2.accept(this, args);
       
        // store parent class in symbol table
        String parent = n.f3.accept(this, args); 

        n.f4.accept(this, args);
        n.f5.accept(this, args);
        n.f6.accept(this, args);
        n.f7.accept(this, args);

        return null;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, VisitorArgs args) throws Exception {
        String _ret=null;

        String className = args.getClassName();
        String type = n.f0.accept(this, args);
        String var = n.f1.accept(this, null);  // we only need the name of the var

        return _ret;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, VisitorArgs argu) throws Exception {
        
        String myType = n.f1.accept(this, argu);
        String myName = n.f2.accept(this, argu);

        VisitorArgs newArgs = new VisitorArgs(argu.getClassName(), myName, "-", myType, "");

        // if the method has non-void parameters: accept and visit them 
        String argumentList = n.f4.present() ? n.f4.accept(this, newArgs) : "";
        
        newArgs.setParameters(argumentList);

        // visit the local fields
        n.f7.accept(this, newArgs);

        // visit the statements
        n.f8.accept(this, newArgs);

        // visit the return value
        String retValueType = n.f10.accept(this, newArgs);
        String retType = symboltable.getReturnTypeOfMethod(argu.getClassName(), myName, newArgs.getParameters());
       System.out.println(retType + " " + retValueType);
        if(!retValueType.equals(retType)){
            throw new Exception("Method Declaration error: return value type " + retValueType + " does not match expected return type " + 
              retType + " in method " + myName);
        }


        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, VisitorArgs args) throws Exception {
        
        // this visits only the first parameter
        String ret = n.f0.accept(this, args); // ret consists of type + name of first parameter

        // if there is more than one parameter: visit the Tail
        // and add the result to the final string
        if (n.f1 != null) {
            String tempRes = n.f1.accept(this, args); // tempRes stores remaining parameters: "type name type name ..."
            ret += tempRes;
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, VisitorArgs argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        return type + " " + name;
    }
    
    /**
    * f0 -> ( FormalParameterTerm() )*
    */
    @Override
    public String visit(FormalParameterTail n, VisitorArgs argu) throws Exception {
        String ret = "";

        // each node is of type FormalParameterTerm
        for ( Node node: n.f0.nodes) {
            ret += " " + node.accept(this, null);
        }
 
        return ret;
    }

    /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */

    // this visit method is called for every parameter 
    // of a method after the first one
    @Override
    public String visit(FormalParameterTerm n, VisitorArgs argu) throws Exception {
        return n.f1.accept(this, argu);
    }


    @Override
    public String visit(ArrayType n, VisitorArgs argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, VisitorArgs argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, VisitorArgs argu) {
        return "int";
    }

    /**
     * f0 -> <IDENTIFIER>
    */
   @Override
   public String visit(Identifier n, VisitorArgs argu) {
       String name = n.f0.toString();
       
        // in case of goal rule, argu is still null so we should
        // return the name of the class
       if(argu == null || argu.getClassName().isEmpty()){
           return name;
       }

       // in any other case, the identifier comes from a vardecl, 
       // a class decl or a methoddecl
       String classn = argu.getClassName();
       
       // in case of a class field
       String type = symboltable.getTypeOfField(classn, name);

      // in case of a method local 
       if(type == null && argu.getMethodName() != null && !argu.getMethodName().isEmpty()){
            type = symboltable.getTypeOfLocal(classn, name, argu.getMethodName(), argu.getParameters());
       }
       
       // in case of a method parameter 
       if(type == null && argu.getMethodName() != null && !argu.getMethodName().isEmpty() ){
            type = symboltable.getTypeOfParameter(classn, name, argu.getMethodName(), argu.getParameters());
       }

       if(type == null)
            return name;
       
       return type;
       
    }


    ///////////////////////////////////////////////////////////////////////////

    /**
     * f0 -> "{" 
     * f1 -> ( Statement() )* 
     * f2 -> "}" 
     * */
    @Override
    public String visit(Block n, VisitorArgs argu){
        return "";
    }

    /**
     * f0 -> Identifier() 
     * f1 -> "=" 
     * f2 -> Expression() 
     * f3 -> ";"
     */
    @Override
    public String visit(AssignmentStatement n , VisitorArgs argu) throws Exception{
        
        String ltype = n.f0.accept(this, argu); // get variable type
        String rtype = n.f2.accept(this, argu); // get type of expression

        // check if rtype is subtype of ltype
        if (ltype != null && rtype != null && !symboltable.isSubtype(ltype, rtype)) {
            throw new Exception("Type error: cannot assign " + rtype + " to " + ltype);
        }

        return "";
    }

    /**
     * f0 -> Identifier() 
     * f1 -> "[" 
     * f2 -> Expression() 
     * f3 -> "]" 
     * f4 -> "=" 
     * f5 -> Expression() 
     * f6 -> ";"
     */
    @Override
    public String visit(ArrayAssignmentStatement n, VisitorArgs argu) throws Exception{
        String type = n.f0.accept(this, argu);
        if(!type.equals("int[]")){
            throw new Exception("Array Assignment error: variable is not of type int[]");
        }

        String typeExp = n.f2.accept(this, argu);
        if(!typeExp.equals("int")){
            throw new Exception("Array Assignment error: index for assignment must be an int");
        }

        String typeRes = n.f5.accept(this, argu);
        if(!typeRes.equals("int")){
            throw new Exception("Array Assignment error: value of assignment must be an int");
        }

        return "";
    }

    /**
     * f0 -> "if" 
     * f1 -> "(" 
     * f2 -> Expression() 
     * f3 -> ")" 
     * f4 -> Statement() 
     * f5 -> "else" 
     * f6 -> Statement()
     */
    @Override
    public String visit(IfStatement n, VisitorArgs argu) throws Exception{
        return "";
    }

    /**
     * f0 -> "while" 
     * f1 -> "(" 
     * f2 -> Expression() 
     * f3 -> ")" 
     * f4 -> Statement()
     */
    @Override
    public String visit(WhileStatement n, VisitorArgs argu) throws Exception{
        return "";
    }    

    /**
     * f0 -> "System.out.println" 
     * f1 -> "(" 
     * f2 -> Expression() 
     * f3 -> ")" 
     * f4 -> ";"
     */
    @Override
    public String visit(PrintStatement n, VisitorArgs argu) throws Exception{
        String type = n.f2.accept(this, argu); 
        if(!type.equals("int")){
            throw new Exception("Print Statement error: value to print is not an int");
        }
        return "";
    }


    /**
     * f0 -> Clause() 
     * f1 -> "&&" 
     * f2 -> Clause()
     */
    @Override
    public String visit(AndExpression n, VisitorArgs argu) throws Exception{
        return "boolean";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "<" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n, VisitorArgs argu) throws Exception{
        return "boolean";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "+" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n, VisitorArgs argu) throws Exception{
        return "int";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "-" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n, VisitorArgs argu) throws Exception{
        return "int";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "*" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n, VisitorArgs argu) throws Exception{
        return "int";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "[" 
     * f2 -> PrimaryExpression() 
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n, VisitorArgs argu) throws Exception{
        String type1 = n.f0.accept(this, argu);
        if(!type1.equals("int[]")){
            throw new Exception("Array Lookup error: cannot use array indexing on int type, must be int[]");
        }
        String type2 = n.f2.accept(this, argu);
        if(!type2.equals("int")){
            throw new Exception("Array Lookup error: array index is not an int");
        }
        
        return "int";
    }
    
    
    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "." 
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n, VisitorArgs argu) throws Exception{
        return "int";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "." 
     * f2 -> Identifier() 
     * f3 -> "(" 
     * f4 -> ( ExpressionList() )? 
     * f5 -> ")"
     */
    @Override
    public String visit(MessageSend n, VisitorArgs argu) throws Exception{
        String objectType = n.f0.accept(this, argu);  // type of object

        VisitorArgs args = new VisitorArgs(argu.getClassName(), null, null, null, null);
        String methodName = n.f2.accept(this, args);  // method name
    
        
        // Check if it's a valid class type
        if((!symboltable.isTypeClass(objectType)) && (!objectType.equals("this"))){

            throw new Exception("MessageSend error: cannot call method on " + objectType);
        }

        if(objectType.equals("this")){
            objectType = argu.getClassName();
        }

        // check if object class contains the specific method

        // first we need to construct the string with the arguments of the function call
        // ExpressionList returns string of format "type1 type2 type3 ..."
        String typesStr = n.f4.accept(this, argu);

        if(!symboltable.containsMethodWithTypes(objectType, methodName, typesStr)){
            throw new Exception("MessageSend error: method " + methodName + " does not exist in class " + objectType);
        }
        
        // the type of the messageSend result is the return type of the method
        String retType = symboltable.getReturnTypeOfMethod(objectType, methodName, typesStr);
        
        return retType;
    }

    /**
     * f0 -> Expression() 
     * f1 -> ExpressionTail()
     */
    @Override
    // the only place we can have expression lists are in the message sends
    // so we can return the string type(expr1) type(expr2) type(expr3) ...
    public String visit(ExpressionList n, VisitorArgs argu) throws Exception{
        String type1 = n.f0.accept(this, argu);
        String types = n.f1.accept(this, argu);

        types = type1 + " " + types;
        return types;
    }

    /**
     * f0 -> ( ExpressionTerm() )
     */
    @Override
    public String visit(ExpressionTail n, VisitorArgs argu) throws Exception{
        String types = "";

        // each node is of type ExpressionTerm
        for ( Node node: n.f0.nodes) {
            types += " " + node.accept(this, null);
        }
 
        return types;
    }

    /**
     * f0 -> "," 
     * f1 -> Expression()
     */
    @Override
    public String visit(ExpressionTerm n, VisitorArgs argu) throws Exception{
        
        // expression() must return type
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    @Override
    public String visit(IntegerLiteral n, VisitorArgs argu) throws Exception{
        return "int";
    }

    /**
     * f0 -> "true"
    */
    @Override
    public String visit(TrueLiteral n, VisitorArgs argu) throws Exception{
        return "boolean";
    }

    /**
     * f0 -> "false"
    */
    @Override
    public String visit(FalseLiteral n, VisitorArgs argu) throws Exception{
        return "boolean";
    }

    /**
     * f0 -> "this"
    */
    @Override
    public String visit(ThisExpression n, VisitorArgs argu) throws Exception{
        return "this";
    }

    /**
     * f0 -> "new" 
     * f1 -> "int" 
     * f2 -> "[" 
     * f3 -> Expression() 
     * f4 -> "]"
    */
    @Override
    public String visit(ArrayAllocationExpression n, VisitorArgs argu) throws Exception{
        String type = n.f3.accept(this, argu);
        if(!type.equals("int")){
            throw new Exception("Array Allocation error: type of allocation size is not an int");
        }


        return "int[]";
    }

    /**
     * f0 -> "new" 
     * f1 -> Identifier() 
     * f2 -> "(" 
     * f3 -> ")"
    */
    @Override
    public String visit(AllocationExpression n, VisitorArgs argu) throws Exception{
        String classn = n.f1.accept(this, argu);
        boolean res = symboltable.containsClass(classn);
        if(res == true){
            return classn;
        }
        else
            throw new Exception("Allocation expression error: " + classn + " is not a class");
    }


    /**
     * f0 -> "!" 
     * f1 -> Clause()
    */
    @Override
    public String visit(NotExpression n, VisitorArgs argu) throws Exception{
        String type = n.f1.accept(this, argu);  
        return "boolean";
    }

    /**
     * f0 -> "(" 
     * f1 -> Expression() 
     * f2 -> ")"
    */
    @Override
    public String visit(BracketExpression n, VisitorArgs argu) throws Exception{
        return n.f1.accept(this, argu);
    }

}