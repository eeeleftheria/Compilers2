import syntaxtree.*;
import visitor.*;
import SymbolTable.*;


class MyVisitor extends GJDepthFirst<String, Void>{
    
    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    // use default visitor for now
    @Override 
    public String visit(Goal n, Void argu) throws Exception{
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
    public String visit(MainClass n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        // super.visit(n, argu);

        System.out.println();

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
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        
        String classname = n.f1.accept(this, argu);
        System.out.println("Class: " + classname);

        n.f2.accept(this, argu);
        System.out.println("Fields: ");
        n.f3.accept(this, argu);
        System.out.println("Methods: ");
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);

        System.out.println();

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
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        n.f0.accept(this, argu);

        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        System.out.println("Fields: ");
        n.f5.accept(this, argu);
        System.out.println("Methods: ");
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);

        System.out.println();

        return null;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, Void argu) throws Exception {
        String _ret=null;
        String type = n.f0.accept(this, argu);
        String var = n.f1.accept(this, argu);
        System.out.println(var + " " + type);
        super.visit(n, argu);
        
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
    public String visit(MethodDeclaration n, Void argu) throws Exception {

        // if the method has non-void parameters: accept and visit them 
        String argumentList = n.f4.present() ? n.f4.accept(this, null) : "";

        String myType = n.f1.accept(this, null);
        String myName = n.f2.accept(this, null);

        System.out.println("Method: " + myType + " " + myName + " (" + argumentList + ")");
        System.out.println("Local vars:");

        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
        
        // this visits only the first parameter
        String ret = n.f0.accept(this, null);

        // if there is more than one parameter: visit the Tail
        // and add the result to the final string
        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        return type + " " + name;
    }
    
    /**
    * f0 -> ( FormalParameterTerm() )*
    */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        String ret = "";

        // each node is of type FormalParameterTerm
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, null);
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
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }


    @Override
    public String visit(ArrayType n, Void argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, Void argu) {
        return "int";
    }

    /**
     * f0 -> <IDENTIFIER>
    */
   @Override
   public String visit(Identifier n, Void argu) {
       return n.f0.toString();
    }


    ///////////////////////////////////////////////////////////////////////////

    /**
     * f0 -> "{" 
     * f1 -> ( Statement() )* 
     * f2 -> "}" 
     * */
    @Override
    public String visit(Block n, Void argu){
        return "";
    }

    /**
     * f0 -> Identifier() 
     * f1 -> "=" 
     * f2 -> Expression() 
     * f3 -> ";"
     */
    @Override
    public String visit(AssignmentStatement n , Void argu){
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
    public String visit(ArrayAssignmentStatement n, Void argu){
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
    public String visit(IfStatement n, Void argu){
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
    public String visit(WhileStatement n, Void argu){
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
    public String visit(PrintStatement n, Void argu){
        return "";
    }


    /**
     * f0 -> Clause() 
     * f1 -> "&&" 
     * f2 -> Clause()
     */
    @Override
    public String visit(AndExpression n, Void argu){
        return "";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "<" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n, Void argu){
        return "";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "+" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n, Void argu){
        return "";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "-" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n, Void argu){
        return "";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "*" 
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n, Void argu){
        return "";
    }

    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "[" 
     * f2 -> PrimaryExpression() 
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n, Void argu){
        return "";
    }
    
    
    /**
     * f0 -> PrimaryExpression() 
     * f1 -> "." 
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n, Void argu){
        return "";
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
    public String visit(MessageSend n, Void argu){
        return "";
    }

    /**
     * f0 -> Expression() 
     * f1 -> ExpressionTail()
     */
    @Override
    public String visit(ExpressionList n, Void argu){
        return "";
    }

    /**
     * f0 -> ( ExpressionTerm() )
     */
    @Override
    public String visit(ExpressionTail n, Void argu){
        return "";
    }

    /**
     * f0 -> "," 
     * f1 -> Expression()
     */
    @Override
    public String visit(ExpressionTerm n, Void argu){
        return "";
    }

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    @Override
    public String visit(IntegerLiteral n, Void argu){
        return "";
    }

    /**
     * f0 -> "true"
    */
    @Override
    public String visit(TrueLiteral n, Void argu){
        return "";
    }

    /**
     * f0 -> "false"
    */
    @Override
    public String visit(FalseLiteral n, Void argu){
        return "";
    }

    /**
     * f0 -> "this"
    */
    @Override
    public String visit(ThisExpression n, Void argu){
        return "";
    }

    /**
     * f0 -> "new" 
     * f1 -> "int" 
     * f2 -> "[" 
     * f3 -> Expression() 
     * f4 -> "]"
    */
    @Override
    public String visit(ArrayAllocationExpression n, Void argu){
        return "";
    }

    /**
     * f0 -> "new" 
     * f1 -> Identifier() 
     * f2 -> "(" 
     * f3 -> ")"
    */
    @Override
    public String visit(AllocationExpression n, Void argu){
        return "";
    }


    /**
     * f0 -> "!" 
     * f1 -> Clause()
    */
    @Override
    public String visit(NotExpression n, Void argu){
        return "";
    }

    /**
     * f0 -> "(" 
     * f1 -> Expression() 
     * f2 -> ")"
    */
    @Override
    public String visit(BracketExpression n, Void argu){
        return "";
    }

}