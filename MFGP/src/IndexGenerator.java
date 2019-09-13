import java.util.Arrays;
import java.util.Random;

public class IndexGenerator {
	// 0 to bound
	public static int[] serialIndex(int bound) {
		int[] index = new int[bound];
		for(int i=0; i<index.length; i++) {
			index[i] = i;
		}
		return index;
	}
	
	// Sampling without replacement
	public static int[] randomPermutation(int bound) {
		Random rand = new Random();
		int[] index = IndexGenerator.serialIndex(bound);
		for(int i=0; i<index.length; i++) {
			int j = rand.nextInt(bound);
			int tmp = index[i];
			index[i] = index[j];
			index[j] = tmp;
		}
		return index;
	}
	public static int[] randomPermutation(int bound, int required) {
		if(required > bound) {
			return null;
		}
		
		Random rand = new Random();
		int[] index = IndexGenerator.serialIndex(bound);
		for(int i=0; i<index.length; i++) {
			int j = rand.nextInt(bound);
			int tmp = index[i];
			index[i] = index[j];
			index[j] = tmp;
		}
		return Arrays.copyOf(index, required);
	}
	
	
	// Sampling with replacement
	public static int[] randomBagging(int bound, int required) {		
		Random rand = new Random();
		int[] index = new int[required];
		for(int i=0; i<required; i++) {
			index[i] = rand.nextInt(bound);
		}
		return index;
	}
}
