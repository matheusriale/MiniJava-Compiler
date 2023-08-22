package visitor;

import tree.*;
import translate.Ex;
import syntaxtree.*;

public interface IRVisitor {
  public Ex visit(syntaxtree.Program n);             // Pronto
  public Ex visit(syntaxtree.MainClass n);           // Pronto
  public Ex visit(syntaxtree.ClassDeclSimple n);     // Pronto
  public Ex visit(syntaxtree.ClassDeclExtends n);    // Pronto
  public Ex visit(syntaxtree.VarDecl n);             // Pronto
  public Ex visit(syntaxtree.MethodDecl n);          // Pronto
  public Ex visit(syntaxtree.Formal n);              // Pronto
  public Ex visit(syntaxtree.IntArrayType n);        // Pronto
  public Ex visit(syntaxtree.BooleanType n);         // Pronto
  public Ex visit(syntaxtree.IntegerType n);         // Pronto
  public Ex visit(syntaxtree.IdentifierType n);      // Pronto
  public Ex visit(syntaxtree.Block n);               // Pronto
  public Ex visit(syntaxtree.If n);                  // Pronto
  public Ex visit(syntaxtree.While n);               // Pronto
  public Ex visit(syntaxtree.Print n);               // Pronto
  public Ex visit(syntaxtree.Assign n);              // Pronto
  public Ex visit(syntaxtree.ArrayAssign n);         // Pronto
  public Ex visit(syntaxtree.And n);                 // Pronto
  public Ex visit(syntaxtree.LessThan n);            // Pronto
  public Ex visit(syntaxtree.Plus n);                // Pronto
  public Ex visit(syntaxtree.Minus n);               // Pronto
  public Ex visit(syntaxtree.Times n);               // Pronto
  public Ex visit(syntaxtree.ArrayLookup n);         // Pronto
  public Ex visit(syntaxtree.ArrayLength n);         // Pronto
  public Ex visit(syntaxtree.Call n);                // Pronto
  public Ex visit(syntaxtree.IntegerLiteral n);      // Pronto
  public Ex visit(syntaxtree.True n);                // Pronto
  public Ex visit(syntaxtree.False n);               // Pronto
  public Ex visit(syntaxtree.IdentifierExp n);       // Pronto
  public Ex visit(syntaxtree.This n);                // Pronto
  public Ex visit(syntaxtree.NewArray n);            // Pronto
  public Ex visit(syntaxtree.NewObject n);           // Pronto 
  public Ex visit(syntaxtree.Not n);                 // Pronto
  public Ex visit(syntaxtree.Identifier n);          // Pronto
}
