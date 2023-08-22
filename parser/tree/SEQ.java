package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class SEQ extends Stm {
  public Stm left, right;
  public SEQ(Stm l, Stm r) { left=l; right=r; }
  public List<EXP> kids() {throw new Error("kids() not applicable to SEQ");}
  public Stm build(List<EXP> kids) {throw new Error("build() not applicable to SEQ");}
}

