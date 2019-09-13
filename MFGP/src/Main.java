import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	public static final int Bagging = 1;
	public static final int Repeating = 2;
	
	public static void main(String[] args) throws IOException {

		final Runtime rt = Runtime.getRuntime();

		final int repeat = 1;
		ArrayList<Experiment> exps = new ArrayList<Experiment>();
		Experiment exp;
		
		// Experiment #37
		exp = new Experiment();
		exp.popGP = 50;
		exp.genGP = 100;
		exp.runGP = true;
		
		exp.popMF = 50;
		exp.genMF = 100;
		exp.runMF = true;
		
		exp.ensembleSize = 2;
		exp.maxDepth = 2;
		exp.dataName = new String[exp.ensembleSize];
		exp.dataName[0] = "task1";
		exp.dataName[1] = "task2";
		
		exp.kfold = 3;
		exp.ensembleMethod = Main.Bagging;
		exps.add(exp);

		// Experiment #38
		/*
		exp = new Experiment();
		exp.popGP = 2000;
		exp.genGP = 250
		exp.runGP = true;
		
		exp.popMF = 2000;
		exp.genMF = 250;
		exp.runMF = false;
		
		exp.ensembleSize = 1;
		exp.maxDepth = 10;
		exp.dataName = "GCD";
		
		exp.kfold = 10;
		exp.ensembleMethod = Main.Bagging;
		exps.add(exp);

		// Experiment #39
		exp = new Experiment();
		exp.popGP = 2000;
		exp.genGP = 250;
		exp.runGP = true;
		
		exp.popMF = 2000;
		exp.genMF = 250;
		exp.runMF = false;
		
		exp.ensembleSize = 1;
		exp.maxDepth = 10;
		exp.dataName = "LD";
		
		exp.kfold = 10;
		exp.ensembleMethod = Main.Bagging;
		exps.add(exp);

		// Experiment #40
		exp = new Experiment();
		exp.popGP = 2000;
		exp.genGP = 250;
		exp.runGP = true;
		
		exp.popMF = 2000;
		exp.genMF = 250;
		exp.runMF = false;
		
		exp.ensembleSize = 1;
		exp.maxDepth = 10;
		exp.dataName = "PID";
		
		exp.kfold = 10;
		exp.ensembleMethod = Main.Bagging;
		exps.add(exp);
        */

		for(int i=0; i<repeat; i++) {
			for(Experiment e : exps) {
				Main.runExperiment(e);
			}
		}
	}
	
	public static void runExperiment(Experiment exp) throws IOException {
		/********** Do NOT edit the following codes **********/
		final int popGP = exp.popGP;
		final int genGP = exp.genGP;
		final boolean runGP = exp.runGP;
		
		final int popMF = exp.popMF;
		final int genMF = exp.genMF;
		final boolean runMF = exp.runMF;
		
		final int maxDepth = exp.maxDepth;
		final int kfold = exp.kfold;
		int ensembleSize = exp.ensembleSize; // number of tasks
		int ensembleMethod = exp.ensembleMethod;
		
		final String[] dataName = exp.dataName;
		
		Task[] tasks = new Task[ensembleSize];

		Dataset ds = new Dataset("../dataset/"+dataName[0]+".csv");

		int poolSize = popGP * 2 * DecisionTree.maxNumOfNodes(maxDepth);
		String rootDir = System.getProperty("user.dir")+"/data/"+dataName+"/";
		String GPDir = rootDir+"GP_m"+ensembleMethod+"_es"+ensembleSize+"_p"+popGP+"_g"+genGP+"_d"+maxDepth;
		String MFDir = rootDir+"MF_m"+ensembleMethod+"_es"+ensembleSize+"_p"+popMF+"_g"+genMF+"_d"+maxDepth;

		// cross validation accuracy
		double[] cvAccGP = new double[kfold];
		double[] cvAccMF = new double[kfold];
		
		/********** Initialize tasks **********/
		for(int f=0; f<kfold; f++) {
			System.out.println("\nCrossvalidation #"+f);
			
			int[] testingInstance;
			int[] trainingInstance;
			int[] shuffle = IndexGenerator.randomPermutation(ds.length);
			testingInstance = Arrays.copyOf(shuffle, (ds.length/kfold));
			trainingInstance = Arrays.copyOfRange(shuffle, (ds.length/kfold), shuffle.length);

			// Preparing tasks for ensemble learning
			if(ensembleSize > 1) {

				switch(ensembleMethod) {
				case Bagging:
					for(int t=0; t<tasks.length; t++) {
						// load dataset for each task
						ds = new Dataset("../dataset/"+dataName[t]+".csv");

						poolSize = popGP * 2 * DecisionTree.maxNumOfNodes(maxDepth);
						rootDir = System.getProperty("user.dir")+"/data/"+dataName+"/";
						GPDir = rootDir+"GP_m"+ensembleMethod+"_es"+ensembleSize+"_p"+popGP+"_g"+genGP+"_d"+maxDepth;
						MFDir = rootDir+"MF_m"+ensembleMethod+"_es"+ensembleSize+"_p"+popMF+"_g"+genMF+"_d"+maxDepth;

						int[] featureMap = IndexGenerator.randomPermutation(ds.dimension, (ds.dimension/2)+1);
						int[] bag = IndexGenerator.randomBagging(trainingInstance.length, trainingInstance.length);
						int[] trainSampled = new int[trainingInstance.length];
						for(int i=0; i<trainingInstance.length; i++) {
							trainSampled[i] = trainingInstance[bag[i]];
						}
						tasks[t] = new Task(ds, trainSampled, testingInstance, featureMap, maxDepth);
					}
					break;
				case Repeating:
					// TODO Code section of Island model
					break;
				default:
					System.out.println("[Error] no such method.");
					return;
				}
			}
			else if(ensembleSize == 1) {
				// Preparing a task for single classifier learning
				int[] featureMap = IndexGenerator.serialIndex(ds.dimension);
				
				tasks[0] = new Task(ds, trainingInstance, testingInstance, featureMap, maxDepth);
			}
			else {
				System.out.println("[Error] # classifiers must be at least 1.");
				return;
			}
			
			/********** Run GP **********/
			if(runGP) {
				(new File(GPDir)).mkdirs();
				
				DecisionTree[] ensemble = new DecisionTree[tasks.length];
				double[] weights = new double[tasks.length];
				
				for(int t=0; t<tasks.length; t++) {
					// allocate space for treePool
					Pool treePool = new Pool(poolSize);
					
					GP gp = new GP(tasks[t], genGP, popGP, treePool);
					ArrayList<Record> records = gp.evolve();
					
					Chromosome bestIndFinal = records.get(records.size()-1).bestIndividual;
					
					// Reverse mapping
					DecisionTree.mapFeature(bestIndFinal.tree, tasks[t].featureMapping);
					ensemble[t] = bestIndFinal.tree;
					
					// Get accuracy of reverse mapped tree with respect to whole training set
					weights[t] = ds.soloAccuracy(ensemble[t], trainingInstance);
					
					// Recording to files
					String fileTrain = GPDir+"/fold"+f+"_en"+t+".csv";
					String fileTest = GPDir+"/fold"+f+"_en"+t+".csv";
					
					PrintWriter pwTrain = new PrintWriter(new BufferedWriter(new FileWriter(fileTrain, true)));
					PrintWriter pwTest = new PrintWriter(new BufferedWriter(new FileWriter(fileTest, true)));
					
					String[] bufferTrain = new String[genGP];
					String[] bufferTest = new String[genGP];
					for(int g=0; g<genGP; g++) {
						bufferTrain[g] = String.format("%.3f", records.get(g).trainingAccuracy);
						bufferTest[g] = String.format("%.3f", records.get(g).testingAccuracy);
					}
					pwTrain.println(String.join(",", bufferTrain));
					pwTest.println(String.join(",", bufferTest));
					pwTrain.close();
					pwTest.close();
				}
				
				// Test and record ensemble accuracy
				// Code section of weighted vote
				double enAcc = ds.ensembleAccuracyWeighted(ensemble, weights, testingInstance);
				
				// Code section of majority vote
				// double enAcc = ds.ensembleAccuracyMajority(ensemble);
				
				String fileAcc = GPDir+"/acc_fold"+f+".csv";
				PrintWriter pwAcc = new PrintWriter(new BufferedWriter(new FileWriter(fileAcc, true)));
				pwAcc.println(String.format("%.3f", enAcc));
				pwAcc.close();
				System.out.println(String.format("GP Acc = %.3f", enAcc));
				
				cvAccGP[f] = enAcc;
				
				System.gc();
			}
			
			// TODO MFGP
			/********** Run MFGP **********/
			if(runMF) {
				(new File(MFDir)).mkdirs();
				
				DecisionTree[] ensemble = new DecisionTree[tasks.length];
				double[] weights = new double[tasks.length];
				
				// allocate space for treePool
				Pool treePool = new Pool(poolSize);
				
				MFGP mfgp = new MFGP(tasks, genGP, popGP, treePool);
				MultiTaskRecords mtRecords = mfgp.evolve();
				
				//String[] fileTrain = new String[tasks.length];
				//String[] fileTest = new String[tasks.length];
				for(int t=0; t<tasks.length; t++) {
					// Get bestInd in final population
					//Chromosome bestIndFinal = records.get(records.size()-1).bestIndividual;
					ArrayList<Record> tskRecords = mtRecords.multiTaskRecords.get(t);
					Chromosome bestIndFinal = tskRecords.get(tskRecords.size()-1).bestIndividual;
					
					// Reverse mapping
					DecisionTree.mapFeature(bestIndFinal.tree, tasks[t].featureMapping);
					ensemble[t] = bestIndFinal.tree;
                    System.out.println(bestIndFinal.tree.toPrettyString());
					// Get accuracy of reverse mapped tree with respect to whole training set
					weights[t] = ds.soloAccuracy(ensemble[t], trainingInstance);
					
					// Recording to files
					String fileTrain = MFDir+"/fold"+f+"_en"+t+".csv";
					String fileTest = MFDir+"/fold"+f+"_en"+t+".csv";
					PrintWriter pwTrain = new PrintWriter(new BufferedWriter(new FileWriter(fileTrain, true)));
					PrintWriter pwTest = new PrintWriter(new BufferedWriter(new FileWriter(fileTest, true)));
					String[] bufferTrain = new String[genGP];
					String[] bufferTest = new String[genGP];
					File file = new File("../fitness.txt");
					FileWriter fr = new FileWriter(file);
					File file2 = new File("../bestTree.txt");
					FileWriter fr2 = new FileWriter(file2);

					for(int g=0; g<genGP; g++) {
						bufferTrain[g] = String.format("%.3f", tskRecords.get(g).trainingAccuracy);
						bufferTest[g] = String.format("%.3f", tskRecords.get(g).testingAccuracy);
						fr.write("Fold "+ f + " Generation " + g + " Fitness " + tskRecords.get(g).trainingAccuracy +"\n");
						//System.out.println("Fold "+ f + " Generation " + g + " Fitness " + tskRecords.get(g).trainingAccuracy);
						Chromosome bestIndFinalg = tskRecords.get(g).bestIndividual;
						//System.out.println(bestIndFinalg.tree.toPrettyString());
						fr2.write(bestIndFinalg.tree.toPrettyString());
						fr2.write("new");

					}
					fr.close();
					fr2.close();

					pwTrain.println(String.join(",", bufferTrain));
					pwTest.println(String.join(",", bufferTest));
					pwTrain.close();
					pwTest.close();
				}
				
				// Test and record ensemble accuracy
				// Code section of weighted vote
				double enAcc = ds.ensembleAccuracyWeighted(ensemble, weights, testingInstance);
				
				// Code section of majority vote
				// double enAcc = ds.ensembleAccuracyMajority(ensemble);
				
				String fileAcc = MFDir+"/acc_fold"+f+".csv";
				PrintWriter pwAcc = new PrintWriter(new BufferedWriter(new FileWriter(fileAcc, true)));
				pwAcc.println(String.format("%.3f", enAcc));
				pwAcc.close();
				System.out.println(String.format("MF Acc = %.3f", enAcc));
				cvAccMF[f] = enAcc;
				
				System.gc();
			}
		}
		
		if(runGP) {
			double avgGPAcc = 0;
			for(double acc : cvAccGP) {
				avgGPAcc += acc; 
			}
			avgGPAcc = avgGPAcc / kfold;
			
			String fileGPAcc = GPDir+"/GP_avgAcc.txt";
			PrintWriter pwGPAcc = new PrintWriter(new BufferedWriter(new FileWriter(fileGPAcc, true)));
			pwGPAcc.println(String.format("Average: %.3f", avgGPAcc));
			pwGPAcc.close();
			
			// Print summary		
			System.out.printf("Average GP Acc = %.3f\n", avgGPAcc);
		}
		
		if(runMF) {
			double avgMFAcc = 0;
			for(double acc : cvAccMF) {
				avgMFAcc += acc; 
			}
			avgMFAcc = avgMFAcc / kfold;
			
			String fileMFAcc = MFDir+"/MF_avgAcc.txt";
			PrintWriter pwMFAcc = new PrintWriter(new BufferedWriter(new FileWriter(fileMFAcc, true)));
			pwMFAcc.println(String.format("Average: %.3f", avgMFAcc));
			pwMFAcc.close();
			
			// Print summary
			System.out.printf("Average MF Acc = %.3f\n", avgMFAcc);
		}
	}
}

















