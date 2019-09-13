import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

public class Pool {
	//private int numOfObject;
	//private int threshold;
	//private int counter;
	private final static int extendingSize = 2000;
	private int currentSize; 
	//private Class<T> clazz;
	private ArrayList<Integer> freeList;
	
	public DecisionTree[] pool;
	
	public Pool(int numOfNode) {
		//this.numOfObject = numOfNode;
		//this.clazz = clazz;
		//this.pool = (T[]) Array.newInstance(clazz, numOfNode);
		this.currentSize = numOfNode;
		this.pool = new DecisionTree[numOfNode];
		this.freeList = new ArrayList<Integer>(numOfNode);
		
		for(int i=0; i<numOfNode; i++) {
			this.pool[i] = new DecisionTree();
			this.freeList.add(i);
		}
		
		//threshold = this.pool.length/5;
		//counter = 0;
	}
	
	public int getOne() {
		if(!this.freeList.isEmpty()) {
			return this.freeList.remove(this.freeList.size()-1);
		}
		else {
			// TODO try extending the pool
			this.extend();
			return this.freeList.remove(this.freeList.size()-1);
			// throw new OutOfMemoryError("[Error] All pool nodes are occupied.");
		}
	}
	
	public void release(int index) {
		this.freeList.add(index);
	}
	
	public int availableSize() {
		return this.freeList.size();
	}
	
	public void regularcompress() {

	}
	
	// This memory compressing method should be called after crossover
	public void extremeCompress() {
		// TODO set stopwatch
		
		if(this.freeList.size() == 0) {
			return;
		}
		
		// Sort freeList
		this.freeList.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer arg0, Integer arg1) {
				return Integer.compare(arg1, arg0); // Descending order
			}
		});
		
		System.out.println("@@ Building occupiedList ...");
		
		// Build occupiedList
		ArrayList<Integer> occupiedList = new ArrayList<Integer>(this.pool.length - this.freeList.size());
		for(int i=0; i<this.pool.length; i++) {
			if(!this.freeList.contains(i)) {
				occupiedList.add(i);
			}
		}
		
		System.out.println("@@ Compressing ...");
		
		while(this.freeList.size() > 0) {
			// find hole with min index
			int holeIndex = this.freeList.remove(this.freeList.size()-1);
			
			// find occupied with max index
			int occupiedIndex = occupiedList.remove(occupiedList.size()-1);
			
			if(holeIndex < occupiedIndex) {
				// swap
				this.pool[holeIndex] = this.pool[occupiedIndex];
				
				// update index
				this.pool[holeIndex].poolIndex = holeIndex;
				
				// set reference to null
				this.pool[occupiedIndex] = null;
				
				// update currentSize
				this.currentSize--;
			}
			else {
				break;
			}
		}
		
		while(this.freeList.size() > 0) {
			int holeIndex = this.freeList.remove(this.freeList.size()-1);
			this.pool[holeIndex] = null;
			this.currentSize--;
		}
		
		
		System.out.println("@@ Compression done ...");
		
		System.gc();
	}
	
	public void extend() {
		System.out.println("########### Extending pool ##########");
		for(int i=0; i<Pool.extendingSize; i++) {
			int index = this.currentSize + i;
			this.pool[index] = new DecisionTree();
			this.freeList.add(index);
		}
		this.currentSize += Pool.extendingSize;
	}
}























