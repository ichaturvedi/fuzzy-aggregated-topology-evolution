import java.util.Random;

public class Chromosome {
	public double[] factorialCosts;
	public int[] factorialRanks;
	public double scalarFitness;
	public int skillFactor;
	public DecisionTree tree;
	public Pool treePool;
	
	public Chromosome(int numOfTasks, Pool pool) {
		this.factorialCosts = new double[numOfTasks];
		this.factorialRanks = new int[numOfTasks];
		this.treePool = pool;
	}
	
	public void randomTree(int dimension, int numOfClass, int depth) {
		this.tree = DecisionTree.randomGrow(dimension, numOfClass, depth, this.treePool);
	}
	
	public static Chromosome[] crossover(Chromosome parent1, Chromosome parent2) {
		Random rand = new Random();
		Chromosome[] children = new Chromosome[2];
		children[0] = parent1.clone();
		children[1] = parent2.clone();
		
		DecisionTree.randomSwapSubtree(children[0].tree, children[1].tree);
		children[0].skillFactor = rand.nextBoolean() == true ? parent1.skillFactor : parent2.skillFactor;
		children[1].skillFactor = rand.nextBoolean() == true ? parent1.skillFactor : parent2.skillFactor;
		
		return children;
	}
	
	public Chromosome clone() {
		Chromosome chr = new Chromosome(this.factorialCosts.length, this.treePool);
		chr.tree = DecisionTree.clone(this.tree, this.treePool);
		chr.skillFactor = this.skillFactor;
		return chr;
	}
	
	//public double evaluate(Task task) {
		/*
		double objective = task.getAccuracy(this.tree, Task.TRAIN);
		
		// Over depth penalty
		if(this.tree.depth > (maxDepth + 3)) {
			objective = 0;
		}
		else if(this.tree.depth > maxDepth) {
			objective -= (this.tree.depth - maxDepth) * 0.002;
		}
		
		return objective;
		*/
	//}
}























