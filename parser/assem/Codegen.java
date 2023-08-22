package assem;

import tree.*;
import frame.Frame;
import mips.MipsFrame;
import temp.*;
import java.util.*;

public class Codegen {
    MipsFrame frame;
    public Codegen(MipsFrame f) {frame=f;}
    private InstrList ilist=null, last=null;

    private void emit(Instr inst) {
        if (last!=null)
        {
            last = last.tail = new InstrList(inst,null);
        }
        else
        {
            last = ilist = new InstrList(inst,null);
        }
    }

    public InstrList codegen(tree.Stm s) {
        InstrList l;
        munchStm(s);
        l=ilist;
        ilist=last=null;
        return l;
    }

    void munchStm(tree.Stm s) {
        if (s instanceof tree.SEQ)
        {
            munchStm(((tree.SEQ)s).left);
            munchStm(((tree.SEQ)s).right);
        }
        else if(s instanceof tree.MOVE)
        {
            munchMOVE((tree.MOVE)s);
        }
        else if(s instanceof tree.LABEL)
        {
            munchLABEL((tree.LABEL)s);
        }
        else if(s instanceof tree.CJUMP)
        {
            munchCJUMP((tree.CJUMP)s);
        }
        else if(s instanceof tree.JUMP)
        {
            munchJUMP((tree.JUMP)s);
        }
        else if(s instanceof tree.EXPR)
        {
            munchCALL((tree.CALL)((tree.EXPR)s).exp);
        }
    }

    void munchMOVE(tree.MOVE s)
    {
        if((s.dst instanceof tree.MEM) && (s.src instanceof tree.MEM))
        {
            temp.Temp temp_dst = munchExp(((tree.MEM)s.dst).exp);
            temp.Temp temp_src = munchExp(((tree.MEM)s.src).exp);
            TempList tail      = new TempList(temp_src, null);
            TempList head      = new TempList(temp_dst, tail);
            //OPER op            = new OPER("MOVE M[`s0] <- M[`s1]", null, head);
            OPER op            = new OPER("move $t0,$t2", null, head); // Intrução na MIPS???
            emit(op);
        }
        else if((s.dst instanceof tree.MEM) && (s.src instanceof tree.EXP))
        {
            if(((tree.MEM)s.dst).exp instanceof tree.BINOP)
            {

            if (((tree.BINOP)((tree.MEM)s.dst).exp).binop == BINOP.PLUS) 
            {
                if (((tree.BINOP)((tree.MEM)s.dst).exp).right instanceof tree.CONST) 
                {
                    temp.Temp t1 = munchExp(((tree.BINOP) ((tree.MEM)s.dst).exp).left);
                    temp.Temp t2 = munchExp(s.src);
                    temp.TempList l1 = new TempList(t1, null);
                    temp.TempList l2 = new TempList(t2, null);
                    assem.OPER op = new OPER("move $t0,$t2", l1, l2);
                    emit(op);    
                } 
                else if (((tree.BINOP) ((tree.MEM)s.dst).exp).left instanceof tree.CONST)
                {
                    temp.Temp t2     = munchExp(s.src);
                    temp.TempList l2 = new TempList(t2, null);
                    temp.Temp t1     = munchExp(((tree.BINOP) ((tree.MEM)s.dst).exp).right);
                    temp.TempList l1  = new TempList(t1, null);   
                    assem.OPER op =  new OPER("move $t0,$t2", l1, l2);
                    emit(op);     
                }
            }

                // if(((tree.BINOP)((tree.MEM)s.dst).exp).left instanceof tree.EXP)
                // {
                //     TempList tail = new TempList(munchExp(s.src), null);
                //     TempList head = new TempList(munchExp(((tree.BINOP)((tree.MEM)s.dst).exp).left), tail);
                //     OPER op       = new OPER("STORE M[`s0+" + ((tree.CONST)((tree.BINOP)((tree.MEM)s.dst).exp).right).value + "] <- `s1", null, head);
                //     //OPER op       = new OPER("sw$t1, " + s.dst.exp.right.value + "($t0)", null, head); // Intrução na MIPS???
                //     emit(op);
                // }
                // else
                // {
                //     TempList tail = new TempList(munchExp(s.src), null);
                //     TempList head = new TempList(munchExp(((tree.BINOP)((tree.MEM)s.dst).exp).right), tail);
                //     OPER op       = new OPER("STORE M[`s0+" + ((tree.CONST)((tree.BINOP)((tree.MEM)s.dst).exp).left).value + "] <- `s1", null, head);
                //     //OPER op       = new OPER("sw$t1, " + s.dst.exp.left.value + "($t0)", null, head); // Intrução na MIPS???
                //     emit(op);
                // }
            }
            else if(((tree.MEM)s.dst).exp instanceof tree.CONST)
            {
                TempList l = new TempList(munchExp(s.src), null);
                //OPER op    = new OPER("STORE M[r0+" + ((tree.CONST)((tree.MEM)s.dst).exp).value + "] <- `s0", null, l);
                OPER op    = new OPER("sw$t0, " + ((tree.CONST)((tree.MEM)s.dst).exp).value  + "($zero)", null, l); // Intrução na MIPS???
                emit(op);
            }
            else
            {
                TempList tail = new TempList(munchExp(s.src), null);
                TempList head = new TempList(munchExp(((tree.MEM)s.dst).exp), tail);
                //OPER op       = new OPER("STORE M[`s0] <- `s1", null, head);
                OPER op       = new OPER("sw$t1, 0($t0)", null, head); // Intrução na MIPS???
                emit(op);
            }
        }
        
        else
        {
            TempList l1 = new TempList(((tree.TEMP)s.dst).temp, null);
            TempList l2 = new TempList(munchExp(s.src), null);
            //OPER op     = new OPER("ADD d0 <-s0 + r0", l1, l2);
            OPER op     = new OPER("add $s0,$t0,$r0", l1, l2); // Intrução na MIPS???
            emit(op);
        }
    }

