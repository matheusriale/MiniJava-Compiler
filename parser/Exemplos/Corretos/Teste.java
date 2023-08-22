class Main {
  public static void main (String [] a) {
    System.out.println(new Teste().metodo(10));  }
}

class Teste { 
  public int metodo (int x) { 
    while ((x < 10)) { 
      x = (x + 1);
    } 
    return x;
  }
}
