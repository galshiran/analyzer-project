package complexityAssessmentTest;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import complexityAssessment.AnalyzerSample;
import complexityAssessment.ComplexityAssessmentAlgo;
import complexityAssessment.NPolynom;

/*
 * acceptable polynoms ::
 * 0 = log(n) 
 * 1 = sqrt(n)
 * 2 = n
 * 3 = nlog(n)
 * 4 = n^2
 * 5 = n^3
 * */

public class ComplexityAssessmentTest {

	@Test
	public void test1() {
		int[] expecteds, actuals;
		MathContext mathContext = new MathContext(100);
		ArrayList<AnalyzerSample> analyzerSamples = new ArrayList<AnalyzerSample>();
		

		// ============= f(n) = (2 * n^3) + (5 * n*log(n)) + 50
		
		analyzerSamples.add(new AnalyzerSample(10, new BigDecimal[] { 
				new BigDecimal("2000", mathContext),
				new BigDecimal("50", mathContext), 
				new BigDecimal("50", mathContext) }));
		analyzerSamples.add(new AnalyzerSample(100, new BigDecimal[] { 
				new BigDecimal("2000030", mathContext), // deviation in +30
				new BigDecimal("1000", mathContext), 
				new BigDecimal("50", mathContext) }));
		analyzerSamples.add(new AnalyzerSample(1000, new BigDecimal[] { 
				new BigDecimal("2000000050", mathContext), // deviation in +50
				new BigDecimal("15000", mathContext), 
				new BigDecimal("50", mathContext) }));
		analyzerSamples.add(new AnalyzerSample(10000, new BigDecimal[] { 
				new BigDecimal("2000000000030", mathContext), // deviation in +30
				new BigDecimal("150000", mathContext), 
				new BigDecimal("50", mathContext) }));
		
		expecteds = new int[] { NPolynom.Ntimes3, NPolynom.Ntimes3, NPolynom.Ntimes3 };
		actuals = ComplexityAssessmentAlgo.findBigOInSample(analyzerSamples);
		
		System.out.println(Arrays.toString(expecteds) + " " + Arrays.toString(actuals));
		Assert.assertArrayEquals(expecteds, actuals);

	}

	@Test
	public void test2() {

		int[] expecteds, actuals;
		MathContext mathContext = new MathContext(100);
		ArrayList<AnalyzerSample> analyzerSamples = new ArrayList<AnalyzerSample>();
		
		// ============= f(n) = (7 * n * log(n)) + (9*log(n)) + (3 * n)
		
		analyzerSamples.add(new AnalyzerSample(10, new BigDecimal[] { 
				new BigDecimal("70", mathContext),
				new BigDecimal("9", mathContext), 
				new BigDecimal("30", mathContext) }));
		analyzerSamples.add(new AnalyzerSample(100, new BigDecimal[] { 
				new BigDecimal("1401", mathContext), // deviation in +1
				new BigDecimal("18", mathContext), 
				new BigDecimal("300", mathContext) }));
		analyzerSamples.add(new AnalyzerSample(1000, new BigDecimal[] { 
				new BigDecimal("21010", mathContext), // deviation in +10
				new BigDecimal("27", mathContext), 
				new BigDecimal("3002", mathContext) // deviation in +2
		}));
		
		expecteds = new int[] { NPolynom.NLogN, NPolynom.NLogN };
		actuals = ComplexityAssessmentAlgo.findBigOInSample(analyzerSamples);
		
		System.out.println(Arrays.toString(expecteds) + " " + Arrays.toString(actuals));
		Assert.assertArrayEquals(expecteds, actuals);
	}

	@Test
	public void test3() {
		int[] expecteds, actuals;
		MathContext mathContext = new MathContext(100);
		ArrayList<AnalyzerSample> analyzerSamples = new ArrayList<AnalyzerSample>();

		// ============= f(n) = (3*sqrt(n)) + (2*log(n))

		analyzerSamples.add(new AnalyzerSample(10, new BigDecimal[] { 
				BigDecimal.valueOf(3 * java.lang.Math.sqrt(10)), 
				BigDecimal.valueOf(2 * 1) }));
		analyzerSamples.add(new AnalyzerSample(100, new BigDecimal[] { 
				BigDecimal.valueOf(3 * java.lang.Math.sqrt(100) + 1), // deviation in +1
				BigDecimal.valueOf(2 * 2) }));
		analyzerSamples.add(new AnalyzerSample(1000, new BigDecimal[] { 
				BigDecimal.valueOf(3 * java.lang.Math.sqrt(1000) + 1), // deviation in +1
				BigDecimal.valueOf(2 * 3) }));
		
		expecteds = new int[] { NPolynom.SqrtN, NPolynom.SqrtN };
		actuals = ComplexityAssessmentAlgo.findBigOInSample(analyzerSamples);
		
		System.out.println(Arrays.toString(expecteds) + " " + Arrays.toString(actuals));
		Assert.assertArrayEquals(expecteds, actuals);

	}
}
