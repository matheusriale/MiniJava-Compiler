package tree;
import temp.Temp;
import temp.Label;
import java.util.*;

public class CALL extends EXP {
  public EXP func;
  public List<EXP> args;
  public CALL(EXP f, List<EXP> a) {func=f; args=a;}
  public List<EXP> kids() 
  {
    List<EXP> kids = new ArrayList<EXP>();
    kids.add(0, func);
    kids.addAll(args);
    return kids;
  }
  public EXP build(List<EXP> kids) {
      return new CALL(kids.remove(0),kids);
  }
  
}