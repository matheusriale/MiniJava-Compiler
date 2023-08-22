package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class MEM extends EXP {
  public EXP exp;
  public MEM(EXP e) {exp=e;}
  public List<EXP> kids() 
  {
    List<EXP> kids = new ArrayList<EXP>();
    kids.add(exp);
    return kids;
  }
  public EXP build(List<EXP> kids) {
    return new MEM(kids.get(0));
  }
}

