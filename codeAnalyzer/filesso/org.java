package studentAlgo;

public class MaxElementMatrix extends AnalyzerAbs {

	public MaxElementMatrix(Integer n) {
		super(n);
	}
	
	public int findMaxElementMatrix(int[][] mat) {
		int max = Integer.MIN_VALUE;

		for(int i = 0; i < mat.length; i++) {			
			for(int j = 0; j < (mat[i]).length; j++) {
				if (max < mat[i][j]) {
					max = mat[i][j];
				}
			}	
		}
		return max;
	}
	
}
