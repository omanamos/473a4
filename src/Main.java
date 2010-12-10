import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
	/**
	 * Read in a data set in CSV format
	 * @param filename the file to read from
	 * @return the data set
	 */
	public static List<Example> readData(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = null;
			ArrayList<Example> data = new ArrayList<Example>();
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ", ");
				ArrayList<Boolean> entries = new ArrayList<Boolean>();
				if (!st.hasMoreTokens()) {
					continue;
				}
				while (st.hasMoreTokens()) {
					String s =  st.nextToken();
					if (s.equals("1") || s.equals("True") || s.equals("true")) {
						entries.add(true);
					} else if (s.equals("0") || s.equals("False") || s.equals("false")) {
						entries.add(false);
					} else {
						System.out.println("Error: expected true/false 1/0, got " + s);
						return null;
					}
				}
				if (data.size() != 0 && (
					data.get(data.size() - 1).getData().length != entries.size() - 1)) {
					System.out.println("Error: expected " + 
							data.get(data.size() - 1).getData().length + 
							" features got " + (entries.size() - 1));
					return null;
				}
				if (entries.size() <= 1) {
					System.out.println("Error: expected 2 or more entries, got " +
							entries.size());
				}
				boolean label = entries.get(entries.size() - 1);
				boolean[] vector = new boolean[entries.size() - 1];
				for (int i = 0; i < entries.size() - 1; i++) {
					vector[i] = entries.get(i);
				}
				Example example = new Example(vector, label);
				data.add(example);
			}
			br.close();
			return data;
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return null;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}
	
	public static double computeAccuracy(Classifier c, List<Example> testData) {
		int numRight = 0;
		for (int i = 0; i < testData.size(); i++) {
			if (testData.get(i).getLabel() == c.predict(testData.get(i).getData())) {
				numRight++;
			}
		}
		return numRight * 1.0 / testData.size();
	}
	
	public static void main(String[] args) {
		List<Example> train = readData("train.txt");
		List<Example> test = readData("test.txt");
		
		test(train, test);
		testForests(train, test);
	}
	
	private static void test(List<Example> train, List<Example> test){
		System.out.println("Depth,Info Gain Training Accuracy,Info Gain Test Accuracy,Random Training Accuracy,Random Test Accuracy");
		for(int i = 1; i <= 30; i++){
			
			Classifier infoGain = new DecisionTree(DecisionTree.SplittingRule.INFO_GAIN, i);
			infoGain.train(train);
			double infoGainTrain = computeAccuracy(infoGain, train);
			double infoGainTest = computeAccuracy(infoGain, test);
			
			double randomTrain = testRandom(train, train, i);
			double randomTest = testRandom(train , test, i);
			
			System.out.println(i + "," + infoGainTrain + "," + infoGainTest + "," + randomTrain + "," + randomTest);
		}
	}
	
	private static void testForests(List<Example> train, List<Example> test){
		System.out.println("Depth,Info Gain Training Accuracy,Info Gain Test Accuracy,Random Forest Training Accuracy, Random Forest Test Accuracy");
		for(int i = 1; i <= 100; i++){
			
			Classifier infoGain = new DecisionTree(DecisionTree.SplittingRule.INFO_GAIN, i);
			infoGain.train(train);
			double infoGainTrain = computeAccuracy(infoGain, train);
			double infoGainTest = computeAccuracy(infoGain, test);
			
			Classifier randomForest = new RandomForest(101, i);
			randomForest.train(train);
			double randomForestTrain = computeAccuracy(randomForest, train);
			double randomForestTest = computeAccuracy(randomForest, test);
			
			System.out.println(i + "," + infoGainTrain + "," + infoGainTest + "," + randomForestTrain + "," + randomForestTest);
		}
	}
	
	private static double testRandom(List<Example> train, List<Example> test, int maxDepth){
		double rtn = 0;
		for(int i = 0; i < 100; i++){
			Classifier random = new DecisionTree(DecisionTree.SplittingRule.RANDOM, maxDepth);
			random.train(train);
			rtn += computeAccuracy(random, test);
		}
		return rtn / 100.0;
	}
}
