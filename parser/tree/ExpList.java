package tree;
import java.util.*;
public class ExpList {
    private List<EXP> list;
    public ExpList(List<EXP> l)
    {
	    list = l;
    }
    public List<EXP> getList()
    {
	    return list;
    }
    
    public List<EXP> kids(){return null;}
    public EXP build(List<EXP> kids){return null;}
}


