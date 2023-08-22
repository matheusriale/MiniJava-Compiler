package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class LABEL extends Stm { 
  public Label label;
  public LABEL(Label l) {label=l;}
  public List<EXP> kids() {
    List<EXP> kids = new ArrayList<EXP>();
    return kids;}
  public Stm build(List<EXP> kids) {
    return this;
  }
}

