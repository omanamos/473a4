import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains utility functions for the DecisionTree class.
 * Including entropy calculation, gain calculation, plurality-value of examples,
 * importance function, and dividing examples based on an attribute.
 */
public class Utils {
	
	/**
	 * @param base Examples to divide, which are already split on their label.
	 * @param attr attribute to split on
	 * @return Two new Maps with the same structure as the given base. The first one 
	 * has all examples that have the given attr set to false, the second has all 
	 * exmaples with the given attr set to true.
	 */
	public static List<Map<Boolean, List<Example>>> divideExamples(Map<Boolean, List<Example>> base, int attr){
		
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
	public static int getMostImportant(Map<Boolean, List<Example>> examples, List<Integer> attributes, DecisionTree.SplittingRule rule) throws IllegalArgumentException{
		if(attributes.size() == 0){
			throw new IllegalArgumentException("List of attributes is empty.");
		}else if(rule == DecisionTree.SplittingRule.RANDOM)
			return (int)(Math.random() * attributes.size());
		else{
			int maxAttrInd = 0;
			double maxGain = calculateGain(examples, attributes.get(0));
			
			for(int i = 1; i < attributes.size(); i++){
				double curGain = calculateGain(examples, attributes.get(i));
				if(curGain > maxGain){
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
	private static double calculateGain(Map<Boolean, List<Example>> examples, Integer attr){
		double pkPos = 0.0;
		double nkPos = 0.0;
		double pkNeg = 0.0;
		double nkNeg = 0.0;
		
		for(Example e : examples.get(true)){
			if(e.getData()[attr])
				pkPos++;
			else
				pkNeg++;
		}
		
		for(Example e : examples.get(false)){
			if(e.getData()[attr])
				nkPos++;
			else
				nkNeg++;
		}
		
		double exSize = examples.get(false).size() + examples.get(true).size();
		
		//First term
		double baseEntropy = calculateEntropy(examples.get(true).size() / exSize);
		
		//Remainder term
		double posEntropy = calculateEntropy(pkPos / (pkPos + nkPos));
		double negEntropy = calculateEntropy(pkNeg / (pkNeg + nkNeg));
		
		double rtn = baseEntropy - 
						(((pkPos + nkPos) / exSize) * posEntropy + 
						 ((pkNeg + nkNeg) / exSize) * negEntropy);
		return rtn;
	}
	
	/**
	 * @param q probability to calculate entropy of
	 * @return entropy of given q
	 */
	public static double calculateEntropy(double q){
		if(q == 0.0 || q == 1.0)
			return 0.0;
		return -((q * log(q, 2)) + ((1 - q) * log(1 - q, 2)));
	}
	
	/**
	 * @param q value to take logarithm of
	 * @param base base of logarithm
	 * @return log(base = base) of (q)
	 */
	public static double log(double q, int base){
		return Math.log10(q) / Math.log10(base);
	}
	
	/**
	 * @param examples current list of Examples
	 * @return true if there are more positive examples than negative examples. 
	 * false if there are more negative examples than positive examples.
	 * Randomly breaks ties.
	 */
	public static LeafNode classify(Map<Boolean, List<Example>> examples){
		int posCount = examples.get(true).size();
		int negCount = examples.get(false).size();
		return new LeafNode((posCount == negCount) ? Math.random() > 0.5 : posCount > negCount);
	}
}
