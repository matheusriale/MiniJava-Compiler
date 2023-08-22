import java.util.*;
import syntaxtree.*;
import visitor.*;
import table.*;
import frame.*;
import mips.*;
import translate.*;
import tree.*;
import temp.*;
import canon.*;
import assem.*;
import graph.*;
import flowgraph.*;
import regalloc.*;

public class Main {
   public static void main(String [] args) {
      try {
         Program root = new MiniJavaParser(System.in).Goal();
         SymbolTableVisitor stv = new SymbolTableVisitor();
         root.accept(stv);
         TypeCheckVisitor tcv = new TypeCheckVisitor(stv);
         root.accept(tcv);      
         MipsFrame frame = new MipsFrame();    
         IRTranslatorVisitor irtv = new IRTranslatorVisitor(frame, tcv);
         root.accept(irtv);
         //tree.Print printer = new tree.Print(System.out);
         //for(Iterator<FormalArg> it = this.currentMethod.formals.iterator(); it.hasNext();)
         // for(Iterator<Frag> it = irtv.frags.iterator();it.hasNext();){
         
         // }
         // Iterator<Frag> it = irtv.frags.iterator();
         
         // if (it.hasNext()){
         //    Frag next_frag = it.getNext();
         // }

         Frag next_frag = irtv.frags.getNext();
         tree.Print prt = new tree.Print(System.out);

         List<TraceSchedule> trace_list = new ArrayList<TraceSchedule>();

         while(next_frag.hasNext())
         {
            Stm body = ((ProcFrag)next_frag).body;
            TraceSchedule trace_schedule = new TraceSchedule(new BasicBlocks(Canon.linearize(body)));
            trace_list.add(trace_schedule);
            // StmList stml = trace_schedule.stms;

            // //prt.prStm(stml.head);
            // while(stml.tail != null)
            // {
            //    stml = stml.tail;
            //    //prt.prStm(stml.head);
            // }
            next_frag = next_frag.getNext();
         }

         Stm body = ((ProcFrag)next_frag).body;
         TraceSchedule trace_schedule = new TraceSchedule(new BasicBlocks(Canon.linearize(body)));
         trace_list.add(trace_schedule);
         // StmList stml = trace_schedule.stms;
         // prt.prStm(stml.head);
         // while(stml.tail != null)
         // {
         //    stml = stml.tail;
         //    prt.prStm(stml.head);
         // }

         //for(Iterator<TraceSchedule> it = trace_list.iterator(); it.hasNext();)
         // for(TraceSchedule ts : trace_list)
         // {
         //    InstrList instrList = frame.codegen(ts.stms);
         //    //while(instrList != null)
         //    for(InstrList il = instrList; il != null; il = il.tail)
         //    {
         //       if(il.head != null)
         //       {
         //          System.out.println(il.head.Assem);
         //       }
         //    }
         // }

         Iterator<TraceSchedule> itO = trace_list.iterator();
         InstrList instrList = frame.codegen(itO.next().stms);

         InstrList instL = new InstrList(instrList.head, null);
         for(InstrList i = instrList.tail; i.tail != null; i=i.tail)
         {
            instL = new InstrList(i.head, instL);
         }

         for(Iterator<TraceSchedule> it = itO; it.hasNext();)
         {
            instrList = frame.codegen(it.next().stms);
            for(InstrList i = instrList; i.tail != null; i=i.tail)
            {
               instL = new InstrList(i.head, instL);
            }
         }

         AssemFlowGraph flowgraph = new AssemFlowGraph(instL);

         //flowgraph.show(System.out);

         Liveness livenessGraph = new Liveness(flowgraph);
         livenessGraph.show(System.out);

      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}