package analyzerDriver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import analyzedClass.ClassConfiguration;
import analyzedClass.ClassContainer;

public class Driver {

	public static void injectorProcess(Class<?> cls, String originalFilePath, String reformattedFilePath, String injectedFilePath, ClassConfiguration classConfig) {
		ClassContainer classContainer = null;

		
		// =================== step 1 ===================
		// add brackets for loops and conditions if missing
		analyzerUtils.BracketsCodeReformatter.bracketsReformatter(originalFilePath, reformattedFilePath);
		
		// =================== step 2 ===================
		// reformat by google style code the original code and copy it to reformattedCodeFile
		analyzerUtils.CodeReformatter.googleCodeReformater(originalFilePath, reformattedFilePath);

		
		// =================== step 3 ===================
		// creating array list of the code in reformatted Code File
		File reformattedCodeFile = new File(reformattedFilePath);
		ArrayList<StringBuilder> code = analyzerUtils.FileOperations.fileContentToArrayListString(reformattedCodeFile, true);
		
		
		/* *
		 * create ClassContainer -
		 * maintain the class code in an object to manage correctly all 
		 * the changes that take place in the code
		 * * */
		// =================== step 4 ===================
		// + inject the variable definition code lines 
		// + inject the dependencies code line
		// + divide the class code to methods  
		classContainer = new ClassContainer(cls, code);
		
		
		// =================== step 5 ===================
		// start scanning and the code injection
		analyzerAlgo.ForLoopInj.mainScanner(classContainer.getMethodsDataSet());
		
		
		// =================== step 6 ===================
		// define the new class configuration and set them
		classContainer.setClassConfiguration(classConfig);
		
		
		// =================== step 7 ===================
		// write all the code lines in classContainer to the injectedCodeFile
		Path injectedCodeFile = Paths.get(injectedFilePath);
		classContainer.constructNewClass(injectedCodeFile);

		
	}
	
}
