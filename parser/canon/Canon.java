package canon;
import java.util.*;
import tree.*;
class MoveCall extends tree.Stm {
  tree.TEMP dst;
  tree.CALL src;
  MoveCall(tree.TEMP d, tree.CALL s) {dst=d; src=s;}
  public List<EXP> kids() {return src.kids();}
  public tree.Stm build(List<EXP> kids) {
	return new tree.MOVE(dst, src.build(kids));
  }
}   
  
class ExpCall extends tree.Stm {
  tree.CALL call;
  ExpCall(tree.CALL c) {call=c;}
  public List<EXP> kids() {return call.kids();}
  public tree.Stm build(List<EXP> kids) {
	return new tree.EXPR(call.build(kids));
  }
}   
  
class StmExpList {
  tree.Stm stm;
  List<EXP> exps;
  StmExpList(tree.Stm s, List<EXP> e) {stm=s; exps=e;}
}

public class Canon {
  
 static boolean isNop(tree.Stm a) {
   return a instanceof tree.EXPR
          && ((tree.EXPR)a).exp instanceof tree.CONST;
 }

 static tree.Stm seq(tree.Stm a, tree.Stm b) {
    if (isNop(a)) return b;
    else if (isNop(b)) return a;
    else return new tree.SEQ(a,b);
 }

 static boolean commute(tree.Stm a, tree.EXP b) {
    return isNop(a)
        || b instanceof tree.NAME
        || b instanceof tree.CONST;
 }

 static tree.Stm do_stm(tree.SEQ s) { 
	return seq(do_stm(s.left), do_stm(s.right));
 }

 static tree.Stm do_stm(tree.MOVE s) { 
	if (s.dst instanceof tree.TEMP 
	     && s.src instanceof tree.CALL) 
		return reorder_stm(new MoveCall((tree.TEMP)s.dst,
						(tree.CALL)s.src));
	else if (s.dst instanceof tree.ESEQ)
	    return do_stm(new tree.SEQ(((tree.ESEQ)s.dst).stm,
					new tree.MOVE(((tree.ESEQ)s.dst).exp,
						  s.src)));
	else return reorder_stm(s);
 }

 static tree.Stm do_stm(tree.EXPR s) { 
	if (s.exp instanceof tree.CALL)
	       return reorder_stm(new ExpCall((tree.CALL)s.exp));
	else return reorder_stm(s);
 }

 static tree.Stm do_stm(tree.Stm s) {
     if (s instanceof tree.SEQ) return do_stm((tree.SEQ)s);
     else if (s instanceof tree.MOVE) return do_stm((tree.MOVE)s);
     else if (s instanceof tree.EXPR) return do_stm((tree.EXPR)s);
     else return reorder_stm(s);
 }

 static tree.Stm reorder_stm(tree.Stm s) {
     StmExpList x = reorder(s.kids());
     return seq(x.stm, s.build(x.exps));
 }

 static tree.ESEQ do_exp(tree.ESEQ e) {
      tree.Stm stms = do_stm(e.stm);
      tree.ESEQ b = do_exp(e.exp);
      return new tree.ESEQ(seq(stms,b.stm), b.exp);
  }

 static tree.ESEQ do_exp (tree.EXP e) {
       if (e instanceof tree.ESEQ) return do_exp((tree.ESEQ)e);
       else return reorder_exp(e);
 }
         
 static tree.ESEQ reorder_exp (tree.EXP e) {
     StmExpList x = reorder(e.kids());
     return new tree.ESEQ(x.stm, e.build(x.exps));
 }

 static StmExpList nopNull = new StmExpList(new tree.EXPR(new tree.CONST(0)),null);

//  static StmExpList reorder(tree.ExpList exps) {
//      if (exps==null) return nopNull;
//      else {
//        tree.EXP a = exps.head;
//        if (a instanceof tree.CALL) {
//          temp.Temp t = new temp.Temp();
// 	 tree.EXP e = new tree.ESEQ(new tree.MOVE(new tree.TEMP(t), a),
// 				    new tree.TEMP(t));
//          return reorder(new tree.ExpList(e, exps.tail));
//        } else {
// 	 tree.ESEQ aa = do_exp(a);
// 	 StmExpList bb = reorder(exps.tail);
// 	 if (commute(bb.stm, aa.exp))
// 	      return new StmExpList(seq(aa.stm,bb.stm), 
// 				    new tree.ExpList(aa.exp,bb.exps));
// 	 else {
// 	   temp.Temp t = new temp.Temp();
// 	   return new StmExpList(
// 			  seq(aa.stm, 
// 			    seq(new tree.MOVE(new tree.TEMP(t),aa.exp),
// 				 bb.stm)),
// 			  new tree.ExpList(new tree.TEMP(t), bb.exps));
// 	 }
//        }
//      }
//  }

  static StmExpList reorder(List<tree.EXP> exps) 
  {
    if (exps.isEmpty())
    {
      return nopNull;
    }
    else
    {
      tree.EXP a = exps.remove(0);
      if (a instanceof tree.CALL) 
      {
        temp.Temp t = new temp.Temp();
        tree.EXP e = new tree.ESEQ(new tree.MOVE(new tree.TEMP(t), a), new tree.TEMP(t));
        exps.add(0, e);
        return reorder(exps);
      } 
      else 
      {
        tree.ESEQ aa = do_exp(a);
        StmExpList bb = reorder(exps);
        if (commute(bb.stm, aa.exp))
        {
            List<EXP> l;
            l = bb.exps;
            if(l==null)
            {
              l = new ArrayList<tree.EXP>();
            }
            l.add(0, aa.exp);
            return new StmExpList(seq(aa.stm,bb.stm), l);
        }
        else 
        {
            temp.Temp t = new temp.Temp();
            List<EXP> l = bb.exps;
            l.add(0, new tree.TEMP(t));
            return new StmExpList(seq(aa.stm, seq(new tree.MOVE(new tree.TEMP(t),aa.exp),bb.stm)),l);
        }
      }
    }
  }
        
 static tree.StmList linear(tree.SEQ s, tree.StmList l) {
      return linear(s.left,linear(s.right,l));
 }
 static tree.StmList linear(tree.Stm s, tree.StmList l) {
    if (s instanceof tree.SEQ) return linear((tree.SEQ)s, l);
    else return new tree.StmList(s,l);
 }

 static public tree.StmList linearize(tree.Stm s) {
    return linear(do_stm(s), null);
 }
}
