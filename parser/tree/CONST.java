package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class CONST extends EXP {
  public int value;
  public CONST(int v) {value=v;}
  public List<EXP> kids() {
    List<EXP> kids = new ArrayList<EXP>();
    return kids;}//ExpList
  public EXP build(List<EXP> kids) {return this;}//ExpList kids
}
