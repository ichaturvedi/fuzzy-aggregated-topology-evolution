import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SmallTest {
	
	public static void main(String[] args) throws IOException {
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		
		String currentDir = System.getProperty("user.dir");
		String dataDir = currentDir+"/data/123/789/";
		
		(new File(dataDir)).mkdirs();
		
		String file = dataDir + "testfile";
		
		PrintWriter pwTest = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		pwTest.println("Hi");
		pwTest.close();
	}

}