package tree;
import temp.Temp;
import temp.Label;
import java.util.*;

public class JUMP extends Stm {
  public EXP exp;
  public temp.LabelList targets;
  public JUMP(EXP e, temp.LabelList t) {exp=e; targets=t;}
  public JUMP(temp.Label target) {
      this(new NAME(target), new temp.LabelList(target,null));
  }
  public List<EXP> kids() 
  {
    List<EXP> kids = new ArrayList<EXP>();
    kids.add(exp);
    return kids;
  }
  public Stm build(List<EXP> kids) 
  {
    return new JUMP(kids.get(0),targets);
  }
}

