import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A decision tree classifier
 */
public class DecisionTree implements Classifier {
	
	public enum SplittingRule { INFO_GAIN, RANDOM };

	private Node root;
	private SplittingRule rule;
	private int depthLimit;
	
	
	public DecisionTree(SplittingRule rule, int depthLimit) {
		this.rule = rule;
		this.depthLimit = depthLimit;
	}
	
	/**
	 * @param examples list of examples to build the decision tree off of.
	 */
	public void train(List<Example> examples) {
		List<Integer> attributes = new ArrayList<Integer>();
		for(int i = 0; i < examples.get(0).getData().length; i++)
			attributes.add(i);
		
		List<Example> pos = new ArrayList<Example>();
		List<Example> neg = new ArrayList<Example>();
		for(int i = 0; i < examples.size(); i++){
			Example e = examples.get(i);
			if(e.getLabel())
				pos.add(e);
			else
				neg.add(e);
		}
		Map<Boolean, List<Example>> exs = new HashMap<Boolean, List<Example>>();
		exs.put(true, pos);
		exs.put(false, neg);
		
		this.root = buildTree(exs, attributes, new HashMap<Boolean, List<Example>>(), 0);
	}
	
	/**
	 * @param examples list of examples to train on split based on their label (Examples with the label true could be retrieved with 'examples.get(true)')
	 * @param attributes list of attributes to split the training examples on
	 * @param parents list of parent examples before split of current list
	 * @param depth current depth in the decision tree
	 * @return A decision tree based on the given examples. Either a LeafNode(where a classification can be made)
	 * or a DecisionNode(where the predict function can traverse left or right based on the attribute in that DecisionNode).
	 */
	private Node buildTree(Map<Boolean, List<Example>> examples, List<Integer> attributes, Map<Boolean, List<Example>> parents, int depth){
		
		if(examples.get(true).size() == 0 && examples.get(false).size() == 0){
			return Utils.classify(parents);
		}else if(examples.get(true).size() == 0){
			return new LeafNode(false);
		}else if(examples.get(false).size() == 0){
			return new LeafNode(true);
		}else if(attributes.size() == 0 || this.depthLimit == depth){
			return Utils.classify(examples);
		}else{
			int attrInd = Utils.getMostImportant(examples, attributes, this.rule);
			int attr = attributes.get(attrInd);
			attributes.remove(attrInd);
			
			List<Map<Boolean, List<Example>>> tmp = Utils.divideExamples(examples, attr);
			Map<Boolean, List<Example>> negExamples = tmp.get(0);
			Map<Boolean, List<Example>> posExamples = tmp.get(1);
			
			Node left = buildTree(negExamples, attributes, examples, depth + 1);
			attributes.add(attrInd, attr);
			Node right = buildTree(posExamples, attributes, examples, depth + 1);
			return new DecisionNode(attr, left, right);
		}
	}
	
	/**
	 * @param example example to classify/label
	 * @return classification/label of the given example guess based off of this DecisionTree
	 */
	public boolean predict(boolean[] example) {
		Node curRoot = root;
		while(!(curRoot instanceof LeafNode)){
			DecisionNode tmp = (DecisionNode)curRoot;
			if(example[tmp.getAttribute()])
				curRoot = tmp.getRight();
			else
				curRoot = tmp.getLeft();
		}
		return ((LeafNode)curRoot).getClassification();
	}
}
