package regalloc;

import graph.*;

public class MoveList {
   public graph.Node src, dst;
   public MoveList tail;
   public MoveList(graph.Node s, graph.Node d, MoveList t) {
	src=s; dst=d; tail=t;
   }
}

