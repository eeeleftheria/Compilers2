import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import syntaxtree.*;
import SymbolTable.*;

public class Main {

    // args[0] = first argument (not like in c)
    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            System.err.println("Usage: java Main [file1] [file2] ... [fileN]");
            System.exit(1);
        }

        for(String filename : args){
            FileInputStream fis = null;
            try{
                fis = new FileInputStream(filename);
                MiniJavaParser parser = new MiniJavaParser(fis);

                Goal root = parser.Goal();

                System.err.println("Program parsed successfully.");

                SymbolTable table = new SymbolTable();

                SymbolTableVisitor eval = new SymbolTableVisitor(table);
                root.accept(eval, null);

                TypeCheckingVisitor tc = new TypeCheckingVisitor(table);
                root.accept(tc, null);
                
            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
                System.out.println("\n\n");
            }
            finally{
                try{
                    if(fis != null) fis.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}