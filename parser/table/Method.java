package table;
import java.util.*;

public class Method extends Table {
	public String name;
    public String type;
	public List<FormalArg> formals;
	public Table locals;

	public Method(String name, String type)
	{
		this.name = name;
        this.type = type;
		formals     = new ArrayList();
		locals      = new Table();
	}

	public String getName(){return this.name;}
	public String getType(){return this.type;}
}
