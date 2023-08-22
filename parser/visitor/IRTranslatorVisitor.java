package visitor;
import tree.*;
import syntaxtree.*;
import java.util.*;
import frame.*;
import translate.*;
import table.*;
import temp.*;

public class IRTranslatorVisitor implements IRVisitor
{
    private Frame currentFrame;
    private Method currentMethod;
    public List<ClassTable> classTableList;
    public ClassTable mainClass;
    public ClassTable currentClass;
    public Frag frags = new Frag();
    //public List<Frag> frags = new ArrayList<Frag>();

    public IRTranslatorVisitor(Frame frame, TypeCheckVisitor tcv)
    {
        this.currentFrame   = frame;
        this.classTableList = tcv.classTableList;
        this.mainClass      = tcv.mainTable;
        this.currentMethod  = null;
    }

    public Ex visit(Program n)
    {
        n.m.accept(this);

        for (int i = 0; i < n.cl.size() ; i++) {
            n.cl.elementAt(i).accept(this);
        }   

        return null;
    }

    public Ex visit(MainClass n)
    {
        this.currentClass = this.mainClass;
        List<Boolean> escapes = new ArrayList<Boolean>();
        escapes.add(false);
        this.currentFrame = this.currentFrame.newFrame(n.i1.toString(), escapes); // Perde referencia ao pai, pode????????

        Ex exp    = n.s.accept(this);
        EXP temp1 = exp.unEx();
        Stm func;
        if(temp1 != null)
        {
            func  = new  MOVE(new TEMP(this.currentFrame.RV()), new ESEQ(new tree.EXPR(temp1), new CONST(1)));
        }
        else
        {
            func  = new  MOVE(new TEMP(this.currentFrame.RV()), new ESEQ(new tree.EXPR(new CONST(0)), new CONST(1)));
        }
        
        List<Stm> body = new ArrayList<Stm>();
        body.add(func);

        this.currentFrame.procEntryExit1(body); // ????????

        //this.frags.add(new ProcFrag(body, this.currentFrame));
        this.frags.setNext(new ProcFrag(func, this.currentFrame));
        //this.frags = this.frags.getNext();

        return null;
    }

    public Ex visit(ClassDeclSimple n)
    {
        for(int i = 0; i < this.classTableList.size(); i++)
        {
            ClassTable curr_class = this.classTableList.get(i);
            if (curr_class.name == n.i.toString()) {
                this.currentClass = curr_class;
            }
        }

        n.i.accept(this);

        for (int i = 0; i < n.vl.size() ; i++) {
            n.vl.elementAt(i).accept(this);            
        }
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.elementAt(i).accept(this);            
        }

