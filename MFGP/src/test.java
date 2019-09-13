import java.util.ArrayList;

public class test {

	public static void main(String[] args) {
		Dataset ds = new Dataset("res/ACD.csv");
		
		System.out.println(ds.toString());
		//ds.printInstance(0, ds.length);
		
		//int[] featureMapping = {1,2,3,4,5,6,7,8,9,10};
		int[] train = new int[ds.length];
		int[] test = new int[ds.length];
		int[] fm = new int[ds.dimension];
		int maxDepth = 10;
		int popSize = 2000;
		int numOfNode = DecisionTree.maxNumOfNodes(maxDepth)*popSize*2;
		
		System.out.println("Pool size: "+numOfNode);
		
		for(int i=0; i<ds.dimension; i++) {
			fm[i] = i;
		}
		
		for(int i=0; i<2; i++) {
			train[i] = i;
			test[i] = i;
		}
		
		
		Task task = new Task(ds, train, test, fm, maxDepth);
		
		Pool pool = new Pool(numOfNode);		
		for(int i=0; i<numOfNode; i++) {
			pool.pool[i] = new DecisionTree();
		}

		
		
		//DecisionTree tree;
		ArrayList<DecisionTree> pop = new ArrayList<DecisionTree>();
		
		for(int i=0; i<popSize; i++) {
			pop.add(DecisionTree.randomGrow(task.dimension, task.numOfClass, maxDepth, pool));
			//System.out.println(pop.get(i).depth);
		}
		
		
		for(int g=0; g<250; g++) {
			System.out.println("Gen "+g);
			
			
			for(int i=0; i<popSize; i++) {
				pop.add(DecisionTree.clone(pop.get(i), pool));
			}
			//System.out.println("Check 1");
			
			for(int i=0; i<(popSize/2); i++) {
				DecisionTree.randomSwapSubtree(pop.get(i), pop.get(i+popSize));
			}
			//System.out.println("Check 2");
			
			//System.out.println("unused: "+pool.availableSize());
			
			for(int i=0; i<pop.size(); i++) {
				if(pop.get(i).depth > maxDepth) {
					DecisionTree.releaseTree(pop.remove(i), pool);
					pop.add(i, DecisionTree.randomGrow(task.dimension, task.numOfClass, maxDepth, pool));
				}
			}
			//System.out.println("Check 3");
			
			//System.out.println("unused: "+pool.availableSize());
			
			for(int i=0; i<popSize; i++) {
				DecisionTree.releaseTree(pop.remove(0), pool);
			}
			
			//System.out.println("Check 4");
		}
		
		
		/*
		DecisionTree t1, t2, t3;
		
		System.out.println("----------------------------------------------------");
		t1 = DecisionTree.randomGrow(task, maxDepth, pool);
		System.out.println(t1.toPrettyString());
		System.out.println("----------------------------------------------------");
		t2 = DecisionTree.randomGrow(task, maxDepth, pool);
		System.out.println(t2.toPrettyString());
		System.out.println("----------------------------------------------------");
		t3 = DecisionTree.randomGrow(task, maxDepth, pool);
		System.out.println(t3.toPrettyString());
		System.out.println("----------------------------------------------------");
		
		DecisionTree.randomSwapSubtree(t1, t2);
		System.out.println("----------------------------------------------------");
		System.out.println(t1.toPrettyString());
		System.out.println("----------------------------------------------------");
		System.out.println(t2.toPrettyString());
		System.out.println("----------------------------------------------------");
		DecisionTree.releaseTree(t3, pool);
		t3 = DecisionTree.clone(t2, pool);
		System.out.println(t3.toPrettyString());
		System.out.println("----------------------------------------------------");
		*/
		
		
		//System.out.println(DecisionTree.maxNumOfNodes(3));
		/*
		Chromosome c1 = new Chromosome(1), c2 = new Chromosome(1);
		c1.init(ds.dimension, ds.numOfClass, maxDepth);
		c2.init(ds.dimension, ds.numOfClass, maxDepth);
		
		System.out.println("--------------------------------------------------------------------");
		System.out.println(c1.tree.toString());
		System.out.println("--------------------------------------------------------------------");
		System.out.println(c2.tree.toString());
		
		System.out.println("--------------------------------------------------------------------");
		ds.printInstance(0, 2);
		System.out.println("--------------------------------------------------------------------");
		
		System.out.println(c1.tree.getDecision(ds.instance.get(0).features));
		System.out.println(c1.tree.getDecision(ds.instance.get(1).features));
		
		System.out.println(c2.tree.getDecision(ds.instance.get(0).features));
		System.out.println(c2.tree.getDecision(ds.instance.get(1).features));
		
		System.out.println("--------------------------------------------------------------------");
		System.out.println(task.evaluate(c1));
		System.out.println(task.testAccuracy(c1));
		System.out.println(task.evaluate(c2));
		System.out.println(task.testAccuracy(c2));
		*/
		
		
		//DecisionTree mappedTree = c1.tree.clone();
		//DecisionTree.mapFeature(mappedTree, featureMapping);
		//System.out.println(mappedTree.toString());
		
		/*
		Chromosome[] ch = Chromosome.crossover(c1, c2);
		
		System.out.println(ch[0].tree.toString());
		System.out.println("--------------------------------------------------------------------");
		System.out.println(ch[1].tree.toString());
		*/
		//System.out.println(ch[1].tree.clone().toString());
		
		//System.out.println(dt.toString());
	}

}

