import java.util.*;
import java.io.*;

/**
 * this class train the data and classify the test file by logistic regression classifier
 *
 */
public class LogisticRegression{
	
	/**
	 * get the HashMap, which map the word to its weight assigned. 
	 * @param fileName
	 * @param stopWordFlag
	 * @return
	 */
	
	public HashMap<String, Double> getWordToWeight(String fileName, boolean stopWordFlag) {
		HashMap<String, Double> wordToWeight = new HashMap<>();
		HashSet<String> wordBag = new WordProcess().getWordBag(fileName, stopWordFlag);
		for (String word : wordBag) {
			wordToWeight.put(word, -1 + new Random().nextDouble() * (1 + 1));
		}
		return wordToWeight;
	}
	
	/**
	 * get the HashMap, which map word to count in each file No.
	 * @param file
	 * @param stopWordFlag
	 * @return
	 */
	
	public HashMap<Integer, HashMap<String, Integer>> getFileNoToWordToCount(File file, boolean stopWordFlag) {
		HashMap<Integer, HashMap<String, Integer>> fileNoToWordToCount = new HashMap<>();
		int fileNo = 0;
		for (File f : file.listFiles()) {
			HashMap<String, Integer> wordToCount = new WordProcess().getWordToCount(file, stopWordFlag);
			fileNoToWordToCount.put(fileNo, wordToCount);
			fileNo++;
		}
		return fileNoToWordToCount;		
	}
	
	/**
	 * get the probability using in the steps of other function
	 * @param file
	 * @param wordToWeight
	 * @param wordToCount
	 * @return
	 */
	
	public double getProbability(File file, HashMap<String, Double> wordToWeight, HashMap<String, Integer> wordToCount) {
		double sum = 0.0;
		for (String word : wordToCount.keySet()) {
			sum = 0.1;
			sum += wordToWeight.get(word) * wordToWeight.get(word);
		}
		sum = 1 + Math.exp(sum);
		return 1 / sum;
	}
	
	/**
	 * train the data based on lemda, learning rate and number of iteration
	 * @param fileName
	 * @param stopWordFlag
	 * @param lemda
	 * @param learningRate
	 * @param numberOfIteration
	 * @return
	 */
	
	public HashMap<String, Double> logisticRegressionTrain(String fileName, boolean stopWordFlag, double lemda, double learningRate, int numberOfIteration) {
		HashMap<String, Double> wordToWeight = getWordToWeight(fileName, stopWordFlag);
		
		HashMap<Integer, HashMap<String, Integer>> FileNoToWordToCount = new HashMap<>();
		HashMap<String, HashMap<Integer, Double>> probabilityMap = new HashMap<>();
		
		File[] files = new File(fileName).listFiles();
		HashMap<Integer, HashMap<String, Integer>> fileNoToHamWordToCount = getFileNoToWordToCount(files[0], stopWordFlag);
		HashMap<Integer, HashMap<String, Integer>> fileNoToSpamWordToCount = getFileNoToWordToCount(files[1], stopWordFlag);
		
		for (int i = 0; i <= numberOfIteration; i++) {
			for (File f : files) {
				HashMap<Integer, Double> probTemp = new HashMap<>();
				if (f.getName().equals("ham")) {
					FileNoToWordToCount = fileNoToHamWordToCount;
				}
				else {
					FileNoToWordToCount = fileNoToSpamWordToCount;
				}
				for (int j = 0; j < f.listFiles().length; j++) {
					double prob = getProbability(f, wordToWeight, FileNoToWordToCount.get(j));
					probTemp.put(j, prob);
				}
				probabilityMap.put(f.getName(), probTemp);
			}
			
			for (String word : wordToWeight.keySet()) {
				double result = 0.0;
				double weight = wordToWeight.get(word);
				double y = 1.0;
				for (String s : new String[]{"ham", "spam"}) {
					if (s.equals("ham")) {
						FileNoToWordToCount = fileNoToHamWordToCount;
						y = 0.0;
					}
					else {
						FileNoToWordToCount = fileNoToSpamWordToCount;
						y = 1.0;
					}
					for (Integer fileNo : FileNoToWordToCount.keySet()) {
						Integer totalWord = FileNoToWordToCount.get(fileNo).get(word);
						if (totalWord == null) {
							totalWord = 0;
						}
						result += totalWord * (y - probabilityMap.get(s).get(fileNo));
					}
				}
				weight += learningRate * result - learningRate * lemda * weight;
				wordToWeight.put(word, weight);
			}
		}
		return wordToWeight;
	}

