package mips;

public class InFrame extends frame.Access {
    int offset;
    InFrame(int o) {
	offset = o;
    }

    public tree.EXP exp(tree.EXP fp) {
        return new tree.MEM
	    (new tree.BINOP(tree.BINOP.PLUS, fp, new tree.CONST(offset)));
    }

    public String toString() {
        Integer offset = new Integer(this.offset);
	return offset.toString();
    }
}
