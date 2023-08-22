package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class CJUMP extends Stm {
  public int relop;
  public EXP left, right;
  public Label iftrue, iffalse;
  public CJUMP(int rel, EXP l, EXP r, Label t, Label f) {
     relop=rel; left=l; right=r; iftrue=t; iffalse=f;
  }
  public final static int EQ=0, NE=1, LT=2, GT=3, LE=4, GE=5,
		   ULT=6, ULE=7, UGT=8, UGE=9;
  public List<EXP> kids() 
  {
    List<EXP> kids = new ArrayList<EXP>();
    kids.add(left);
    kids.add(right);
    return kids;
    }
  public Stm build(List<EXP> kids) 
  {
    return new CJUMP(relop,kids.get(0),kids.get(1),iftrue,iffalse);
  }
  public static int notRel(int relop) {
    switch (relop) {
        case EQ:  return NE;
        case NE:  return EQ;
	case LT:  return GE;
	case GE:  return LT;
	case GT:  return LE;
	case LE:  return GT;
	case ULT: return UGE;
	case UGE: return ULT;
	case UGT: return ULE;
	case ULE: return UGT;
	default: throw new Error("bad relop in CJUMP.notRel");
    }
  }
}