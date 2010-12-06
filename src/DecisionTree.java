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
	
	private Node buildTree(Map<Boolean, List<Example>> examples, List<Integer> attributes, Map<Boolean, List<Example>> parents, int depth){
		
		if(examples.get(true).size() == 0 && examples.get(false).size() == 0){
			return classify(parents);
		}else if(examples.get(true).size() == 0){
			return new LeafNode(false);
		}else if(examples.get(false).size() == 0){
			return new LeafNode(true);
		}else if(attributes.size() == 0 || this.depthLimit == depth){
			return classify(examples);
		}else{
			int attrInd = getMostImportant(examples, attributes);
			int attr = attributes.get(attrInd);
			attributes.remove(attrInd);
			
			List<Map<Boolean, List<Example>>> tmp = divideExamples(examples, attr);
			Map<Boolean, List<Example>> negExamples = tmp.get(0);
			Map<Boolean, List<Example>> posExamples = tmp.get(1);
			
			return new DecisionNode(attr, 
					buildTree(negExamples, attributes, examples, depth + 1), //negative subtree
					buildTree(posExamples, attributes, examples, depth + 1));//positive subtree
		}
	}
	
	private static List<Map<Boolean, List<Example>>> divideExamples(Map<Boolean, List<Example>> base, int attr){
		
		//Assumes that the attribute is boolean (only two values)
		Map<Boolean, List<Example>> posTree = new HashMap<Boolean, List<Example>>();
		posTree.put(true, new ArrayList<Example>());
		posTree.put(false, new ArrayList<Example>());
		
		Map<Boolean, List<Example>> negTree = new HashMap<Boolean, List<Example>>();
		negTree.put(true, new ArrayList<Example>());
		negTree.put(false, new ArrayList<Example>());
		
		for(Example e : base.get(true)){
			if(e.getData()[attr]){
				posTree.get(true).add(e);
			}else{
				negTree.get(true).add(e);
			}
		}
		
		for(Example e : base.get(false)){
			if(e.getData()[attr]){
				posTree.get(false).add(e);
			}else{
				negTree.get(false).add(e);
			}
		}
		
		List<Map<Boolean, List<Example>>> rtn = new ArrayList<Map<Boolean, List<Example>>>();
		rtn.add(negTree);
		rtn.add(posTree);
		return rtn;
	}
	
	/**
	 * @param examples current list of examples
	 * @param attributes current list of attributes
	 * @return index of the most selected attribute to split on. Chosen based off of the global SplittingRule, rule.
	 * @throws IllegalArgumentException if attributes.size() == 0
	 */
	private int getMostImportant(Map<Boolean, List<Example>> examples, List<Integer> attributes){
		if(attributes.size() == 0){
			throw new IllegalArgumentException("List of attributes is empty.");
		}else if(this.rule == SplittingRule.RANDOM)
			return (int)(Math.random() * attributes.size());
		else{
			int maxAttrInd = 0;
			double maxGain = calculateGain(examples, attributes.get(0));
			
			for(int i = 1; i < attributes.size(); i++){
				double curGain = calculateGain(examples, attributes.get(i));
				if(curGain >= maxGain){
					maxGain = curGain;
					maxAttrInd = i;
				}
			}
			return maxAttrInd;
		}
	}
	
	/**
	 * @param examples current list of examples
	 * @param attr attribute to calculate information gain of
	 * @return InfoGain of splitting on the given attr
	 */
	private double calculateGain(Map<Boolean, List<Example>> examples, Integer attr){
		int pkPos = 0;
		int nkPos = 0;
		for(Example e : examples.get(true)){
			if(e.getData()[attr])
				pkPos++;
			else
				nkPos++;
		}
		
		int pkNeg = 0;
		int nkNeg = 0;
		for(Example e : examples.get(false)){
			if(e.getData()[attr])
				pkNeg++;
			else
				nkNeg++;
		}
		
		int exSize = examples.get(false).size() + examples.get(true).size();
		double posEntropy = calculateEntropy((double)pkPos / (pkPos + nkPos));
		double negEntropy = calculateEntropy((double)pkNeg / (pkNeg + nkNeg));
		double rtn = 1.0 - (((pkPos + nkPos) / exSize) * posEntropy) + (((pkNeg + nkNeg) / exSize) * negEntropy);
		return rtn;
	}
	
	private double calculateEntropy(double q){
		return -(q * (Math.log10(q) / Math.log10(2)) + (1 - q) * (Math.log10(1 - q) / Math.log10(2)));
	}
	
	private LeafNode classify(Map<Boolean, List<Example>> examples){
		int posCount = examples.get(true).size();
		int negCount = examples.get(false).size();
		return new LeafNode((posCount == negCount) ? Math.random() > .5 : posCount > negCount);
	}
	
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
