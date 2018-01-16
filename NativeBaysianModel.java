import java.util.*;
import java.io.*;

/**
 * this class make a NB Model which has the probability attributes
 *
 */

public class NativeBaysianModel {
	double[] prior;
	HashMap<String, HashMap<String, Double>> classToWordToProb;
	HashSet<String> wordBag;
	
	public NativeBaysianModel(double[] prior, HashMap<String, HashMap<String, Double>> classToWordToProb,
			HashSet<String> wordBag) {
		this.prior = prior;
		this.classToWordToProb = classToWordToProb;
		this.wordBag = wordBag;
	}

	public double[] getPrior() {
		return prior;
	}
	public void setPrior(double[] prior) {
		this.prior = prior;
	}
	public HashSet<String> getWordBag() {
		return wordBag;
	}
	public void setWordBag(HashSet<String> wordBag) {
		this.wordBag = wordBag;
	}
	public HashMap<String, HashMap<String, Double>> getClassToWordToProb() {
		return classToWordToProb;
	}
	public void setClassToWordToProb(HashMap<String, HashMap<String, Double>> classToWordToProb) {
		this.classToWordToProb = classToWordToProb;
	}
}
