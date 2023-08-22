package visitor;

import java.util.*;
import syntaxtree.*;
import table.*;

public class TypeCheckVisitor implements TypeVisitor {
  
  SymbolTableVisitor stv;
  ClassTable mainTable;
  List<ClassTable> classTableList;
  
  ClassTable classe_atual = null;
  ClassTable classe_call_global  = null;
  Method     metodo_atual = null;


  public TypeCheckVisitor(SymbolTableVisitor stv)
  {
    this.stv       = stv;
    mainTable      = stv.mainTable;
    classTableList = stv.classTableList;
  }

  // MainClass m;
  // ClassDeclList cl;
  public Type visit(Program n) {
    this.classe_atual = this.mainTable;
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        this.classe_atual = this.classTableList.get(i);
        n.cl.elementAt(i).accept(this);
    }
    return null;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public Type visit(MainClass n) {
    n.i1.accept(this);
    //n.i2.accept(this);
    n.s.accept(this);

    return null;
  }
  
  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclSimple n) {
    n.i.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        this.metodo_atual = this.classe_atual.methods.get(i);
        n.ml.elementAt(i).accept(this);
    }
    return null;
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclExtends n) {
    n.i.accept(this);
    n.j.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        this.metodo_atual = this.classe_atual.methods.get(i);
        n.ml.elementAt(i).accept(this);
    }
    return null;
  }

  // Type t;
  // Identifier i;
  public Type visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public Type visit(MethodDecl n) {
    Type mt = n.t.accept(this);

    n.i.accept(this);
    for ( int i = 0; i < n.fl.size(); i++ ) {
        n.fl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    Type et = n.e.accept(this);

    if(!(et.toString().equals(mt.toString())))
    {
      System.out.println("Tipo de retorno diferente do tipo do método");
      return null;
    }
    
    if(mt.toString().equals("int"))
    {
      return new IntegerType();
    }
    else if(mt.toString().equals("int[]"))
    {
      return new IntArrayType();
    }
    else if(mt.toString().equals("boolean"))
    {
      return new BooleanType();
    }
    else 
    {
      return new IdentifierType(mt.toString());
    }
  }

  // Type t;
  // Identifier i;
  public Type visit(Formal n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  public Type visit(IntArrayType n) {
    return new IntArrayType();
  }

  public Type visit(BooleanType n) {
    return new BooleanType();
  }

  public Type visit(IntegerType n) {
    return new IntegerType();
  }

  // String s;
  public Type visit(IdentifierType n) {
    boolean declared = false;
    // Checa se o tipo foi declarado
    for( Iterator<ClassTable> it = this.classTableList.iterator(); it.hasNext(); )
    {
      ClassTable cl = it.next();
      if(cl.getName().equals(n.s))
      {
        declared = true;
      }
    }
    if(declared)
    {
      return new IdentifierType(n.toString());
    }
    return null;

  }

  // StatementList sl;
  public Type visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    return null;
  }

  // Exp e;
  // Statement s1,s2;
  public Type visit(If n) {
    Type et = n.e.accept(this);

    if(!(et instanceof BooleanType))
    {
      System.out.println("Expressão precisa ser do tipo Boolean");
      return null;
    }
    n.s1.accept(this);
    n.s2.accept(this);
    return null;
  }

  // Exp e;
  // Statement s;
  public Type visit(While n) {
    Type et = n.e.accept(this);

    if(!(et instanceof BooleanType))
    {
      System.out.println("Expressão precisa ser do tipo Boolean");
      return null;
    }

    n.s.accept(this);
    return null;
  }

  // Exp e;
  public Type visit(Print n) {
    Type et = n.e.accept(this);
    if (!(et instanceof IntegerType))
    {
      System.out.println("Só imprime inteiro");
      return null;
    }
    return null;
  }
  
  // Identifier i;
  // Exp e;
  public Type visit(Assign n) {
      Type itype = n.i.accept(this);
      Type et = n.e.accept(this);
      List<String> possible_types = new ArrayList();
      if(et instanceof IdentifierType)
      {
        ClassTable type_table = null;
        for(Iterator<ClassTable> it = classTableList.iterator(); it.hasNext();)
        {
          ClassTable temp = it.next();
          if (temp.getName().equals(et.toString()))
          {
            type_table = temp;
          }
        }
        
        possible_types.add(type_table.getName());
        boolean has_parent = false;
        if(type_table.parent != null)
        {
          has_parent = true;
          type_table = type_table.parent;
        }

        while(has_parent)
        {
          possible_types.add(type_table.getName());
          if(type_table.parent != null )
          {
            type_table = type_table.parent;
          }
          else
          {
            has_parent=false;
          }
        }

        boolean match = false;
        for(Iterator<String> it = possible_types.iterator(); it.hasNext();)
        {
          String t = it.next();
          if(t.equals(itype.toString()))
          {
            match = true;
            break;
          }
        }
        if (!match)
        {
          System.out.println("Tipo esperado diferente do tipo passado1");
          return null;
        }

      }
      else
      {
        if(!(itype.toString().equals(et.toString())))
        {
          System.out.println("Tipo esperado diferente do tipo passado2");
          return null;
        }
      }
      return null;
  } 

  // Identifier i;
  // Exp e1,e2;
  public Type visit(ArrayAssign n) {

    Type it  = n.i.accept(this);
    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!(it instanceof IntArrayType))
    {
      System.out.println("Variável não é um vetor");
      return null;
    }

    if(!(et1 instanceof IntegerType))
    {
      System.out.println("Index precisar ser um inteiro");
      return null;
    }
    if(!(et2 instanceof IntegerType))
    {
      System.out.println("Valor atribuído precisa ser um inteiro");
      return null;
    }
    return null;
  }

  // Exp e1,e2;
  public Type visit(And n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof BooleanType) && (et2 instanceof BooleanType)))
    {
      System.out.println("Ambos operandos precisam ter tipo boolean");
      return null;
    }

    return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(LessThan n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof IntegerType) && (et2 instanceof IntegerType)))
    {
      System.out.println("Ambos operandos precisam ter tipo int");
      return null;
    }

    return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(Plus n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof IntegerType) && (et2 instanceof IntegerType)))
    {
      System.out.println("Ambos operandos precisam ter tipo int");
      return null;
    }

    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Minus n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof IntegerType) && (et2 instanceof IntegerType)))
    {
      System.out.println("Ambos operandos precisam ter tipo int");
      return null;
    }

    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Times n) {

    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!((et1 instanceof IntegerType) && (et2 instanceof IntegerType)))
    {
      System.out.println("Ambos operandos precisam ter tipo int");
      return null;
    }

    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(ArrayLookup n) {
    Type et1 = n.e1.accept(this);
    Type et2 = n.e2.accept(this);

    if(!(et1 instanceof IntArrayType))
    {
      System.out.println("Variável não é um vetor");
      return null;
    }

    if(!(et2 instanceof IntegerType))
    {
      System.out.println("O index precisa ser um inteiro");
      return null;
    }

    return new IntegerType();
  }

  // Exp e;
  public Type visit(ArrayLength n) {
    Type et = n.e.accept(this);

    if(!(et instanceof IntArrayType))
    {
      System.out.println("Variável não é um vetor");
      return null;
    }

    return new IntegerType();
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public Type visit(Call n) {
    Type et = n.e.accept(this);
    ClassTable class_call = null;
    Method method_call = null;

    if (!(et instanceof IdentifierType)) // Checa se a expressão avalia para uma classe definida no código
    {
      System.out.println("Erro de tipo");
      return null;
    }

    // Busca a tabela da classe cujo método é chamado
    for(Iterator<ClassTable> it =  classTableList.iterator(); it.hasNext();)
    {
      ClassTable class_it = it.next();
      if(et.toString().equals(class_it.name))
      {
        class_call = class_it;
        this.classe_call_global = class_it;
        break;
      }
    }

    // Caso não ache a tabela da classe, erro
    if(class_call == null)
    {
      System.out.println("Classe não declarada");
      return null;
    }
    
    n.i.accept(this); // Accept do identificador do método chamado
    this.classe_call_global = null; // É necessário que essa variável seja null ao fim desse método

    // Busca a tabela de simbolos do método sendo chamado na classe sendo chamada
    for(Iterator<Method> it = class_call.methods.iterator(); it.hasNext();)
    {
      Method method_it = it.next();
      if(n.i.toString().equals(method_it.name))
      {
        method_call = method_it;
        break;
      }
    }
    
    // Busca a tabela de simbolos do método sendo chamado nos ancestrais da classe atual
    if(method_call == null)
    {
      boolean has_parent = false;
      ClassTable parent = null;
      if(class_call.parent != null)
      {
        has_parent = true;
        parent = class_call.parent;
      }

      while(has_parent && method_call==null)
      {
        for(Iterator<Method> it = parent.methods.iterator(); it.hasNext();)
        {
          Method method_it = it.next();
          if(n.i.toString().equals(method_it.name))
          {
            method_call = method_it;
            break;
          }
        }
        if(parent.parent != null)
        {
          parent = parent.parent;
        }
        else
        {
          has_parent = false;
        }
      }
    }


    // Caso o método não exista, erro
    if(method_call == null)
    {
      System.out.println("Método não declarado");
      return null;
    }

    // Compara a lista de argumentos formais do método
    // com a lista do argumentos passados
    for ( int i = 0; i < n.el.size(); i++ ) {
        Type pt = n.el.elementAt(i).accept(this);
        List<String> possible_types = new ArrayList();
        if(pt instanceof IdentifierType)
        {
          ClassTable type_table = null;
          for(Iterator<ClassTable> it = classTableList.iterator(); it.hasNext();)
          {
            ClassTable temp = it.next();
            if (temp.getName().equals(pt.toString()))
            {
              type_table = temp;
            }
          }
          
          possible_types.add(type_table.getName());
          boolean has_parent = false;
          if(type_table.parent != null)
          {
            has_parent = true;
            type_table = type_table.parent;
          }

          while(has_parent)
          {
            possible_types.add(type_table.getName());
            if(type_table.parent != null )
            {
              type_table = type_table.parent;
            }
            else
            {
              has_parent=false;
            }
          }

          boolean match = false;
          for(Iterator<String> it = possible_types.iterator(); it.hasNext();)
          {
            String t = it.next();
            if(t.equals(method_call.formals.get(i).getType().toString()))
            {
              match = true;
              break;
            }
          }
          if (!match)
          {
            System.out.println("Tipo esperado diferente do tipo passado3");
            return null;
          }

        }
        else
        {
          if(!(method_call.formals.get(i).getType().toString().equals(pt.toString())))
          {
            System.out.println("Tipo esperado diferente do tipo passado4");
            return null;
          }
        }
    }

    // Retorna o tipo compatível com o método invocado
    if(method_call.type == "int")
    {
      return new IntegerType();
    }
    else if(method_call.type == "int[]")
    {
      return new IntArrayType();
    }
    else if(method_call.type == "boolean")
    {
      return new BooleanType();
    }
    else
    {
      return new IdentifierType(method_call.type);
    }
  }

  // int i;
  public Type visit(IntegerLiteral n) {
    return new IntegerType();
  }

  public Type visit(True n) {
    return new BooleanType();
  }

  public Type visit(False n) {
    return new BooleanType();

  }

  // String s;
  public Type visit(IdentifierExp n) {
    String t = null;
    
    // Checa se é um parâmetro formal do método
    for(Iterator<FormalArg> it = this.metodo_atual.formals.iterator(); it.hasNext();)
    {
      FormalArg fa = it.next();
      if (fa.getName().equals(n.s))
      {
        t = fa.getType().toString();
      }
    }

    // Checa se é uma variável local do método
    if((t == null) && metodo_atual.locals.table.containsKey(n.s))
    {
      t = metodo_atual.locals.table.get(n.s);
    }

    // Checa se é um membro da classe atual
    if((t == null) && classe_atual.attrs.table.containsKey(n.s))
    {
      t = classe_atual.attrs.table.get(n.s);
    }

    // Checa se é um membro de alguma classe ancestral a classe atual
    boolean has_parent = false;
    ClassTable parent = null;
    if((t == null) && (classe_atual.parent != null))
    {
      has_parent = true;
      parent = classe_atual.parent;
    }

    while(has_parent)
    {
      if(parent.attrs.table.containsKey(n.s))
      {
        t = parent.attrs.table.get(n.s);
        break;
      }
      if(parent.parent != null)
      {
        parent = parent.parent;
      }
      else
      {
        has_parent = false;
      }
    }

    if(t == null)
    {
      System.out.println("Identificador não declarado");
      return null;
    }
    else
    {
      if(t.equals("int"))
      {
        return new IntegerType();
      }
      else if(t.equals("int[]"))
      {
        return new IntArrayType();
      }
      else if(t.equals("boolean"))
      {
        return new BooleanType();
      }
      else
      {
        return new IdentifierType(t);
      }
    }
  }

  public Type visit(This n) {
    return new IdentifierType(classe_atual.getName());
  }

  // Exp e;
  public Type visit(NewArray n) {
    Type et = n.e.accept(this);

    if(!(et instanceof IntegerType))
    {
      System.out.println("Expressão precisa ser um inteiro");
      return null;
    }
    return new IntArrayType();
  }

  // Identifier i;
  public Type visit(NewObject n) {
    Type t = n.i.accept(this);
    if(!(t instanceof IdentifierType))
    {
      System.out.println("Classe não é um identificador válido");
      return null;
    }
    return new IdentifierType(t.toString());
    
  }

  // Exp e;
  public Type visit(Not n) {
    Type et = n.e.accept(this);

    if(!(et instanceof BooleanType))
    {
      System.out.println("Expressão precisa ter tipo boolean");
      return null;
    }
    return new BooleanType();
  }

  // String s;
  public Type visit(Identifier n) {
    Type t = null;

    // Checa se é o identificardor da MainClass
    if(this.mainTable.getName().equals(n.s))
    {
      return null;
    }

    // Checa se é o identificador de uma classe declarada no código
    for(Iterator<ClassTable> it =  this.classTableList.iterator(); it.hasNext();)
    {
      ClassTable cl = it.next();
      if(cl.getName().equals(n.s))
      {
        return new IdentifierType(n.s);
      }
    }

    if(metodo_atual != null) // Caso estejamos dentro de um método
    {
      // Checa se é o identificador do método atual
      if(this.metodo_atual.getName().equals(n.s))
      {
        if(this.metodo_atual.getType().equals("int"))
        {
          return new IntegerType();
        }
        else if(this.metodo_atual.getType().equals("int[]"))
        {
          return new IntArrayType();
        }
        else if(this.metodo_atual.getType().equals("boolean"))
        {
          return new BooleanType();
        }
        else
        {
          return new IdentifierType(this.metodo_atual.getType());
        }
      }

      // Checa se é o identificador de um parâmetro formal do método
      for(Iterator<FormalArg> it = this.metodo_atual.formals.iterator(); it.hasNext();)
      {
        FormalArg fa = it.next();
        if (fa.getName().equals(n.s))
        {
          t = fa.getType();
        }
      }

      // Checa se é uma variável local do método
      if(metodo_atual.locals.table.containsKey(n.s))
      {
        String temp = metodo_atual.locals.table.get(n.s);

        if(temp.equals("int"))
        {
          return new IntegerType();
        }
        else if(temp.equals("int[]"))
        {
          return new IntArrayType();
        }
        else if(temp.equals("boolean"))
        {
          return new BooleanType();
        }
        else
        {
          return new IdentifierType(temp);
        }
      }
    }

    // Checa se é um membro da classe atual
    if(classe_atual.attrs.table.containsKey(n.s))
    {
      String temp = classe_atual.attrs.table.get(n.s);

      if(temp.equals("int"))
      {
        return  new IntegerType();
      }
      else if(temp.equals("int[]"))
      {
        return new IntArrayType();
      }
      else if(temp.equals("boolean"))
      {
        return new BooleanType();
      }
      else
      {
        return new IdentifierType(temp);
      }
    }

    // Checa se é um membro de alguma classe ancestral a classe atual
    boolean has_parent = false;
    ClassTable parent = null;
    if(classe_atual.parent != null)
    {
      has_parent = true;
      parent = classe_atual.parent;
    }

    while(has_parent)
    {
      if(parent.attrs.table.containsKey(n.s))
      {
        String temp = parent.attrs.table.get(n.s);
        if(temp.equals("int"))
        {
          return  new IntegerType();
        }
        else if(temp.equals("int[]"))
        {
          return new IntArrayType();
        }
        else if(temp.equals("boolean"))
        {
          return new BooleanType();
        }
        else
        {
          return new IdentifierType(temp);
        }
      }
      else
      {
        if(parent.parent != null)
        {
          parent = parent.parent;
        }
        else
        {
          has_parent = false;
        }
      }
    }
    
    // Caso seja chamado durante uma chamada de método, checa se é o identificador de 
    // um método da classe cujo método está sendo chamado
    if(classe_call_global != null)
    {
        for (Iterator<Method> it = this.classe_call_global.methods.iterator(); it.hasNext();)
        {
          Method metodo = it.next();
          String temp = metodo.getType();
          if(metodo.getName().equals(n.s))
          {
            if(metodo.getType().equals("int"))
            {
              return  new IntegerType();
            }
            else if(metodo.getType().equals("int[]"))
            {
              return new IntArrayType();
            }
            else if(metodo.getType().equals("boolean"))
            {
              return new BooleanType();
            }
            else
            {
              return new IdentifierType(temp);
            }
          }
        }
    }

    // Caso nenhum dos casos acima, erro
    if(t==null)
    {
      System.out.println("Variável não declarada");
      return null;
    }
    else
    {
      if(t instanceof IdentifierType)
      {
        return new IdentifierType(t.toString());
      }
      if(t instanceof IntegerType)
      {
        return new IntegerType();
      }
      if(t instanceof IntArrayType)
      {
        return new IntArrayType();
      }
      if(t instanceof BooleanType)
      {
        return new BooleanType();
      }
    }
    return null;
  }

}
