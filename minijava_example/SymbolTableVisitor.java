import syntaxtree.*;
import visitor.*;
import SymbolTable.*;


class SymbolTableVisitor extends GJDepthFirst<String, VisitorArgs>{

    SymbolTable symboltable;

    // constructor so the symbol table can be passed properly
    public SymbolTableVisitor(SymbolTable table){
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
    
        symboltable.printSymbolTable();

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
        String classname = n.f1.accept(this, null);
        symboltable.addClass(classname);

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
    public String visit(ClassDeclaration n, VisitorArgs argu) throws Exception {
        
        n.f0.accept(this, argu);
        
        String classname = n.f1.accept(this, argu);
        symboltable.addClass(classname);

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
        symboltable.addClass(classname);
        
        
        VisitorArgs args = new VisitorArgs(classname, "", "", "", "");
        
        n.f2.accept(this, args);
       
        // store parent class in symbol table
        String parent = n.f3.accept(this, args); 
        symboltable.addParentClass(classname, parent);

        n.f4.accept(this, args);
        n.f5.accept(this, args);
        n.f6.accept(this, args);
        n.f7.accept(this, args);

        System.out.println();

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
        String var = n.f1.accept(this, args);
  
        // super.visit(n, argu);
        
        // in this case the field comes from a class decl
        if(args.inMethod() == false){
            symboltable.addClassField(className, var, type);
        }
        // in this case the field comes from a method decl
        else{
            symboltable.addMethodLocal(className, args.getMethodName(), var, type, args.getParameters());
        }

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

        symboltable.addClassMethod(argu.getClassName(), myName, myType);
        
        VisitorArgs args = new VisitorArgs(argu.getClassName(), myName, "-", myType, "");
        args.setInMethod(); // set flag to true, since we are inside of a method decl

        // if the method has non-void parameters: accept and visit them 
        String argumentList = n.f4.present() ? n.f4.accept(this, args) : "";

        args.setParameters(argumentList);

        // visit the local fields
        n.f7.accept(this, args);

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

            symboltable.addAllParameters(args.getClassName(), args.getMethodName(), ret);

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
       return n.f0.toString();
    }
}
