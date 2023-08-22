package table;
import java.util.*;

public class ClassTable extends Table {
	public ClassTable parent = null;
	public String name;
	public Table attrs;
	public List<Method> methods;

	public ClassTable(String name)
	{
		this.name = name;
		attrs     = new Table();
		methods   = new ArrayList<Method>();
	}

	public String getName(){return this.name;}
}
