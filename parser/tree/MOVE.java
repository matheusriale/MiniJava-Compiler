package tree;
import temp.Temp;
import temp.Label;
import java.util.*;
public class MOVE extends Stm 
{
  public EXP dst, src;
  public MOVE(EXP d, EXP s) {dst=d; src=s;}
  public List<EXP> kids() 
  {
    List<EXP> kids = new ArrayList<EXP>();
    if (dst instanceof MEM) 
    {
	    kids.add(((MEM)dst).exp);
	    kids.add(src);
	  }
    else
    {
	    kids.add(src);
    }
	return kids;
  }
  public Stm build(List<EXP> kids) 
  {
    if (dst instanceof MEM)
    {
      return new MOVE(new MEM(kids.get(0)), kids.get(1));
    }
    else
    {
      return new MOVE(dst, kids.get(0));
    }   
  }
}

