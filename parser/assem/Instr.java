package assem;

public abstract class Instr {
  public String Assem;
  public abstract temp.TempList def();
  public abstract temp.TempList use();
  public abstract Targets jumps();

  private temp.Temp nthTemp(temp.TempList l, int i) {
    if (i==0) return l.head;
    else return nthTemp(l.tail,i-1);
  }

  private temp.Label nthLabel(temp.LabelList l, int i) {
    if (i==0) return l.head;
    else return nthLabel(l.tail,i-1);
  }

  public String format(temp.TempMap m) {
    temp.TempList dst = def();
    temp.TempList src = use();
    Targets j = jumps();
    temp.LabelList jump = (j==null)?null:j.labels;
    StringBuffer s = new StringBuffer();
    int len = Assem.length();
    for(int i=0; i<len; i++)
	if (Assem.charAt(i)=='`')
	   switch(Assem.charAt(++i)) {
              case 's': {int n = Character.digit(Assem.charAt(++i),10);
			 s.append(m.tempMap(nthTemp(src,n)));
			}
			break;
	      case 'd': {int n = Character.digit(Assem.charAt(++i),10);
			 s.append(m.tempMap(nthTemp(dst,n)));
			}
 			break;
	      case 'j': {int n = Character.digit(Assem.charAt(++i),10);
			 s.append(nthLabel(jump,n).toString());
			}
 			break;
	      case '`': s.append('`'); 
			break;
              default: throw new Error("bad Assem format");
       }
       else s.append(Assem.charAt(i));

    return s.toString();
  }

}