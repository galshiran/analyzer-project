package complexityAssessment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

import ch.obermuhlner.math.big.BigDecimalMath;

public class ComplexityAssessmentAlgo {

	// TODO: if considering if statement is the code we need to include
	// Approximation function.

	/*
	 * acceptable polynoms ::
	 * 0 = log(n) 
	 * 1 = sqrt(n)
	 * 2 = n
	 * 3 = nlog(n)
	 * 4 = n^2
	 * 5 = n^3
	 * */

	static double div = 0.5;

	//public static void main(String[] args) {}

	public static int[] findBigOInSample(ArrayList<AnalyzerSample> analyzerSamples) {
		int[] maxOfSamples = new int[analyzerSamples.size() - 1];

		for (int i = analyzerSamples.size() - 1; i > 0; i--) {
			int mostDominent = getMax(compliteSampleToPolynomOfN(analyzerSamples.get(i), analyzerSamples.get(i - 1)));
			maxOfSamples[i - 1] = mostDominent;
		}
		return maxOfSamples;
	}

	public static int[] compliteSampleToPolynomOfN(AnalyzerSample analyzerSampleA, AnalyzerSample analyzerSampleB) {
		int[] res = new int[analyzerSampleA.iterations.length];
		for (int i = 0; i < res.length; i++) {
			BigDecimal sampleCellA = analyzerSampleA.iterations[i];
			BigDecimal sampleCellB = analyzerSampleB.iterations[i];
			int r = findPolynomOfN(analyzerSampleA.inputSize, analyzerSampleB.inputSize, sampleCellA, sampleCellB);
			res[i] = r;
		}
		return res;

	}

	public static int findPolynomOfN(BigDecimal inputSizeA, BigDecimal inputSizeB, BigDecimal sampleCellA, BigDecimal sampleCellB) {

		int res = isLogN(inputSizeA, inputSizeB, sampleCellA, sampleCellB);
		res = res != -1 ? res : isSqrtN(inputSizeA, inputSizeB, sampleCellA, sampleCellB);
		res = res != -1 ? res : isN(inputSizeA, inputSizeB, sampleCellA, sampleCellB);
		res = res != -1 ? res : isNLogN(inputSizeA, inputSizeB, sampleCellA, sampleCellB);
		res = res != -1 ? res : isNtimes2(inputSizeA, inputSizeB, sampleCellA, sampleCellB);
		res = res != -1 ? res : isNtimes3(inputSizeA, inputSizeB, sampleCellA, sampleCellB);

		return res;
	}

	/*
	 * ... private
	 */

	private static int getMax(int[] arr) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < arr.length; i++) {
			if (max < arr[i]) {
				max = arr[i];
			}
		}
		return max;
	}

	private static int isLogN(BigDecimal inputSizeA, BigDecimal inputSizeB, BigDecimal sampleCellA, BigDecimal sampleCellB) {
		// log(n)
		MathContext mathContext = new MathContext(50);
		BigDecimal inputRation = (BigDecimalMath.log10(inputSizeA, mathContext))
				.divide(BigDecimalMath.log10(inputSizeB, mathContext), mathContext);
		
		BigDecimal sampleRation = sampleCellA.divide(sampleCellB, mathContext);
		return compareApproximatedNumbers(inputRation, sampleRation, div) ? 0 : -1;
	}

	private static int isSqrtN(BigDecimal inputSizeA, BigDecimal inputSizeB, BigDecimal sampleCellA, BigDecimal sampleCellB) {
		// sqrt(n)
		MathContext mathContext = new MathContext(50);
		BigDecimal inputRation = (BigDecimalMath.sqrt(inputSizeA, mathContext))
				.divide(BigDecimalMath.sqrt(inputSizeB, mathContext), mathContext);
		BigDecimal sampleRation = sampleCellA.divide(sampleCellB, mathContext);
		return compareApproximatedNumbers(inputRation, sampleRation, div) ? 1 : -1;
	}

	private static int isN(BigDecimal inputSizeA, BigDecimal inputSizeB, BigDecimal sampleCellA, BigDecimal sampleCellB) {
		// n
		MathContext mathContext = new MathContext(50);
		BigDecimal inputRation = inputSizeA.divide(inputSizeB);
		BigDecimal sampleRation = sampleCellA.divide(sampleCellB, mathContext);
		return compareApproximatedNumbers(inputRation, sampleRation, div) ? 2 : -1;
	}

	private static int isNLogN(BigDecimal inputSizeA, BigDecimal inputSizeB, BigDecimal sampleCellA, BigDecimal sampleCellB) {
		// n*log(n)
		MathContext mathContext = new MathContext(50);
		
		BigDecimal lognInputA = BigDecimalMath.log10(inputSizeA, mathContext);
		BigDecimal lognInputB = BigDecimalMath.log10(inputSizeB, mathContext);
		BigDecimal nlognInputA = inputSizeA.multiply(lognInputA, mathContext);
		BigDecimal nlognInputB = inputSizeB.multiply(lognInputB, mathContext);
		BigDecimal inputRation = nlognInputA.divide(nlognInputB, mathContext);
		BigDecimal sampleRation = sampleCellA.divide(sampleCellB, mathContext);
		return compareApproximatedNumbers(inputRation, sampleRation, div) ? 3 : -1;
	}

	private static int isNtimes2(BigDecimal inputSizeA, BigDecimal inputSizeB, BigDecimal sampleCellA, BigDecimal sampleCellB) {
		// n^2
		MathContext mathContext = new MathContext(50);
		BigDecimal n2InputA = inputSizeA.multiply(inputSizeA, mathContext);
		BigDecimal n2InputB = inputSizeB.multiply(inputSizeB, mathContext);
		BigDecimal inputRation = n2InputA.divide(n2InputB, mathContext);
		BigDecimal sampleRation = sampleCellA.divide(sampleCellB, mathContext);
		return compareApproximatedNumbers(inputRation, sampleRation, div) ? 4 : -1;
	}

	private static int isNtimes3(BigDecimal inputSizeA, BigDecimal inputSizeB, BigDecimal sampleCellA, BigDecimal sampleCellB) {
		// n^3
		MathContext mathContext = new MathContext(50);
		BigDecimal inputRation = ((inputSizeA.multiply(inputSizeA).multiply(inputSizeA))
				.divide((inputSizeB).multiply(inputSizeB).multiply(inputSizeB)));
		BigDecimal sampleRation = (sampleCellA).divide(sampleCellB, mathContext);
		return compareApproximatedNumbers(inputRation, sampleRation, div) ? 5 : -1;
	}

	/**
	 * approximate comparison between two numbers.
	 * 
	 * @param a
	 *            first number
	 * @param b
	 *            second number
	 * @param deviation
	 *            the maximum difference between two number that we consider
	 *            equals.
	 * @return if |a - b| < deviation
	 * 
	 *         exepmle : givan :: a = 230.5002 , b = 230.5004 , deviation =
	 *         0.005 . |a - b| = 0.0002 < 0.005 ==> a = b
	 */
	private static boolean compareApproximatedNumbers(BigDecimal a, BigDecimal b, double deviation) {
		BigDecimal difference = (a.subtract(b)).abs();
		return difference.doubleValue() < deviation;
	}

}
