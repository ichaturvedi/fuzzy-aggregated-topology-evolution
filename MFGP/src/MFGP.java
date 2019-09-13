import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class MFGP {
	public Task[] tasks;
	public Pool treePool;
	private final static double randomMatingProbability = 0.3;
	private int maxDimension;
	private int maxNumOfClass;
	private int maxDepth;
	private int generation;
	private int popSize;
	private ArrayList<Chromosome> population;
	private int tmpfc = 0;
	
	public MFGP(Task[] tasks, int gen, int popSize, Pool treePool) {
		this.tasks = tasks;
		this.generation = gen;
		this.popSize = popSize;
		this.population = new ArrayList<Chromosome>(popSize*2);
		this.treePool = treePool;
		
		this.maxDimension = tasks[0].dimension;
		this.maxNumOfClass = tasks[0].numOfClass;
		this.maxDepth = tasks[0].maxDepth;
		
		for(int i=1; i<this.tasks.length; i++) {
			System.out.println(this.tasks[i].dimension);
			if(this.tasks[i].dimension > this.maxDimension) {
				this.maxDimension = this.tasks[i].dimension;
			}
			if(this.tasks[i].numOfClass > this.maxNumOfClass) {
				this.maxNumOfClass = this.tasks[i].numOfClass;
			}
			if(this.tasks[i].maxDepth > this.maxDepth) {
				this.maxDepth = this.tasks[i].maxDepth;
			}
		}
		
		System.out.println(this.maxDimension);
	}
	
	private void initialize() {
		for(int i=0; i<this.popSize; i++) {
			// new Chromosome(numOfTasks, treePool);
			Chromosome chr = new Chromosome(this.tasks.length, this.treePool);
			chr.randomTree(this.maxDimension, this.maxNumOfClass, this.maxDepth);
			this.population.add(chr);
		}
	}
	
	private void evaluateAll() {
		for(Chromosome ind : this.population) {
			for(int i=0; i<this.tasks.length; i++) {
				ind.factorialCosts[i] = this.tasks[i].evaluate(ind);
			}
		}
	}
	
	private void evaluateSkill() {
		for(Chromosome ind : this.population) {
			for(int i=0; i<this.tasks.length; i++) {
				if(i == ind.skillFactor) {
					ind.factorialCosts[i] = this.tasks[i].evaluate(ind);
				}
				else {
					ind.factorialCosts[i] = 0;
				}
			}
		}
	}
	
	private void evaluateSkill(Chromosome[] inds) {
		for(Chromosome ind : inds) {
			for(int i=0; i<this.tasks.length; i++) {
				if(i == ind.skillFactor) {
					ind.factorialCosts[i] = this.tasks[i].evaluate(ind);
				}
				else {
					ind.factorialCosts[i] = 0;
				}
			}
		}
	}
	
	private void getFactorialRanks() {
		for(int i=0; i<this.tasks.length; i++) {
			this.tmpfc = i;
			
			// Sort on each factorial cost
			this.population.sort(new Comparator<Chromosome>() {
				@Override
				public int compare(Chromosome chr1, Chromosome chr2) {
					return Double.compare(chr2.factorialCosts[tmpfc], chr1.factorialCosts[tmpfc]); // Descending
				}
			});
			
			// Assign factorial ranks
			for(int j=0; j<this.population.size(); j++) {
				this.population.get(j).factorialRanks[i] = j+1;
			}
		}
	}
	
	private void getSkillFactorAndScalarFitness() {
		// Sort factorial rank of each individual
		for(Chromosome ind : this.population) {
			int skillFactor=0;
			for(int i=1; i<ind.factorialRanks.length; i++) {
				if(ind.factorialRanks[i] < ind.factorialRanks[skillFactor]) {
					skillFactor = i;
				}
			}
			ind.skillFactor = skillFactor;
			ind.scalarFitness = 1.0 / ind.factorialRanks[skillFactor];
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
	
	public MultiTaskRecords evolve() {
		Random rand = new Random();
		MultiTaskRecords mtRecords = new MultiTaskRecords(this.tasks.length);
		
		System.out.println("\nInitializing ... ");
		
		// Initialization
		this.initialize();
		
		System.out.println("Initialization done.");
		
		System.out.println("Evaluating ... ");
		
		// Evaluation
		this.evaluateAll();
		this.getFactorialRanks();
		this.getSkillFactorAndScalarFitness();
		
		// Evolution
		for(int gen=0; gen<this.generation; gen++) {
			System.out.printf("Generation % 3d  ", gen);
			
			//   assortative mating
			int[] randPerm = IndexGenerator.randomPermutation(popSize);
			for(int i=0, pivot=(randPerm.length/2); i<pivot; i++) {
				Chromosome parent1 = this.population.get(randPerm[i]);
				Chromosome parent2 = this.population.get(randPerm[i+pivot]);
				
				Chromosome[] children;
				if(parent1.skillFactor == parent2.skillFactor) {
					children = Chromosome.crossover(parent1, parent2);
				}
				else if(rand.nextDouble() < this.randomMatingProbability) {
					children = Chromosome.crossover(parent1, parent2);
				}
				else {
					children = new Chromosome[2];
					children[0] = parent1.clone();
					children[1] = parent2.clone();
				}
				
				// Evaluation
				this.evaluateSkill(children);
				
				// Add offspring to current population
				this.population.add(children[0]);
				this.population.add(children[1]);
			}
			
			//   evaluation
			this.getFactorialRanks();
			this.getSkillFactorAndScalarFitness();
			
			//   survival selection
			this.survivalElitism();
			
			// Recording best individual of each task
			for(int tsk=0; tsk<this.tasks.length; tsk++) {
				Chromosome bestInd = this.population.get(0);
				for(Chromosome ind : this.population) {
					if(ind.factorialRanks[tsk] < bestInd.factorialRanks[tsk]) {
						bestInd = ind;
					}
				}
				
				double trainAcc = bestInd.factorialCosts[tsk]; 
				// double testAcc = this.tasks[tsk].testAccuracy(bestInd);
				mtRecords.multiTaskRecords.get(tsk).add(new Record(trainAcc, 0, bestInd));
				System.out.printf("#%dTrain = %.3f   ", tsk, trainAcc);
			}
			System.out.println();
			
			
			/*
			Chromosome bestInd = this.population.get(0);
			double trainAcc = bestInd.factorialCosts[0]; 
			double testAcc = this.task.testAccuracy(bestInd);
			
			System.out.printf("Train = %.3f\tTest = %.3f\n", trainAcc, testAcc);
			
			records.add(new Record(trainAcc, testAcc, bestInd));
			*/
		}

		return mtRecords;	
	}
}

class MultiTaskRecords {
	public ArrayList<ArrayList<Record>> multiTaskRecords;
	public MultiTaskRecords(int numOfTasks) {
		this.multiTaskRecords = new ArrayList<ArrayList<Record>>(numOfTasks);
		for(int i=0; i<numOfTasks; i++) {
			this.multiTaskRecords.add(new ArrayList<Record>());
		}
	}
}



