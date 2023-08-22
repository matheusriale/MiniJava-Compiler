package temp;

public class Temp  {
   private static int count;
   public int num;
   public String toString() {return "t" + num;}
   public Temp() { 
     num=count++;
   }

   public boolean equals(Temp t)
   {
    return num == t.num;
   }
}