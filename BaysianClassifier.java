import java.util.*;
import java.io.*;

/**
 * this class implement the Native Baysian, classify the test files and calculate the accuracy
 *
 */

public class BaysianClassifier {
	
	/**
	 * get the prior probability of this model
	 * @return
	 */
	
	public double[] getPrior() {
		double[] prior = {0.0, 0.0};
		WordProcess wp = new WordProcess();
		int countHam = wp.getCountHam("train");
		int countSpam = wp.getCountSpam("train");
		prior[0] = (double) countHam / (countHam + countSpam);
		prior[1] = (double) countSpam / (countHam + countSpam);
		return prior; 
	}
	
	/**
	 * train the NB, store the word bag and wor to probability
	 * @param fileName
	 * @param classification
	 * @param stopWordFlag
	 * @return
	 */
	
	public NativeBaysianModel train(String fileName, String[] classification, boolean stopWordFlag) {
		WordProcess wp = new WordProcess();
		HashSet<String> wordBag = wp.getWordBag(fileName, stopWordFlag);
		HashMap<String, HashMap<String, Double>> classToWordToProb = new HashMap<>();

		double[] prior = getPrior();
		for (String c : classification) {
			
			HashMap<String, Double> wordToProb = new HashMap<>();
			
			int size = wp.classToWordToCount.get(c).get("totalWordCount") + wordBag.size();
			wordToProb.put("dummy", (double) 1 / size);
			
			for (String word : wordBag) {
				Integer wordCount = wp.classToWordToCount.get(c).get(word);
				if (wordCount == null) {
					wordCount = 0;
				}
				double conditionalProb = (double) (wordCount + 1) / size;
				wordToProb.put(word, conditionalProb);
			}
			classToWordToProb.put(c, wordToProb);
		}
		NativeBaysianModel model = new NativeBaysianModel(prior, classToWordToProb, wordBag);
		return model;
	}
	
	/**
	 * Classify the test files, label them with "ham" or "spam"
	 * @param model
	 * @param classification
	 * @param file
	 * @return
	 */
	
	public String classify(NativeBaysianModel model, String[] classification, File file) {
		double[] scores = new double[2];
		HashSet<String> wordDicInFile = new WordProcess().getWordDicInFile(file);
		
		double score = 0.0;
		for (String c : classification) {
			if (c.equals("ham")) {
				score = Math.log(model.getPrior()[0]);
			}
			else {
				score = Math.log(model.getPrior()[1]);
			}
			for (String word : wordDicInFile) {
				if (model.getClassToWordToProb().get(c).containsKey(word)) {
					score += Math.log(model.getClassToWordToProb().get(c).get(word));
				}
				else {
					score += Math.log(model.getClassToWordToProb().get(c).get("dummy"));
				}
			}
			if (c.equals("ham")) { 
				scores[0] = score;
			}
			else {
				scores[1] = score;
			}
		}
		if (scores[0] > scores[1]) {
			return "ham";
		}
		else {
			return "spam";
		}
	}
	
	/**
	 * based on the classification, calculate the accuracy of the test files
	 * @param stopWordFlag
	 * @return
	 */
	
	public double getAccuracy(boolean stopWordFlag) {
		String[] classification = {"ham", "spam"};
		NativeBaysianModel model = new BaysianClassifier().train("train", classification, stopWordFlag);
		int correct = 0;
		int total = 0;
		File[] files = new File("test").listFiles();
		for (File f : files) {
			for (File f2 : f.listFiles()) {
				String result = new BaysianClassifier().classify(model, classification, f2);
				if (result.equals(f.getName())) {
					correct++;
				}
				total++;
			}
		}
		return (double) correct / total;
	}
	
	public static void main(String[] args) {
		BaysianClassifier test = new BaysianClassifier();
		System.out.println("Test accuracy by Baysian with no stop word removal: " + test.getAccuracy(false));
		System.out.println("Test accuracy by Baysian with stop word removal: " + test.getAccuracy(true));
	}
}
