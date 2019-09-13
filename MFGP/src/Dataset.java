import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Dataset {
	public int length;
	public int dimension;
	public int numOfClass; // Support only enumerative class (regression not supported)
	public double[] upperBound;
	public double[] lowerBound;
	public ArrayList<String> classList;
	public ArrayList<Instance> instance;
	
	// Class should go after features
	public Dataset(String filename) {
		this.length = 0;
		this.instance = new ArrayList<Instance>(1000);
		this.classList = new ArrayList<String>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line = br.readLine();
			String[] fields = line.split(",");
			this.dimension = fields.length - 1;
			
			this.upperBound = new double[this.dimension];
			this.lowerBound = new double[this.dimension];
			
			Arrays.fill(this.upperBound, Double.MIN_VALUE);
			Arrays.fill(this.lowerBound, Double.MAX_VALUE);
						
		    while (line != null) {
		    	fields = line.split(",");
		    	double[] feature = new double[fields.length-1];
		    	
		    	int i;
		    	for(i=0; i<(fields.length-1); i++) {
		    		feature[i] = Double.parseDouble(fields[i]);
		    		if( (feature[i]+1) > this.upperBound[i] ) {
		    			this.upperBound[i] = feature[i]+1;
		    		}
		    		if( (feature[i]-1) < this.lowerBound[i] ) {
		    			this.lowerBound[i] = feature[i]-1;
		    		}
		    	}
		    	
		    	String className = fields[i];
		    	int classIndex = this.classList.indexOf(className); 
		    	if(classIndex == -1) {
		    		this.classList.add(className);
		    		classIndex = this.classList.size() - 1;
		    	}
		    	
		    	this.instance.add(new Instance(feature, classIndex));
		    	
		    	line = br.readLine();
		    }
		    br.close();
		}
		catch(IOException e) {
			e.printStackTrace();
			System.out.println("Error: something wrong with '"+filename+"'.");
		}
		
		this.numOfClass = this.classList.size();
		this.length = this.instance.size();
		this.normalize();
	}
	
	// this function will normalize data to [0, 1]
	private void normalize() {		
		Instance ins;
		for(int i=0; i<this.length; i++) {
			ins = this.instance.get(i);
			for(int d=0; d<this.dimension; d++) {
				ins.features[d] = (ins.features[d] - this.lowerBound[d]) / (this.upperBound[d] - this.lowerBound[d]);
			}
		}
		return;
	}
	
	public double soloAccuracy(DecisionTree tree, int[] testingInstance) {
		// Features of trees must be reverse mapped before passed in
		double correct=0, wrong=0;
		
		for(int i=0; i<testingInstance.length; i++) {

			Instance inst = this.instance.get(testingInstance[i]);
			int prediction = tree.getDecision(inst.features);

			if(prediction == inst.target) {
				correct++;
			}
			else {
				wrong++;
			}
		}
		
		return correct / (correct+wrong);
	}
	
	public double ensembleAccuracyMajority(DecisionTree[] trees, int[] testingInstance) {
		// Features of trees must be reverse mapped before passed in
		double correct=0, wrong=0;
		
		for(int i=0; i<testingInstance.length; i++) {
			Instance inst = this.instance.get(testingInstance[i]);
			int[] votes = new int[this.numOfClass];
			
			for(DecisionTree tree : trees) {
				votes[tree.getDecision(inst.features)]++;
			}
			
			int answer=0;
			for(int j=1; j<votes.length; j++) {
				if(votes[j] > votes[answer]) {
					answer = j;
				}
			}
			
			if(answer == inst.target) {
				correct++;
			}
			else {
				wrong++;
			}
		}
		return correct / (correct+wrong);
	}
	
	public double ensembleAccuracyWeighted(DecisionTree[] trees, double[] weights, int[] testingInstance) {
		// Features of trees must be reverse mapped before passed in
		double correct=0, wrong=0;
		
		for(int i=0; i<testingInstance.length; i++) {
			Instance inst = this.instance.get(testingInstance[i]);
			double[] votes = new double[this.numOfClass];
			
			for(int j=0; j<trees.length; j++) {
				int prediction = trees[j].getDecision(inst.features);
				//System.out.println("Prediction = "+prediction);
				votes[prediction] += weights[j];
			}
			
			int answer=0;
			for(int j=1; j<votes.length; j++) {
				if(votes[j] > votes[answer]) {
					answer = j;
				}
			}
			
			if(answer == inst.target) {
				correct++;
			}
			else {
				wrong++;
			}
		}
		return correct / (correct+wrong);
	}
	
	@Override
	public String toString() {
		String buffer = "Length: "+this.length+"\nDimension: "+this.dimension+"\n# Class: "+this.numOfClass;
		String upperList = "", lowerList = "";
		for(int i=0; i<this.dimension; i++) {
			upperList += String.format("%.3f", this.upperBound[i])+" ";
			lowerList += String.format("%.3f", this.lowerBound[i])+" ";
		}
		buffer += "\nUpp: "+upperList+"\nLow: "+lowerList;
		return buffer;
	}
	
	public void printInstance(int begin, int count) {
		if(begin > this.length || begin < 0) {
			System.out.println("Range Error: "+begin+" to "+(begin+count)+" are not accessible.");
		}
		else if(count < 0 ) {
			System.out.println("Range Error: "+begin+" to "+(begin+count)+" are not accessible.");
		}
		else {
			for(int i=begin; i<(begin+count); i++) {
				System.out.println(this.instance.get(i).toString()); 
			}
		}
	}
}
