package assem;

public class LABEL extends Instr {
   public temp.Label label;

   public LABEL(String a, temp.Label l) {
      Assem=a; label=l;
   }

   public temp.TempList def() {return null;}
   public temp.TempList use() {return null;}
   public Targets jumps()     {return null;}

}