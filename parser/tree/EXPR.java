package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class EXPR extends Stm {
  public EXP exp; 
  public EXPR(EXP e) {exp=e;}
  public List<EXP> kids() {
    ArrayList<EXP> kids = new ArrayList<EXP>();
    kids.add(exp);
    return kids;
  }
  public Stm build(List<EXP> kids) {
    return new EXPR(kids.get(0));
  }
}
