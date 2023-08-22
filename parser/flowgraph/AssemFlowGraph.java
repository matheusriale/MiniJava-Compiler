package flowgraph;

import graph.*;
import assem.*;
import temp.*;

import java.util.*;

public class AssemFlowGraph extends FlowGraph {

    //public abstract boolean isMove(Node node);


    public Hashtable<assem.Instr, graph.Node> map;
    public Hashtable<graph.Node, assem.Instr> rmap;
    

    public AssemFlowGraph (assem.InstrList instrl)
    {
        map  = new Hashtable<assem.Instr, graph.Node>();
        rmap = new Hashtable<graph.Node, assem.Instr>();
        graphGen(instrl);
    }

    public void graphGen(assem.InstrList instrl)
    {//(cabeca, cauda(cabeca, null) )
     //cauda(cabeca, null)   

        HashMap<temp.Label, assem.Instr> labelmap = new HashMap<temp.Label, assem.Instr>();
        /*
            (Inst1, (Inst2, (Inst3, (Inst4, null))))
            inst <- Inst1 / (Inst2, (Inst3, (Inst4, null)))
            inst <- Inst2 / (Inst3, (Inst4, null))
            inst <- Inst3 / (Inst4, null)
            inst <- Inst4 / null
        */
        for(assem.InstrList instr = instrl; instr != null; instr = instr.tail)
        {
            Instr inst = instr.head;
            graph.Node n = this.newNode();
            map.put(inst, n);
            rmap.put(n, inst);
            if(inst instanceof assem.LABEL)
            {
                labelmap.put(((assem.LABEL)inst).label, inst);
            }
        }
        
        for(assem.InstrList instr = instrl; instr != null; instr = instr.tail)
        {
            Instr inst = instr.head;
            if(instr.tail != null)
            {
                this.addEdge(map.get(inst), map.get((instr.tail).head));
            }

            if(inst.jumps() != null)
            {
                LabelList labels = inst.jumps().labels;
                this.addEdge(map.get(inst), map.get(labelmap.get(labels.head)));
                
                while(labels.tail != null)
                {
                    labels = labels.tail;
                    this.addEdge(map.get(inst), map.get(labelmap.get(labels.head)));
                }
            }
        }
    }


    public TempList def(Node node)
    {
        return rmap.get(node).def();
    }

    public TempList use(Node node)
    {
        return rmap.get(node).use();
    }
}