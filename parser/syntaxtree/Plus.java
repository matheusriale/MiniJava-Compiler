package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.IRVisitor;
import translate.Ex;

public class Plus extends Exp {
  public Exp e1,e2;
  
  public Plus(Exp ae1, Exp ae2) { 
    e1=ae1; e2=ae2;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

  public Ex accept(IRVisitor v) {
    return v.visit(this);
  }
}
