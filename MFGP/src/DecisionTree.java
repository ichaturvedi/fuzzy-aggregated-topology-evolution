import java.util.Random;

public class DecisionTree {
	public static final int NONLEAF = 1;
	public static final int LEAF = 2;
	public static final int GREATER = 1;
	public static final int LESS = 2;
	public static final int ADD = 3;
	public static final int SUB = 4;
	public static final int MUL = 5;
	public static final int GAUSSIAN = 6;
	public static final int CUBERT = 7;
	public static final int COS = 8;
	public static final int SQRT = 9;
	public static final int SIN = 10;
    private static int noOp = 10;

	public DecisionTree posChild;
	public DecisionTree negChild;
	
	public int condition;
	public int decision;
	public int depth;
	public int feature; // feature id
	public double threshold; // threshold respect to fid
	public int type;
	public int poolIndex;
	
	public static DecisionTree randomGrow(int dimension, int numOfClass, int depth, Pool pool) {
		Random rand = new Random();
		int poolIndex = pool.getOne();
		DecisionTree tree = pool.pool[poolIndex];
		tree.poolIndex = poolIndex;
		
		if(depth == 0) {
			tree.posChild = null;
			tree.negChild = null;
			tree.type = DecisionTree.LEAF;
			tree.feature = rand.nextInt(dimension); //-1;
			tree.threshold = 0;
			tree.condition = rand.nextInt(noOp) + 1; //-1;
			tree.depth = 0;
			tree.decision = rand.nextInt(numOfClass); // Class: 0, 1, ..., (# class - 1)		
			return tree;
		}
		else {
			tree.posChild = DecisionTree.randomGrow(dimension, numOfClass, (depth-1), pool);
			tree.negChild = DecisionTree.randomGrow(dimension, numOfClass, (depth-1), pool);
			tree.type = DecisionTree.NONLEAF;
			tree.feature = rand.nextInt(dimension);
			tree.threshold = rand.nextDouble();
			//tree.condition = rand.nextBoolean() ? DecisionTree.GREATER : DecisionTree.LESS;
			tree.condition = rand.nextInt(noOp) + 1;
			tree.depth = depth; 
		}
		
		return tree;
	}
	
	public static void releaseTree(DecisionTree tree, Pool pool) {
		if(tree.type == DecisionTree.LEAF) {
			pool.release(tree.poolIndex);
			return;
		}
		else {
			DecisionTree.releaseTree(tree.posChild, pool);
			DecisionTree.releaseTree(tree.negChild, pool);
			pool.release(tree.poolIndex);
		}
	}
	
	public static DecisionTree clone(DecisionTree source, Pool pool) {
		int poolIndex = pool.getOne();
		DecisionTree tree = pool.pool[poolIndex];
		tree.poolIndex = poolIndex;

		tree.type = source.type;
		tree.feature = source.feature;
		tree.threshold = source.threshold;
		tree.condition = source.condition;
		tree.decision = source.decision;
		tree.depth = source.depth;
		
		if(source.type == DecisionTree.NONLEAF) {
			tree.posChild = DecisionTree.clone(source.posChild, pool);
			tree.negChild = DecisionTree.clone(source.negChild, pool);
		}
		else {
			tree.posChild = null;
			tree.negChild = null;
			tree.type = source.type;
		}
		
		return tree;
	}
	
	public DecisionTree() {
		//parent = null;
		this.posChild = null;
		this.negChild = null;
		this.type = DecisionTree.LEAF;
		this.feature = -1;
		this.threshold = 0;
		this.condition = -1;
		this.depth = 0;
	}
	
	// Randomly grow to given depth
	public DecisionTree(int dimension, int numOfClass, int depth) {
		Random rand = new Random();
		if(depth == 0) {
			this.posChild = null;
			this.negChild = null;
			this.type = DecisionTree.LEAF;
			this.feature = -1;
			this.threshold = 0;
			this.condition = -1;
			this.depth = 0;
			this.decision = rand.nextInt(numOfClass); // Class: 0, 1, ..., (# class - 1)		
			return;
		}
		else {
			this.posChild = new DecisionTree(dimension, numOfClass, (depth-1));
			this.negChild = new DecisionTree(dimension, numOfClass, (depth-1));
			this.type = DecisionTree.NONLEAF;
			this.feature = rand.nextInt(dimension);
			this.threshold = rand.nextDouble();
			//this.condition = rand.nextBoolean() ? DecisionTree.GREATER : DecisionTree.LESS;
			this.condition = rand.nextInt(noOp) + 1;
			this.depth = depth; 
		}
	}
	
	public int updateDepth() {
		if(this.type == DecisionTree.LEAF) {
			this.depth = 0;
			return 0;
		}
		
		int d1 = this.posChild.updateDepth();
		int d2 = this.negChild.updateDepth();
		this.depth = (d1 > d2) ? (d1+1) : (d2+1);
		
		return this.depth;
	}
	
