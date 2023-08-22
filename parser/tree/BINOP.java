package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class BINOP extends EXP {
  public int binop;
  public EXP left, right;
  public BINOP(int b, EXP l, EXP r) {
    binop=b; left=l; right=r; 
  }
  public final static int PLUS=0, MINUS=1, MUL=2, DIV=3, 
		   AND=4,OR=5,LSHIFT=6,RSHIFT=7,ARSHIFT=8,XOR=9;
  public List<EXP> kids() 
  {
    List<EXP> kids = new ArrayList<EXP>();
    kids.add(left);
    kids.add(right);
    return kids;
  }
  public EXP build(List<EXP> kids) {
    return new BINOP(binop,kids.get(0),kids.get(1));
  }
}