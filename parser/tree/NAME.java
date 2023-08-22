package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class NAME extends EXP {
  public Label label;
  public NAME(Label l) {label=l;}
  public List<EXP> kids() {return new ArrayList<EXP>();}
  public EXP build(List<EXP> kids) {return this;}
}