	public int getDecision(double[] data, int[] featureMapping) {
		if(this.condition == DecisionTree.GREATER) {
			if(data[featureMapping[this.feature]] > this.threshold) {
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data, featureMapping);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data, featureMapping);
			}
		}
		else if(this.condition == DecisionTree.LESS) {
			if(data[featureMapping[this.feature]] < this.threshold) {
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data, featureMapping);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data, featureMapping);
			}
		}
		else if(this.condition == DecisionTree.ADD)  {
			if(this.posChild.feature + this.negChild.feature  > this.threshold) {
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data, featureMapping);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data, featureMapping);
			}
		}
		else if(this.condition == DecisionTree.SUB)  {
			if(this.posChild.feature - this.negChild.feature  > this.threshold) {
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data, featureMapping);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data, featureMapping);
			}
		}
		else if(this.condition == DecisionTree.MUL)  {
			if(this.posChild.feature * this.negChild.feature  > this.threshold) {
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data, featureMapping);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data, featureMapping);
			}
		}
		else if(this.condition == DecisionTree.GAUSSIAN) {
			Random r = new Random();
			double mySample = r.nextGaussian()*this.feature;
			if(mySample  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.CUBERT) {
			if(Math.cbrt(this.feature)  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.COS) {
			if(Math.cos(this.feature)  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.SQRT) {
			if(Math.sqrt(this.feature)  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.SIN) {
			if(Math.sin(this.feature)  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}

		return -1; // this line is unreachable
	}
	
	public int getDecision(double[] data) {
		
		if(this.condition == DecisionTree.GREATER) {
			if(data[this.feature] > this.threshold) {
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.LESS) {
			if(data[this.feature] < this.threshold) {
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else { 
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.ADD) {
			if(this.posChild.feature + this.negChild.feature  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.SUB) {
			if(this.posChild.feature - this.negChild.feature  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.MUL) {
			if(this.posChild.feature * this.negChild.feature  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.GAUSSIAN) {
			Random r = new Random();
			double mySample = r.nextGaussian()*this.feature;
			if(mySample  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.CUBERT) {
			if(Math.cbrt(this.feature)  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.COS) {
			if(Math.cos(this.feature)  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.SQRT) {
			if(Math.sqrt(this.feature)  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
		else if(this.condition == DecisionTree.SIN) {
			if(Math.sin(this.feature)  > this.threshold){
				return (this.posChild.type==DecisionTree.LEAF) ? this.posChild.decision : this.posChild.getDecision(data);
			}
			else {
				return (this.negChild.type==DecisionTree.LEAF) ? this.negChild.decision : this.negChild.getDecision(data);
			}
		}
	
		//System.out.println("Condition: "+this.condition);
		//System.out.println("Type: "+this.type);
		return -1; // this line is unreachable
	}
	
	public static void mapFeature(DecisionTree tree, int[] featureMapping) {
		if(tree.type == DecisionTree.NONLEAF) {
		    try{ 
			   tree.feature = featureMapping[tree.feature];
            }
			catch(Exception e){}

			DecisionTree.mapFeature(tree.posChild, featureMapping);
			DecisionTree.mapFeature(tree.negChild, featureMapping);
		}
		
		return;
	}
	
	public static void randomSwapSubtree(DecisionTree tree1, DecisionTree tree2) {
		DecisionTree subtree1, subtree2, tmp1, tmp2;
		subtree1 = tree1.getRandomSubtree();
		subtree2 = tree2.getRandomSubtree();
		
		Random rand = new Random();
		boolean b1 = rand.nextBoolean(), b2 = rand.nextBoolean();
		
		tmp1 = (b1 == true) ? subtree1.posChild : subtree1.negChild;
		tmp2 = (b2 == true) ? subtree2.posChild : subtree2.negChild;
		
		if(b1 == true) {
			subtree1.posChild = tmp2;
		}
		else {
			subtree1.negChild = tmp2;
		}
		
		if(b2 == true) {
			subtree2.posChild = tmp1;
		}
		else {
			subtree2.negChild = tmp1;
		}
		
		tree1.updateDepth();
		tree2.updateDepth();
		return;
	}
	
	public DecisionTree getRandomSubtree() {
		Random rand = new Random();
		DecisionTree currentTree = this;
		
		while(currentTree.type == DecisionTree.NONLEAF) {
			switch(rand.nextInt(noOp)) {
			// random integer in [0,1,2]
			case 0:
				return currentTree;
				
			case 1:
				if(currentTree.posChild.type == DecisionTree.LEAF) {
					return currentTree;
				}
				else {
					currentTree = currentTree.posChild;
				}
				break;
				
			case 2:
				if(currentTree.negChild.type == DecisionTree.LEAF) {
					return currentTree;
				}
				else {
					currentTree = currentTree.negChild;
				}
				break;
			}
			
		}
		
		// Reach a leaf node
		return null;
	}

	public DecisionTree cloneOld() {
		DecisionTree tree = new DecisionTree();

		tree.type = this.type;
		tree.feature = this.feature;
		tree.threshold = this.threshold;
		tree.condition = this.condition;
		tree.decision = this.decision;
		tree.depth = this.depth;
		
		if(this.type == DecisionTree.NONLEAF) {
			tree.posChild = this.posChild.cloneOld();
			tree.negChild = this.negChild.cloneOld();
		}
		else {
			tree.posChild = null;
			tree.negChild = null;
			tree.type = this.type;
		}
		
		return tree;
	}
	
	public DecisionTree readFromString(String s) {
		DecisionTree tree = new DecisionTree();
		

		
		
		return tree;
	}
	
	@Override
	public String toString() {
		int size = DecisionTree.maxNumOfNodes(12);
		String[] nodes = new String[size];
		String buffer = "";
		
		nodes[0] = this.condition2String();

		return buffer;
	}
	
	public String toString(int[] mapping) {
		String buffer = "";

		
		return buffer;
	}
	
	public String toPrettyString() {
		return this.toPrettyString(0);
	}
	
	private String toPrettyString(int currentDepth) {
		String prefix = "";
		for(int i=0; i<currentDepth; i++) {
			prefix += "\t\t";
		}
		if(this.type == DecisionTree.LEAF) {
			return prefix+this.decision+"\n";
		}
		String pos = this.posChild.toPrettyString(currentDepth+1);
		String neg = this.negChild.toPrettyString(currentDepth+1);
		String current = prefix+this.condition2String()+"\n";
		return  pos+current+neg;
	}
	
	public String condition2String() {
		if(this.condition == DecisionTree.GREATER) {
			return "f"+String.format("%02d", this.feature)+" > "+String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.LESS) {
			return "f"+String.format("%02d", this.feature)+" < "+String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.ADD) {
			return "f"+String.format("%02d", this.posChild.feature)+" + " + String.format("%02d", this.negChild.feature) + " > " + String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.SUB) {
			return "f"+String.format("%02d", this.posChild.feature)+" - " + String.format("%02d", this.negChild.feature) + " > " + String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.MUL) {
			return "f"+String.format("%02d", this.posChild.feature)+" * " + String.format("%02d", this.negChild.feature) + " > " + String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.GAUSSIAN) {
			return "f"+String.format("%02d", this.posChild.feature)+" g " + " > " + String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.CUBERT) {
			return "f"+String.format("%02d", this.posChild.feature)+" c3 " + " > " + String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.COS) {
			return "f"+String.format("%02d", this.posChild.feature)+" cos " + " > " + String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.SQRT) {
			return "f"+String.format("%02d", this.posChild.feature)+" sqrt " + " > " + String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.SIN) {
			return "f"+String.format("%02d", this.posChild.feature)+" sin " + " > " + String.format("%.3f", threshold);
		}
		else {
			return "f"+String.format("%02d", this.feature)+" ? "+String.format("%.3f", threshold);
		}

	}
	
	public String condition2String(int[] mapping) {
		if(this.condition == DecisionTree.GREATER) {
			return "f"+String.format("%02d", mapping[this.feature])+" > "+String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.LESS) {
			return "f" + String.format("%02d", mapping[this.feature]) + " < " + String.format("%.3f", threshold);
		}
		else if(this.condition == DecisionTree.ADD) {
			return "f" + String.format("%02d", mapping[this.posChild.feature]) + " + " + String.format("%02d", mapping[this.negChild.feature])  +" > "+String.format("%.3f", threshold) ;
		}
		else if(this.condition == DecisionTree.SUB) {
			return "f" + String.format("%02d", mapping[this.posChild.feature]) + " - " + String.format("%02d", mapping[this.negChild.feature])  +" > "+String.format("%.3f", threshold) ;
		}
		else if(this.condition == DecisionTree.MUL) {
			return "f" + String.format("%02d", mapping[this.posChild.feature]) + " * " + String.format("%02d", mapping[this.negChild.feature])  +" > "+String.format("%.3f", threshold) ;
		}
		else if(this.condition == DecisionTree.GAUSSIAN) {
			return "f" + String.format("%02d", mapping[this.posChild.feature]) + " g " + " > "+String.format("%.3f", threshold) ;
		}
		else if(this.condition == DecisionTree.CUBERT) {
			return "f" + String.format("%02d", mapping[this.posChild.feature]) + " c3 " + " > "+String.format("%.3f", threshold) ;
		}
		else if(this.condition == DecisionTree.COS) {
			return "f" + String.format("%02d", mapping[this.posChild.feature]) + " cos " + " > "+String.format("%.3f", threshold) ;
		}
		else if(this.condition == DecisionTree.SQRT) {
			return "f" + String.format("%02d", mapping[this.posChild.feature]) + " sqrt " + " > "+String.format("%.3f", threshold) ;
		}
		else if(this.condition == DecisionTree.SIN) {
			return "f" + String.format("%02d", mapping[this.posChild.feature]) + " sin " + " > "+String.format("%.3f", threshold) ;
		}
		else {
			return "f"+String.format("%02d", mapping[this.feature])+" ? "+String.format("%.3f", threshold);
		}
	}
	
	public static int maxNumOfNodes(int depth) {
		int num = 0;
		
		for(int i=0; i<(depth+1); i++) {
			num += (int) Math.pow(2, (double) i);
		}
		
		return num;
	}
}

