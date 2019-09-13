
public class Record {
	public double trainingAccuracy;
	public double testingAccuracy;
	// public long time;
	// public double objective;
	public Chromosome bestIndividual;
	
	public Record(double train, double test, Chromosome chr) {
		this.trainingAccuracy = train;
		this.testingAccuracy = test;
		this.bestIndividual = chr;
	}
}
