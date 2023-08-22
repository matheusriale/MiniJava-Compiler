package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.IRVisitor;
import translate.Ex;

public class BooleanType extends Type {
  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

  public Ex accept(IRVisitor v) {
    return v.visit(this);
  }

  public String toString(){return "boolean";}
}
