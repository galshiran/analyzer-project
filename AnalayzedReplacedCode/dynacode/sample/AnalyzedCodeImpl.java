package sample;
import analyzerAbs.AnalyzerCounters;
public class AnalyzedCodeImpl implements CodeAnalyze {
public AnalyzerCounters analyzerCounters  = new AnalyzerCounters();
public AnalyzerCounters getAnalyzerCounters() { return analyzerCounters;}
public void runAlgorithm(Object[] algoArgs) { findMaxElement((int[])algoArgs[0]);}
  public int findMaxElement(int[] arr) {
    // bubble sorting
    int temp = 0;
    for (int i = 0; i < arr.length; i++) {
analyzerCounters.setEntryTrue(0);
analyzerCounters.setEntryFalse(1);
      for (int j = 1; j < arr.length - i; j++) {
analyzerCounters.incCount(1);
analyzerCounters.setEntryTrue(1);
        if (arr[j - 1] > arr[j]) {
          // swap elements
          temp = arr[j - 1];
          arr[j - 1] = arr[j];
          arr[j] = temp;
        }
      }
if(!analyzerCounters.getEntryValue(1)){
analyzerCounters.incCount(0);
}

    }
    return arr[arr.length - 1];
  }
}