    void munchLABEL(tree.Stm s)
    {
        emit(new LABEL(((tree.LABEL)s).label.toString() + ":", ((tree.LABEL)s).label));
    }

    void munchCJUMP(tree.CJUMP s)
    {   
        String relop = "";
        switch(s.relop){
            case 0://EQ
                relop = "beq";
                break;
            case 2://LT
                relop = "blt";
                break;
        }
        temp.Temp l = munchExp(s.left);
        temp.Temp r = munchExp(s.right);
        TempList l1 = new TempList(l,new TempList(r,null));
        temp.LabelList ll = new temp.LabelList(s.iftrue,new temp.LabelList(s.iffalse,null));
        OPER op = new OPER(relop+" $s0,$s1,$d0",null,l1,ll);
        emit(op);
    }

    void munchJUMP(tree.Stm s)
    {   
        OPER op = new OPER("j j0",null,null,((tree.JUMP)s).targets);
        emit(op);
    }

    void munchCALL(tree.CALL s){
        temp.Temp r      = munchExp(s.func); 
        temp.TempList l  = munchARGS(0,s.args);
        NAME name        = (tree.NAME)s.func;
        temp.TempList l1 = new TempList(r,l);

        //OPER op = new OPER("jal "+name.label+"",this.frame.calldefs,l1);
        OPER op = new OPER("jal "+name.label+"",null,l1);

        emit(op);
    }

    temp.TempList munchARGS(int i, List<EXP> args){
        if (args.isEmpty()){
            return new temp.TempList(null,null);
        }
        else{
            tree.EXP e = args.remove(0);
            return new temp.TempList(munchExp(e),munchARGS(0,args));
        }   
    }

    temp.Temp munchExp(tree.EXP e)
    {
        if (e instanceof tree.MEM){
            return munchMEM((tree.MEM)e);
        }
        else if (e instanceof tree.BINOP){
            return munchBINOP((tree.BINOP)e);
        }
        else if (e instanceof tree.CONST){
            return munchCONST((tree.CONST)e);
        }
        else if (e instanceof tree.TEMP){
            return munchTEMP((tree.TEMP)e);
        }
        return null;
    }

