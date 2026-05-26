
package SymbolTable;

import java.lang.reflect.Field;

public class FieldSymbol{
    
    private String type;
    private String name;
    private int offset;

    public FieldSymbol(String n, String t, int o){
        type = t;
        name = n;
        offset = o;
    }

    public String getType(){
        return type;
    }


    public String getName(){
        return name;
    }

    public int getOffset(){
        return offset;
    }

}
