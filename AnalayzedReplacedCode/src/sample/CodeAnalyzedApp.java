package sample;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import analyzedClass.ClassConfiguration;
import analyzerAbs.AnalyzerCounters;
import complexityAssessment.AnalyzerSample;
import complexityAssessment.ComplexityAssessmentAlgo;
import dynacode.DynaCode;

public class CodeAnalyzedApp {

	public static String thisClassPath = "C:\\javaCode\\a-c-project\\analyzer-project\\AnalayzedReplacedCode" ;

	public static void main(String[] args) throws Exception  {

		CodeAnalyze analyze = compileDynamicClassProcess();  // can throw exp

		// ====================********************** ONI IS A FUCKIN BITCH **********************==================== //

		ArrayList<Object[]> algoInputs = getInputDataFromJson();
		ArrayList<AnalyzerSample> samples = codeSamplingProcess(analyze, algoInputs);
		int[] results = ComplexityAssessmentAlgo.findBigOInSample(samples);
		System.out.println(Arrays.toString(results));

	}

	private static CodeAnalyze compileDynamicClassProcess() throws InterruptedException {
		String originPath = thisClassPath + "\\src\\analyzedClass\\FindMaxElement_B.java";
		String reformatPath = thisClassPath + "\\tmp\\rmf";
		String finalPath = thisClassPath + "\\dynacode\\sample\\AnalyzedCodeImpl.java";

		ClassConfiguration classConfig = getClassConfiguration();
		Class<?> cls = analyzedClass.FindMaxElement_B.class;
		analyzerDriver.Driver.injectorProcess(cls, originPath, reformatPath, finalPath, classConfig);

		Thread.sleep(2000);

		return getAnalyzedCode();
	}

	public static CodeAnalyze getAnalyzedCode() {
		DynaCode dynacode = new DynaCode();
		dynacode.addSourceDir(new File("dynacode"));
		return (CodeAnalyze) dynacode.newProxyInstance(CodeAnalyze.class,
				"sample.AnalyzedCodeImpl");
	}

	private static ArrayList<AnalyzerSample> codeSamplingProcess(CodeAnalyze analyze ,ArrayList<Object[]> algoInputs) {
		// can access only the inherited methods (from his interface)
		ArrayList<AnalyzerSample> returnedSamples = new ArrayList<AnalyzerSample>();
		
		for (int i = 0; i < algoInputs.size(); i++) {
			Object[] input = algoInputs.get(i);
			int inputSize = (int) input[1];
			analyze.runAlgorithm(input);
			AnalyzerCounters analyzerCounters = analyze.getAnalyzerCounters();
			System.out.println(analyzerCounters.__counters.toString());
			returnedSamples.add(convertCountersToSamples(analyzerCounters, inputSize));
		}

		return returnedSamples;
	}


	private static AnalyzerSample convertCountersToSamples(AnalyzerCounters counters, int inputSize) {
		BigDecimal list[] = new BigDecimal[counters.__counters.size()];
		return new AnalyzerSample(inputSize, (counters.__counters).toArray(list));
	}

	// unused
	public static AnalyzerCounters invokeMethodExtractCounters(CodeAnalyze analyze) {
		AnalyzerCounters analyzerCounters = null;
		try {
			// extract the method
			Method met = analyze.getClass().getDeclaredMethod("testerMethodB", int[].class, Integer.class);
			met.invoke(analyze, new int[] { 2, 5, 12, 346, 87 }, 89);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO: handle exception
		}
		try {
			// extract the counters list
			Field field = analyze.getClass().getField("analyzerCounters");
			analyzerCounters = (AnalyzerCounters) field.get(analyze);
			// analyzerCounters.printCounters();
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			// TODO: handle exception
		}
		return analyzerCounters;
	}

	public static ClassConfiguration getClassConfiguration() {
		ClassConfiguration classConfig = new ClassConfiguration();
		classConfig.packageName = "sample";
		classConfig.importsCodeLine = new String[]{"import analyzerAbs.AnalyzerCounters;"};
		classConfig.className = "AnalyzedCodeImpl";
		classConfig.implementsClasses = new String[]{"CodeAnalyze"};
		classConfig.extendsClasses = null;
		classConfig.classMembersCodeLines = new String[]{
				"public AnalyzerCounters analyzerCounters  = new AnalyzerCounters();"
		};
		classConfig.classMethodCodeLines = new String[]{
				"public AnalyzerCounters getAnalyzerCounters() { return analyzerCounters;}",
				"public void runAlgorithm(Object[] algoArgs) { findMaxElement((int[])algoArgs[0]);}",	
		};
		return classConfig;
	}
// temp function, TODO : use a real json file
	public static ArrayList<Object[]> getInputDataFromJson() {
		ArrayList<Object[]> inputsData = new ArrayList<Object[]>();
		inputsData.add(new Object[]{(new int[]{190, 32, 250, 70, 60, 63, 21, 288, 12, 6, 9, 6, 45, 133, 16}), 15});
		inputsData.add(new Object[]{(new int[]{190, 32, 250, 70, 60, 63, 21, 288, 12, 6, 9, 6, 45, 133, 16}), 15});
		inputsData.add(new Object[]{(new int[]{190, 32, 250, 70, 60, 63, 21, 288, 12, 6, 9, 6, 45, 133, 16}), 15});
		inputsData.add(new Object[]{(new int[]{190, 32, 250, 70, 60, 63, 21, 288, 12, 6, 9, 6, 45, 133, 16}), 15});


		return inputsData ;
	}
	
}
