package tree;
import java.util.*;
abstract public class Stm {
	abstract public List<EXP> kids();
	abstract public Stm build(List<EXP> kids);
}