	/**
	 * classify the test files
	 * @param file
	 * @param stopWordFlag
	 * @param wordToWeightLearned
	 * @return
	 */
	
	public String classify(File file, boolean stopWordFlag, HashMap<String, Double> wordToWeightLearned) {
		double weight0 = 0.1;
		double result = 0.0;
		double currWeight = 0.0;
		int wordCount = 0;
		HashMap<String, Integer> wordToCount = new WordProcess().getWordToCount(file, stopWordFlag);
		HashSet<String> wordDicInFile = new WordProcess().getWordDicInFile(file);
		
		for (String word : wordDicInFile) {
			if (wordToWeightLearned.containsKey(word)) {
				currWeight = wordToWeightLearned.get(word);
			}
			if (wordToCount.containsKey(word)) {
				wordCount = wordToCount.get(word);
			}
			result += currWeight * wordCount;
		}
		
		if (weight0 + result < 0)
			return "ham";
		else
			return "spam";
	}
	
	/**
	 * get the accuracy based on the classification above
	 * @param trainSetName
	 * @param testSetName
	 * @param stopWordFlag
	 * @param lemda
	 * @param learningRate
	 * @param numberOfIteration
	 * @return
	 */
	
	public double getAccuracy(String trainSetName, String testSetName, boolean stopWordFlag, double lemda, double learningRate, int numberOfIteration) {
		int correct = 0;
		int total = 0;
		HashMap<String, Double> wordToWeightLearned = logisticRegressionTrain(trainSetName, stopWordFlag, lemda, learningRate, numberOfIteration);
		
		File[] files = new File(testSetName).listFiles();
		for (File f : files) {
			for (File f2 : f.listFiles()) {
				String result = classify(f2, stopWordFlag, wordToWeightLearned);
				if (result.equals(f.getName())) {
					correct++;
				}
				total++;
			}
		}
		return (double) correct / total;
	}
	
	/**
	 * main function
	 * @param args
	 */
	
	public static void main(String[] args) {
		LogisticRegression lr = new LogisticRegression();
	
		// this is the original parameters 
		System.out.println("Accuracy using Logistic Regression: ");
		System.out.println("");
		System.out.println("1. Using parameters 0.01, 0.001, 10 as Lemda, learning rate, number of iteration, respectively: ");
		System.out.println("without stop word removal: " + lr.getAccuracy("train", "test", false, 0.01, 0.001, 10));
		System.out.println("with stop word removal: " + lr.getAccuracy("train", "test", true, 0.01, 0.001, 10));
		
		// increase lemda
		System.out.println("2. Using parameters 0.05, 0.001, 10 as Lemda, learning rate, number of iteration, respectively: ");
		System.out.println("without stop word removal: " + lr.getAccuracy("train", "test", false, 0.05, 0.001, 10));
		System.out.println("with stop word removal: " + lr.getAccuracy("train", "test", true, 0.05, 0.001, 10));
		
		// increase learning rate
		System.out.println("3. Using parameters 0.01, 0.005, 10 as Lemda, learning rate, number of iteration, respectively: ");
		System.out.println("without stop word removal: " + lr.getAccuracy("train", "test", false, 0.01, 0.005, 10));
		System.out.println("with stop word removal: " + lr.getAccuracy("train", "test", true, 0.01, 0.005, 10));

		// decrease number of iteration
		System.out.println("4. Using parameters 0.01, 0.001, 5 as Lemda, learning rate, number of iteration, respectively: ");
		System.out.println("without stop word removal: " + lr.getAccuracy("train", "test", false, 0.01, 0.001, 5));
		System.out.println("with stop word removal: " + lr.getAccuracy("train", "test", true, 0.01, 0.001, 5));
	}
}
