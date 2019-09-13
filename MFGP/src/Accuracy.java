
public class Accuracy {
	int correct;
	int wrong;
	
	public Accuracy() {
		this.correct = 0;
		this.wrong = 0;
	}
	
	public Accuracy(int tp, int tn, int fp, int fn) {
		this.correct = tp;
		this.wrong = tn;
	}
}