        return null;
    }

    public Ex visit(ClassDeclExtends n)
    {
        for(int i = 0; i < this.classTableList.size(); i++)
        {
            ClassTable curr_class = this.classTableList.get(i);
            if (curr_class.name == n.i.toString()) {
                this.currentClass = curr_class;
            }
        }

        n.i.accept(this);
        n.j.accept(this);

        for (int i = 0; i < n.vl.size() ; i++) {
            n.vl.elementAt(i).accept(this);            
        }
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.elementAt(i).accept(this);            
        }

        return null;
    }

    public Ex visit(VarDecl n)
    {
        n.t.accept(this);
        n.i.accept(this);

        return null;
    }

    public Ex visit(MethodDecl n)
    {
        // Busca a tabela de simbolos do método
        for(int i = 0; i < this.currentClass.methods.size(); i++)
        {
            Method met = this.currentClass.methods.get(i);
            if(met.getName().equals(n.i.toString()))
            {
                this.currentMethod = met;
            }
        }

        List<Boolean> escapes = new ArrayList<Boolean>();
        // Cria a lista de booleanos indicando, para cada formal, se ele escapa ou não (nunca escapa)
        for(int i = 0; i<n.fl.size(); i++)
        {
            escapes.add(false);
        }

        // Cria o frame relativo ao método atual
        this.currentFrame = this.currentFrame.newFrame(this.currentClass.getName()+"."+this.currentMethod.getName(), escapes);

        // Accept nas declarações de parâmetros formais
        for(int i = 0; i <n.fl.size(); i++)
        {
            n.fl.elementAt(i).accept(this);
        }

        // Accept nas declarações de variáveis locais
        for(int i = 0; i <n.vl.size(); i++)
        {
            n.vl.elementAt(i).accept(this);
        }


        Stm body = new tree.EXPR(new CONST(0));
        // Representação em árvore, dos statments que compõem o corpo do método
        for(int i = 0; i <n.sl.size(); i++)
        {
            EXP temp1 = n.sl.elementAt(i).accept(this).unEx();
            if(temp1 != null)
            {
                body = new SEQ(body, new tree.EXPR(temp1)); 
            }
            else
            {
                body = new SEQ(body, new tree.EXPR(new CONST(0))); 
            }
        }

        tree.EXP ret  = new ESEQ(body, n.e.accept(this).unEx()); // Executa o corpo e avalia a tree.EXPressão de retorno
        Stm func = new MOVE(new TEMP(this.currentFrame.RV()), ret); // Move o resultado da tree.EXPressão de retorno para o registrador adequado

        List<Stm> body_list = new ArrayList<Stm>();
        body_list.add(func);

        this.currentFrame.procEntryExit1(body_list); // Adiciona o prólogo e o epílogo

        //this.frags.add(new ProcFrag(body_list, this.currentFrame)); // Cria o fragmento relativo ao método
        Frag next = this.frags;

        while(next.hasNext())
        {
            next = next.getNext();
        }
        next.setNext(new ProcFrag(func, this.currentFrame));
        //this.frags = this.frags.getNext();

        return null;
    }

    public Ex visit(Formal n)
    {
        n.t.accept(this);
        n.i.accept(this);
        return null;
    }

    public Ex visit(IntArrayType n)
    {
        return null;
    }
    
    public Ex visit(BooleanType n)
    {
        return null;
    }
    
    public Ex visit(IntegerType n)
    {
        return null;
    }
    
    public Ex visit(IdentifierType n)
    {
        return null;
    }
    
    public Ex visit(Block n)
    {
        EXP temp1 = n.sl.elementAt(0).accept(this).unEx();
        SEQ acc;
        if(temp1 != null)
        {
            acc = new SEQ(new EXPR(new CONST(0)), new EXPR(temp1));
        }
        else
        {
            acc = new SEQ(new EXPR(new CONST(0)), new EXPR(new CONST(0)));
        }
        
        tree.EXPR curr_stm;
        for(int i = 1; i<n.sl.size(); i++)
        {
            EXP temp2 = n.sl.elementAt(i).accept(this).unEx();
            if(temp2!=null)
            {
                curr_stm = new tree.EXPR(temp2);
            }
            else
            {
                curr_stm = new tree.EXPR(new CONST(0));
            }
            
            acc = new SEQ(acc, curr_stm); 
        }

        return new Ex(new ESEQ(acc, new CONST(1)));
    }

    public Ex visit(If n)
    {
        Ex ex   = n.e.accept(this);
        Ex stm1 = n.s1.accept(this);
        Ex stm2 = n.s2.accept(this);

        Label t   = new Label();
        Label f   = new Label();
        Label end = new Label();

        // Checa se a tree.EXPressão é V ou F e desvia pro label adequado
        CJUMP cjump = new CJUMP(CJUMP.EQ, ex.unEx(), new CONST(1), t, f);
        
        // Executa o Stm para V, e pula para o final
        EXP temp1 = stm1.unEx();
        SEQ true_jump;
        if(temp1 != null)
        {
            true_jump = new SEQ(new EXPR(temp1), new JUMP(end));
        }
        else
        {
            true_jump = new SEQ(new EXPR(new CONST(0)), new JUMP(end));
        }
        
        
        // Label t com execução do Stm p V 
        SEQ True    = new SEQ(new LABEL(t), true_jump);
        // Executa o Stm p F, e continua
        EXP temp2 = stm2.unEx();
        SEQ False;
        if(temp2 != null)
        {
            False   = new SEQ(new LABEL(f), new EXPR(temp2));
        }
        else
        {
            False   = new SEQ(new LABEL(f), new EXPR(new CONST(0)));
        }
        

        // Caso V ou caso F
        SEQ trueOrFalse = new SEQ(True, False);
        
        // Corpo do if
        SEQ mainSEQ = new SEQ(cjump, trueOrFalse);

        return new Ex(new ESEQ(new SEQ(mainSEQ, new LABEL(end)), new CONST(1)));
    }

    public Ex visit(While n)
    {
        Ex ex  = n.e.accept(this);
        Ex stm = n.s.accept(this);

        Label body = new Label();
        Label end  = new Label();
        Label test = new Label();

        CJUMP cjump = new CJUMP(CJUMP.EQ, ex.unEx(), new CONST(1), body, end);
        JUMP jump   = new JUMP(test);

        SEQ check = new SEQ(new LABEL(test), cjump);
        EXP temp1 = stm.unEx();
        SEQ exec;
        if(temp1 != null)
        {
            exec = new SEQ(new LABEL(body), new EXPR(temp1));
        }
        else
        {
            exec = new SEQ(new LABEL(body), new EXPR(new CONST(0)));
        }
        
        SEQ loop = new SEQ(exec, jump);
        
        SEQ While = new SEQ(check, loop);

        SEQ main = new SEQ(While, new LABEL(end));

        return new Ex(new ESEQ(main, new CONST(1)));
    }

    public Ex visit(syntaxtree.Print n)
    {
        Ex e = n.e.accept(this);

        List<tree.EXP> args = new ArrayList<tree.EXP>();
        args.add(e.unEx());

        tree.EXP call = currentFrame.externalCall("print", args);

        return new Ex(call);
    }

    public Ex visit(Assign n)
    {
        EXP var = n.i.accept(this).unEx();
        EXP val = n.e.accept(this).unEx();

        return new Ex(new ESEQ(new MOVE(new MEM(var), val), new CONST(1)));
    }

    public Ex visit(ArrayAssign n)
    { 
        Ex array = n.i.accept(this); // Posição de memória inicial do array (armazena o tamanho do array)
        Ex pos   = n.e1.accept(this); // Posição sendo acessada
        Ex value   = n.e2.accept(this); // Valor sendo atribuído à posição indicada

        Label t = new Label();
        Label f = new Label();
        
        // Checa se a posição acessada é uma posição válida do array
        Stm val = new CJUMP(CJUMP.LT, pos.unEx(), new MEM(array.unEx()), t, f);

        BINOP offset_pos   = new BINOP(BINOP.PLUS, new CONST(1), pos.unEx()); // Calcula o offset do elemento, em posições
        BINOP offset_bytes = new BINOP(BINOP.MUL, offset_pos, new CONST(currentFrame.wordSize())); // Calcula o offset em wordSizes
        BINOP elem_pos     = new BINOP(BINOP.PLUS, offset_bytes, array.unEx()); // Calcula o endereço do elemento

        // Caso a posição seja válida
        Stm val_pos = new SEQ(new LABEL(t), new MOVE(new MEM(elem_pos), value.unEx()));

        // Checa se a posição é valida e executa de acordo
        Stm res = new SEQ(new SEQ(val, val_pos), new LABEL(f));
        
        return new Ex(new ESEQ(res, new CONST(1))); // É necessário um retorno específico?? (Ex: CONST(1) se for um sucesso CONST(0) caso haja falha)
    }

    public Ex visit(And n)
    {
        Ex e1 =  n.e1.accept(this);
        Ex e2 =  n.e2.accept(this);

        Label t = new Label(); // Label se a primeira tree.EXPressão for V
        Label f = new Label(); // Label final
        Label tt = new Label(); // Label se a segunda tree.EXPressão for V
        
        Temp out = new Temp(); // Registrador que armazena o resultado

        Stm init_out = new MOVE(new TEMP(out), new CONST(0)); // Inicializa o valor como 0 (falso)
        Stm ok = new MOVE(new TEMP(out), new CONST(1)); // Caso ambas as expressões sejam V, muda o resultado para 1 (verdadeiro)

        Stm ex1 = new CJUMP(CJUMP.EQ, e1.unEx(), new CONST(1), t, f); // Checa se a primeira tree.EXP é V, se sim, checa a segunda, senão, vai ao final
        Stm ex2 = new CJUMP(CJUMP.EQ, e2.unEx(), new CONST(1), tt, f); // Checa se a segunda tree.EXP é V, se sim, muda o resultado para V, senão, vai ao final
        
        // Monta a árvore IR relativa ao && 
        Stm stm = new SEQ(new SEQ(ex1, new SEQ(new LABEL(t), ex2)), new SEQ(new LABEL(tt), new SEQ(ok, new LABEL(f))));

        // Executa a árvore e retorna o resultado
        return new Ex(new ESEQ(stm, new MEM(new TEMP(out))));
    }

    public Ex visit(LessThan n)
    {
        Ex e1 =  n.e1.accept(this);
        Ex e2 =  n.e2.accept(this);
        
        Label t = new Label();
        Label f = new Label();
        Temp result = new Temp();

        // Resultado inicializado como zero
        Stm init_result = new MOVE(new TEMP(result), new CONST(0));
        
        // Condicional
        Stm lt = new CJUMP(CJUMP.LT, e1.unEx(), e2.unEx(), t, f);

        // Monta a árvore IR relativa ao <
        Stm res = new SEQ(new SEQ(new SEQ(init_result, lt), new SEQ(new LABEL(t), new MOVE(new TEMP(result), new CONST(1)))), new LABEL(f));

        // Executa a árvore e retorna o resultado
        return new Ex(new ESEQ(res, new TEMP(result)));
    }

    public Ex visit(Plus n)
    {
        Ex e1 =  n.e1.accept(this);
        Ex e2 =  n.e2.accept(this);

        return new Ex(new BINOP(BINOP.PLUS, e1.unEx(), e2.unEx()));
    }

    public Ex visit(Minus n)
    {
        Ex e1 =  n.e1.accept(this);
        Ex e2 =  n.e2.accept(this);

        return new Ex(new BINOP(BINOP.MINUS, e1.unEx(), e2.unEx()));
    }

    public Ex visit(Times n)
    {
        Ex e1 =  n.e1.accept(this);
        Ex e2 =  n.e2.accept(this);

        return new Ex(new BINOP(BINOP.MUL, e1.unEx(), e2.unEx()));
    }

    public Ex visit(ArrayLookup n)
    {
        Ex array = n.e1.accept(this);
        Ex pos   = n.e2.accept(this);

        Label val = new Label();
        Label inv = new Label();
        Temp out = new Temp();

        Stm validation = new CJUMP(CJUMP.LT, pos.unEx(), new MEM(array.unEx()), val, inv); // Checa se a posição é válida

        BINOP offset_pos   = new BINOP(BINOP.PLUS, new CONST(1), pos.unEx()); // Calcula o offset (considerando que o primeiro elemento é o tamanho do vetor)
        BINOP offset_bytes = new BINOP(BINOP.MUL, offset_pos, new CONST(currentFrame.wordSize())); // Calcula o offset em bytes
        BINOP mem_pos      = new BINOP(BINOP.PLUS, offset_bytes, array.unEx()); // Calcula a posição de memória de fato

        Stm valid_pos = new SEQ(new LABEL(val), new MOVE(new TEMP(out), new MEM(mem_pos))); // Caso a posição seja válida
        
        Stm res = new SEQ(new SEQ(validation, valid_pos), new LABEL(inv)); // Executa a operação em si
        
        return new Ex(new ESEQ(res, new TEMP(out))); // Executa tudo e retorna o valor (O QUE RETORNAR SE A POS FOR INVÁLIDA????)
    }

    public Ex visit(ArrayLength n)
    {
        Ex array = n.e.accept(this);

        return new Ex(new MEM(array.unEx()));
    }
    
    public Ex visit(Call n)
    {
        List<tree.EXP> args = new ArrayList<tree.EXP>();
        Ex id = n.e.accept(this);
        String c_name = null;

        for(int i = 0; i < n.el.size() ; i++)
        {
            args.add(n.el.elementAt(i).accept(this).unEx());
        }

        if(n.e instanceof This)
        {
            c_name = this.currentClass.getName();
        }
        
        if(n.e instanceof NewObject)
        {
            c_name = ((NewObject)n.e).i.toString();
        }

        if(n.e instanceof IdentifierExp)
        {
            for(Iterator<FormalArg> it = this.currentMethod.formals.iterator(); it.hasNext();)
            {
                FormalArg nxt = it.next();
                if(nxt.getName() == ((IdentifierExp)n.e).s)
                {
                    c_name = nxt.getType().toString();
                    break;
                }
            }
            if (c_name == null)
            {
                c_name = this.currentMethod.locals.table.get(((IdentifierExp)n.e).s);

                if(c_name == null)
                {
                    c_name = this.currentClass.attrs.table.get(((IdentifierExp)n.e).s);
                }
                
            }
        }
        
        if (c_name != null)
        {
            return new Ex(new CALL(new NAME(new Label(c_name+"."+n.i.toString())), args));
        }
        else
        {
            return new Ex(new CONST(0)); // COLOCAR O RETORNO APROPRIADO
        }
        
    }

    public Ex visit(IntegerLiteral n)
    {
        return new Ex(new CONST(n.i));
    }

    public Ex visit(True n)
    {
        return new Ex(new CONST(1));
    }

    public Ex visit(False n)
    {
        return new Ex(new CONST(0));
    }

    public Ex visit(IdentifierExp n)
    {
        Access a = currentFrame.allocLocal(false);

        return new Ex(a.exp(new TEMP(currentFrame.FP())));
    }

    public Ex visit(This n)
    {
        return new Ex(new MEM(new TEMP(currentFrame.FP())));
    }

    public Ex visit(NewArray n)
    {
        Ex size = n.e.accept(this);

        BINOP size_final = new BINOP(BINOP.PLUS, new CONST(1), size.unEx());
        BINOP bytes = new BINOP(BINOP.MUL, size_final, new CONST(currentFrame.wordSize()));

        List<tree.EXP> args = new ArrayList<tree.EXP>();
        args.add(bytes);

        tree.EXP call = currentFrame.externalCall("initArray", args);

        return new Ex(call);
    }

    public Ex visit(NewObject n)
    {
        Ex id = n.i.accept(this);
        int sz = 0;
        // percorrer this.classTableList até encontrar id
        // na tabela de id, descobrir quantos attrs ela tem
        for(int i = 0; i < this.classTableList.size(); i++)
        {
            if (this.classTableList.get(i).name == n.i.toString()) { 
                sz = this.classTableList.get(i).attrs.table.size();
            }
        }
        BINOP size = new BINOP(BINOP.PLUS, new CONST(1), new CONST(sz));
        BINOP bytes = new BINOP(BINOP.MUL, size, new CONST(currentFrame.wordSize()));

        List<tree.EXP> args = new ArrayList<tree.EXP>();
        args.add(bytes);

        CALL call = currentFrame.externalCall("malloc", args);

        return new Ex(call);
    }

    public Ex visit(Not n)
    {
        Ex e =  n.e.accept(this);

        return new Ex(new BINOP(BINOP.XOR, new CONST(1), e.unEx()));
    }

    public Ex visit(Identifier n)
    {
        Access a = currentFrame.allocLocal(false);

        return new Ex(a.exp(new TEMP(currentFrame.FP())));
    }
}