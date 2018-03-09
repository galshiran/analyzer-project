package sample;
import analyzerAbs.*;
public class NewTesterMethod implements CodeAnalyze {
public AnalyzerCounters analyzerCounters  = new AnalyzerCounters();
  public int testerMethodA(int[] arr, int num) {
    int indextOfElement = -1;
    for (int i = 0; i < 20; i++) {}
    for (int i = 0; i < arr.length; i++) {
      for (int k = 0; k < arr.length; k++) {
        for (int l = 0; l < 100; l++) {}
        for (int l = 0; l < arr.length; l++) {
          // System.out.print("a");
analyzerCounters.incCount(0);
        }
      }
      if (arr[i] == num) {
        indextOfElement = i;
      }
      for (int k = 0; k < 100; k++) {
        // System.out.print(num);
        for (int j = i; j < arr.length; j++) {
          // System.out.print(j);
          for (int l = 0; l < 40; l++) {}
analyzerCounters.incCount(1);
        }
      }
    }
    return indextOfElement;
  }
}
