package complexityAssessment;

import java.math.BigDecimal;

public class AnalyzerSample {
	
		public BigDecimal inputSize;
		public BigDecimal[] iterations;
		public AnalyzerSample(BigDecimal inputSize, BigDecimal[] iterations) {
			this.inputSize = inputSize;
			this.iterations = iterations;
		}
		public AnalyzerSample(int inputSize, BigDecimal[] iterations) {
			this(BigDecimal.valueOf(inputSize), iterations);
		}
	
}
