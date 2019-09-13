import java.util.ArrayList;
import java.util.Comparator;

public class GP {
	private Task task;
	private Pool treePool;
	private int generation;
	private int popSize;
	private int maxDepth;
	private int numOfClass;
	private int dimension;
	private ArrayList<Chromosome> population;
	
	public GP(Task task, int gen, int popSize, Pool treePool) {
		this.task = task;
		this.treePool = treePool;
		this.generation = gen;
		this.popSize = popSize;
		this.maxDepth = task.maxDepth;
		this.dimension = task.dimension;
		this.numOfClass = task.numOfClass;
		this.population = new ArrayList<Chromosome>(this.popSize*2);
	}
	
	private void initialize() {
		for(int i=0; i<this.popSize; i++) {
			// new Chromosome(numOfTasks, treePool);
			Chromosome chr = new Chromosome(1, this.treePool);
			chr.randomTree(this.dimension, this.numOfClass, this.maxDepth);
			this.population.add(chr);
		}
	}
	
	private void evaluateAll() {
		for(Chromosome ind : this.population) {
			ind.factorialCosts[0] = task.evaluate(ind);
			ind.scalarFitness = ind.factorialCosts[0];
		}
	}
	
	private void evaluateAll(Chromosome[] chrs) {
		for(Chromosome ind : chrs) {
			ind.factorialCosts[0] = task.evaluate(ind);
			ind.scalarFitness = ind.factorialCosts[0];
		}
	}
	
	private void sortByScalarFitness() {
		this.population.sort(new Comparator<Chromosome>() {
			@Override
			public int compare(Chromosome chr1, Chromosome chr2) {
				return Double.compare(chr2.scalarFitness, chr1.scalarFitness); // Descending
			}
		});
	}

	// (mu + lambda)
	private void survivalElitism() {
		this.sortByScalarFitness();
		while(this.population.size() > this.popSize) {
			Chromosome ind = this.population.remove(this.population.size()-1);
			DecisionTree.releaseTree(ind.tree, this.treePool);
		}
	}
	
	// (mu , lambda)
	/*
	private void survivalReplace() {
		this.population = new ArrayList<Chromosome>(this.population.subList(this.popSize, this.population.size()));
		this.sortByScalarFitness();
	}
	*/
	
	public ArrayList<Record> evolve() {
		ArrayList<Record> records = new ArrayList<Record>(this.generation);
		
		System.out.println("\nInitializing ... ");
		// Initialization
		this.initialize();
		
		System.out.println("Initialization done.");
		
		System.out.println("Evaluating ... ");
		// Evaluation
		this.evaluateAll();
		System.out.println("Evaluation done.");
		
		for(int gen=0; gen<this.generation; gen++) {
			System.out.printf("Generation % 3d  ", gen);
			// Parents selection
			// Crossover
			int[] randPerm = IndexGenerator.randomPermutation(popSize);

			for(int i=0, pivot=(randPerm.length/2); i<pivot; i++) {
				Chromosome parent1 = this.population.get(randPerm[i]);
				Chromosome parent2 = this.population.get(randPerm[i+pivot]);
				Chromosome[] children = Chromosome.crossover(parent1, parent2);
				
				// Evaluation
				this.evaluateAll(children);
				
				// Add offspring to current population
				this.population.add(children[0]);
				this.population.add(children[1]);
			}

			// Pool compression
			// this.treePool.extremeCompress();

			// Survival selection
			this.survivalElitism();
			
			// Recording best individual
			Chromosome bestInd = this.population.get(0);
			double trainAcc = bestInd.factorialCosts[0]; 
			double testAcc = this.task.testAccuracy(bestInd);
			
			System.out.printf("Train = %.3f\tTest = %.3f\n", trainAcc, testAcc);
			
			records.add(new Record(trainAcc, testAcc, bestInd));
		}

		return records;
	}
	
	
}














