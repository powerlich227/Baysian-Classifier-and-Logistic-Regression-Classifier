import java.util.*;
import java.io.*;

/**
 * This class contains the Stop Word Dictionary, which is used to increase the accuracy of the classifier. 
 * also contains a hashMap, which map the class to the word to number of word
 * @author Jinglin Li (jxl163530)
 *
 */

public class WordProcess {
	
	public HashMap<String, HashMap<String, Integer>> classToWordToCount = new HashMap<>();
	
	public HashSet<String> stopWordDic;
	
	public WordProcess() {
		stopWordDic = new HashSet<>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("stopWords.txt"));
			scanner.useDelimiter("\n");
			while (scanner.hasNext()) {
				String word = scanner.next();
				stopWordDic.add(word);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		scanner.close();
	}
	
	
	public HashSet<String> getStopWordDic() {
		return stopWordDic;
	}
	
	/**
	 * get the number of ham files
	 * @param fileName
	 * @return
	 */
	
	public int getCountHam(String fileName) {
		int countHam = 0;
		File[] files = new File(fileName).listFiles();
		for (File f : files) {
			if (f.getName().equals("ham")) {
				countHam = f.listFiles().length;
			}
		}
		return countHam;
	}
	
	/**
	 * get the number of spam files
	 * @param fileName
	 * @return
	 */
	
	public int getCountSpam(String fileName) {
		int countSpam = 0;
		File[] files = new File(fileName).listFiles();
		for (File f : files) {
			if (f.getName().equals("spam")) {
				countSpam = f.listFiles().length;
			}
		}
		return countSpam;
	}
	
	/**
	 * get the hashMap, which map word to its count
	 * @param file
	 * @param stopWordFlag
	 * @return
	 */
	
	public HashMap<String, Integer> getWordToCount(File file, boolean stopWordFlag) {
		int totalWordCount = 0;
		HashMap<String, Integer> wordToCount = new HashMap<>();
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isFile()) {
					Scanner scanner = null;
					try {
						scanner = new Scanner(f);
						scanner.useDelimiter("[^a-zA-Z']+");
						while (scanner.hasNext()) {
							String word = scanner.next();
							if (stopWordFlag && !stopWordDic.contains(word) || !stopWordFlag) {
								totalWordCount++;
								if (!wordToCount.containsKey(word)) {
									wordToCount.put(word, 1);
								}
								else {
									wordToCount.put(word, wordToCount.get(word) + 1);
								}
							}
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					scanner.close();
				}
			}
		}
		else {
			if (file.isFile()) {
				Scanner scanner = null;
				try {
					scanner = new Scanner(file);
					scanner.useDelimiter("[^a-zA-Z']+");
					while (scanner.hasNext()) {
						String word = scanner.next();
						if (stopWordFlag && !stopWordDic.contains(word) || !stopWordFlag) {
							totalWordCount++;
							if (!wordToCount.containsKey(word)) {
								wordToCount.put(word, 1);
							}
							else {
								wordToCount.put(word, wordToCount.get(word) + 1);
							}
						}
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				scanner.close();
			}
		}
		wordToCount.put("totalWordCount", totalWordCount);
		return wordToCount;
	}
	
	/**
	 * get the word bag
	 * @param fileName
	 * @param stopWordFlag
	 * @return
	 */
	
	public HashSet<String> getWordBag(String fileName, boolean stopWordFlag) {
		HashSet<String> wordBag = new HashSet<>();
		
		HashMap<String, Integer> wordToCount = null;
		
		for (File f : new File(fileName).listFiles()) {
			wordToCount = getWordToCount(f, stopWordFlag);
			classToWordToCount.put(f.getName(), wordToCount);
		}
		
		for (String s : classToWordToCount.keySet()) {
			Set<String> hs = classToWordToCount.get(s).keySet();
			wordBag.addAll(hs);
		}
		return wordBag;
	}
	
	/**
	 * get the word bag in each file
	 * @param file
	 * @return
	 */
	
	public HashSet<String> getWordDicInFile(File file) {
		HashSet<String> wordDicInFile = new HashSet<>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
			scanner.useDelimiter("[^a-zA-Z']+");
			while (scanner.hasNext()) {
				String word = scanner.next();
				if (!stopWordDic.contains(word)) {
					wordDicInFile.add(word);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
		return wordDicInFile;
	}
}
