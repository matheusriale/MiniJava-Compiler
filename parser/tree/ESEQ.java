package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class ESEQ extends EXP {
  public Stm stm;
  public EXP exp;
  public ESEQ(Stm s, EXP e) {stm=s; exp=e;}
  public List<EXP> kids() {throw new Error("kids() not applicable to ESEQ");}
  public EXP build(List<EXP> kids) {throw new Error("build() not applicable to ESEQ");}
}
