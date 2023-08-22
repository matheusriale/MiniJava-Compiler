package tree;
import java.util.*;
public class TEMP extends EXP {
  public temp.Temp temp;
  public TEMP(temp.Temp t) {temp=t;}
  public List<EXP> kids() 
  {
    return new ArrayList<EXP>();
  }
  public EXP build(List<EXP> kids) {return this;}
}

