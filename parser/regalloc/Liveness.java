package regalloc;

import temp.*;
import graph.*;
import flowgraph.*;
import java.util.*;

public class Liveness extends InterferenceGraph
{
    HashMap<graph.Node, temp.TempList> livemap;
    flowgraph.AssemFlowGraph flowgraph;
    HashMap<temp.Temp, graph.Node> tnode;
        
    public Liveness(flowgraph.AssemFlowGraph flowgraph)
    {
        this.flowgraph = flowgraph;
        computeLiveOut();
        tnode = new HashMap<temp.Temp, graph.Node>();
        for(NodeList nodes = flowgraph.nodes(); nodes.tail != null; nodes = nodes.tail)
        {
            TempList def = flowgraph.def(nodes.head);
            for(TempList d = def; d != null; d=d.tail)
            {
                if(!tnode.containsKey(d.head))
                {
                    Node node = this.newNode();
                    tnode.put(d.head, node);
                }
            }

            TempList liveOut = livemap.get(nodes.head);
            for(TempList lo = liveOut; lo != null; lo=lo.tail)
            {
                if(!tnode.containsKey(lo.head))
                {
                    Node node = this.newNode();
                    tnode.put(lo.head, node);
                }
            }
        }


        for(NodeList nodes = flowgraph.nodes(); nodes != null; nodes = nodes.tail)
        {
            TempList def = flowgraph.def(nodes.head);
            TempList live_out = livemap.get(nodes.head);

            for(TempList d = def; d != null; d=d.tail)
            {
                for(TempList lo = live_out; lo != null; lo = lo.tail)
                {
                    this.addEdge(tnode.get(d.head), tnode.get(lo.head));
                }
            }
        }
    }

    void computeLiveOut()
    {
        HashMap<graph.Node, BitSet> useMap = new HashMap<graph.Node, BitSet>();
        HashMap<graph.Node, BitSet> defMap = new HashMap<graph.Node, BitSet>();

        HashMap<graph.Node, BitSet> in  = new HashMap<graph.Node, BitSet>();
        HashMap<graph.Node, BitSet> out = new HashMap<graph.Node, BitSet>();

        HashMap<Integer, temp.Temp> numTemp = new HashMap<Integer, temp.Temp>();
        NodeList flowNodes = flowgraph.nodes();

        for(NodeList fn = flowNodes; fn != null; fn = fn.tail) // Para cada nó do grafo de fluxo
        {
            Node nodeIt = fn.head;

            in.put(nodeIt, new BitSet());
            out.put(nodeIt, new BitSet());

            TempList def = flowgraph.def(nodeIt); // Busca o def[n]
            TempList use = flowgraph.use(nodeIt); // Busca o use[n]

            BitSet defBS = new BitSet();
            for(TempList d = def; d != null; d = d.tail) // Para cada Temp em def[n]
            {
                defBS.set(d.head.num); // Coloca o bit na posição correspondente Temp como true
                numTemp.put(d.head.num, d.head);
            }
            defMap.put(nodeIt, defBS); // Inicializa o BitSet correspondente ao def[n]

            useMap.put(nodeIt, new BitSet()); // Inicializa o BitSet correspondente ao use[n]
            for(TempList u = use; u != null; u = u.tail) // Para cada Temp em use[n]
            {
                if(u.head != null)
                {    
                    useMap.get(nodeIt).set(u.head.num); // Coloca o bit na posição correspondente Temp como true
                    numTemp.put(u.head.num, u.head);
                }
            }
        }

        boolean allEqual = false;
        while(!allEqual)
        {
            allEqual = true;

            for(NodeList nl = flowNodes; nl != null; nl = nl.tail) // Para cada nó
            {
                Node node = nl.head; // Pega o nó

                BitSet in_prime  = (BitSet)in.get(node).clone();  // in'[n] <- in[n]
                BitSet out_prime = (BitSet)out.get(node).clone(); // out'[n] <- out[n] 

                BitSet new_in = new BitSet(); // Inicializa novo in[n]
                new_in.or(useMap.get(node)); // in[n] <- use[n]
                BitSet outDifDef = out.get(node);
                outDifDef.andNot(defMap.get(node)); // out[n] - def[n]
                new_in.or(outDifDef); // in[n] <- use[n] U (out[n] - def[n])
                in.replace(node, new_in); // Armazena no map

                BitSet new_out = new BitSet(); // Inicializa o novo out[n]

                for(NodeList succsNode = node.succ(); succsNode != null; succsNode = succsNode.tail) // Para cada s e succ[n]
                {
                    new_out.or(in.get(succsNode.head)); // new_out[n] <- new_out[n] U in[s]
                }
                out.replace(node, new_out); // out[n] <- U(s e succ[n]) in[s]

                if(!new_out.equals(out_prime) || !new_in.equals(in_prime)) // Se in'[n] != in[n] OU out'[n] != out[n]
                {
                    allEqual = false;
                }
            }
        }
        livemap = new HashMap<graph.Node, temp.TempList>();
        for(NodeList nl = flowNodes; nl != null; nl = nl.tail)
        {
            Node node = nl.head;

            BitSet liveOut = out.get(node);
            int primeiro = liveOut.nextSetBit(0);
            TempList live_out = new TempList(numTemp.get(primeiro), null);
            
            for(int i = primeiro + 1; i < liveOut.size(); ++i)
            {
                if(liveOut.get(i))
                {
                    temp.Temp t = numTemp.get(i);
                    live_out = new TempList(t, live_out);
                }
            }
            livemap.put(node, live_out);
        }
    }
}