    temp.Temp munchMEM(tree.MEM e){
        temp.Temp t = new temp.Temp();
        TempList l1 = new TempList(t,null);

        // if (e.exp instanceof tree.BINOP){
        //     if(((BINOP)e.exp).left instanceof tree.EXP){
                
        //         TempList l2 = new TempList(munchExp(((BINOP)e.exp).left),null);
        //         OPER op = new OPER("LOAD `d0 <- M[`s0+" + ((CONST)((BINOP)e.exp).right).value + "]",l1,l2);
        //         emit(op);
        //     }
        //     else{
        //         TempList l2 = new TempList(munchExp(((BINOP)e.exp).right),null);
        //         OPER op = new OPER("LOAD `d0 <- M[`s0+" + ((CONST)((BINOP)e.exp).left).value + "]",l1,l2);
        //         emit(op);
        //     }
        // }
        // else if(e.exp instanceof tree.CONST){ 
        //     OPER op = new OPER("LOAD `d0 <- M[r0+" + ((CONST)e.exp).value + "]",l1,null);
        //     emit(op);
        // }
        // else{
        //     TempList l2 = new TempList(munchExp(e.exp),null);
        //     OPER op = new OPER("LOAD `d0 <- M[`s0+0]",l1,l2);
        //     emit(op);
        // }

        if (e.exp instanceof tree.BINOP && ((tree.BINOP)e.exp).binop == tree.BINOP.PLUS)
        {
            tree.EXP r = ((tree.BINOP)e.exp).right;
            tree.EXP l = ((tree.BINOP)e.exp).left;

            if(r instanceof tree.CONST)
            {
                TempList s = new TempList(munchExp(l),null);
                emit(new OPER("lw d0," + ((tree.CONST)r).value + "(s0)", l1 ,s));
            }
            else if(l instanceof tree.CONST)
            {
                TempList s = new TempList(munchExp(r),null);
                emit(new OPER("lw d0,"+((tree.CONST)l).value+"(s0) ",l1 ,s));
            }

        }
        else if (e.exp instanceof tree.CONST)
        {
            tree.CONST c = (tree.CONST)e.exp;
            emit(new OPER("lw d0, "+c.value+"($zero) ", l1 ,null));
        }
        else
        {
            emit(new OPER("lw d0,s0", l1,new TempList(munchExp(e.exp),null)));
        }
        return t;    
    }

    // Corrigir
    temp.Temp munchBINOP(tree.BINOP e){
        temp.Temp r = new temp.Temp();
        TempList l1 = new TempList(r,null);

        if(e.binop == tree.BINOP.PLUS)
        {
            if(e.left instanceof tree.EXP){
                if (e.right instanceof tree.CONST){
                    TempList l2 = new TempList(munchExp(e.left),null);
                    OPER op = new OPER("addi d0,s0," + ((CONST)e.right).value + "",l1,l2);
                    emit(op);
                }
                else{
                    TempList l3 = new TempList(munchExp(e.right),null);
                    TempList l2 = new TempList(munchExp(e.left),l3);
                    OPER op = new OPER("add d0,s0,s1",l1,l2);
                    emit(op);
                }
            }
            else{
                TempList l2 = new TempList(munchExp(e.right),null);
                OPER op = new OPER("addi d0,s0," + ((CONST)e.left).value + "",l1,l2);
                emit(op);
                }
        }
        else if(e.binop == tree.BINOP.MINUS)
        {
            TempList l3 = new TempList(munchExp(e.right),null);
            TempList l2 = new TempList(munchExp(e.left),l3);
            OPER op = new OPER("sub d0,s1,s2", l1, l2);
            emit(op);
        }
        else if(e.binop == tree.BINOP.DIV)
        {
            TempList l3 = new TempList(munchExp(e.right),null);
            TempList l2 = new TempList(munchExp(e.left),l3);
            OPER op = new OPER("div s1,s2", null, l2);
            emit(op);
        }
        else if(e.binop == tree.BINOP.MUL)
        {
            TempList l3 = new TempList(munchExp(e.right),null);
            TempList l2 = new TempList(munchExp(e.left),l3);
            OPER op = new OPER("mul d0,s1,s2", l1, l2);
            emit(op);
        }
        else
        {
            TempList l3 = new TempList(munchExp(e.right),null);
            TempList l2 = new TempList(munchExp(e.left),l3);
            OPER op = new OPER("and d0,s1,s2", l1, l2);
            emit(op);
        }
        return r;
    }

    temp.Temp munchCONST(tree.CONST e){
        temp.Temp r = new temp.Temp();
        TempList l  = new TempList(r,null);
        OPER op = new OPER("addi d0,r0+" + e.value + "",null,l);
        return r;
    }

    temp.Temp munchTEMP(tree.TEMP e){
        return e.temp;
    }
}