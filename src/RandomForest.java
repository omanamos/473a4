import java.util.ArrayList;
import java.util.List;


public class RandomForest implements Classifier {
	
	private List<DecisionTree> trees;
	
	/**
	 * @param numTrees number of trees to predict off of, should be an odd number
	 * @param depthLimit depth limit each tree should have
	 * @throws IllegalArgumentException if numTrees is not odd, it must be odd, so there aren't any ties.
	 */
	public RandomForest(int numTrees, int depthLimit){
		if(numTrees % 2 == 0)
			throw new IllegalArgumentException("numTrees must be odd so there aren't any ties.");
		this.trees = new ArrayList<DecisionTree>();
		for(int i = 0; i < numTrees; i++)
			this.trees.add(new DecisionTree(DecisionTree.SplittingRule.RANDOM, depthLimit));
	}
	
	/**
	 * @param examples list of examples to build the forest off of.
	 */
	public void train(List<Example> examples) {
		for(DecisionTree t : this.trees)
			t.train(examples);
	}
	
	/**
	 * @param example example to classify/label
	 * @return classification/label of the given example guess based off of this RandomForest
	 */
	public boolean predict(boolean[] example) {
		int trueCount = 0;
		int falseCount = 0;
		for(DecisionTree t : this.trees){
			if(t.predict(example))
				trueCount++;
			else
				falseCount++;
		}
		return trueCount > falseCount;
	}
}
