import java.util.ArrayList;

public class Task {
	public Dataset dataset;
	public int dimension;
	public int numOfClass;
	public int maxDepth;
	public int[] testingInstances;
	public int[] trainingInstances;
	public int[] featureMapping;
	public ArrayList<Record> records;
	
	public Task(Dataset ds, int[] train, int[] test, int[] fm, int maxDepth) {
		this.dataset = ds;
		this.numOfClass = ds.numOfClass;
		this.dimension = fm.length;
		this.testingInstances = test;
		this.trainingInstances = train;
		this.featureMapping = fm;		
		this.maxDepth = maxDepth;
	}
	
	public double evaluate(Chromosome chromosome) {
		if(chromosome.tree.depth > maxDepth) {
			return 0;
		}
		else {
			return this.getAccuracy(chromosome.tree, this.trainingInstances);
		}
	}
	
	public double trainAccuracy(Chromosome chromosome){
		return this.getAccuracy(chromosome.tree, this.trainingInstances);
	}
	
	public double testAccuracy(Chromosome chromosome){
		return this.getAccuracy(chromosome.tree, this.testingInstances);
	}
	
	private double getAccuracy(DecisionTree tree, int[] instances) {
		double correct=0, wrong=0;
		
		for(int i=0; i<instances.length; i++) {
			Instance ins = dataset.instance.get(instances[i]);
			int prediction = tree.getDecision(ins.features, this.featureMapping);
			if(prediction == ins.target) {
				correct++;
			}
			else {
				wrong++;
			}
		}
		
		return correct / (correct+wrong);
	}
	
}
