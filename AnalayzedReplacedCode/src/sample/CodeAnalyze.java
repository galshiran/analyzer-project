package sample;

import analyzerAbs.AnalyzerCounters;

public interface CodeAnalyze {
	
	public AnalyzerCounters getAnalyzerCounters();
	public void runAlgorithm(Object[] algoArgs);
	
}
