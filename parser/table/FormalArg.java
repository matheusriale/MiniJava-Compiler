package table;

import syntaxtree.*;

public class FormalArg {
    private String name;
    private Type type;

    public FormalArg(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName() {return this.name;}
    public Type getType() {return this.type;}

}