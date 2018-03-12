package analyzerAbs;

import java.math.BigDecimal;
import java.util.ArrayList;

public class AnalyzerCounters {

	public ArrayList<BigDecimal> __counters = null;

	public AnalyzerCounters() {
		__counters = new ArrayList<BigDecimal>();
	}

	/**
	 * @param index
	 *            index of the counter in the '__counters' list to increment. if
	 *            the index >= from the size of the list, more elements will be
	 *            added to the list until the wanted index will be initialized.
	 */
	public void incCount(int index) {

			while (__counters.size() <= index) {
				__counters.add(BigDecimal.valueOf(0));
			}

			try {
				__counters.set(index, __counters.get(index).add(BigDecimal.valueOf(1)));
			} catch (IndexOutOfBoundsException e) {
				// TODO: write the error to the log file
				e.printStackTrace();
			}
		
	}

	public void printCounters() {
		System.out.println(__counters);
	}
}