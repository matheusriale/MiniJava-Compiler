package tree;
import java.util.List;
abstract public class EXP {
	abstract public List<EXP> kids();
	abstract public EXP build(List<EXP> kids);
}
