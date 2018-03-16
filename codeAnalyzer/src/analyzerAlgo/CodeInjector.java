package analyzerAlgo;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import analyzedClass.analyzedMethod.MethodInfo;
import analyzedClass.analyzedMethod.MethodOperations;
import analyzedClass.analyzedMethod.MethodsDataSet;

public class CodeInjector {

	// Regular expressions for blocked / unblocked for loop:
	protected final static String ForStartExp = "^(\\t|\\s)*for(\\s\\().*(\\{)";
	protected final static Pattern containsEmptyScopeLoop = Pattern.compile("^(\\t|\\s)*(for|while)( \\().*(\\{\\})");

	
	// const names of elements in the code
	private final static String injectedCounterVarName = "analyzerCounters";

	/**
	 * main loop that scan the whole code file assuming it as been reformatted
	 * and handles the injections.
	 * 
	 * @param methodsData
	 *            the MethodsData object for the 
	 * @return amount of counters injections
	 */
	public static int mainScanner(MethodsDataSet methodsDataSet) {
		int injectionCount = 0;
		for(Entry<String, MethodInfo> entry : methodsDataSet.getEntrySet()) {
			injectionCount += methodInjector(entry.getValue());
		}
		return injectionCount;
	}

	
	/*
	 * ... inner methods
	 */

	private static int methodInjector(MethodInfo methodInfo) {
		ArrayList<StringBuilder> methodCode = methodInfo.methodCodeLines;
		ArrayList<Integer[]> orderPairs = methodInfo.getDependentLoopStatementIndexPairs();

		//		ArrayList<Integer[]> mostInnerPairs = new ArrayList<Integer[]>();
		//		ArrayList<Integer[]> outerPairs = new ArrayList<Integer[]>();
		//		getMostInnerPairs(orderPairs, mostInnerPairs, outerPairs);

		int injCount = 0; // used for the injection code line
		int offset = 0;


		for(int i = 0; i < orderPairs.size(); i++) {
			
			// handle a case of single loop in a method
			// and always refer to the last for loop as a most inner one
			if(orderPairs.size()-1 == i) {
				Integer[] pair = orderPairs.get(i);
				int startScope = pair[0];
				injectInnerLoop(methodCode, startScope + 1 + offset, injCount); 
				injCount++;
			} else {
				Integer[] curPair = orderPairs.get(i);
				Integer[] nextPair = orderPairs.get(i+1);

				int curStartScope = curPair[0];
				int curEndScope = curPair[1];
				int nextStartScope = nextPair[0];
				int nextEndScope = nextPair[1];

				if(curEndScope < nextEndScope) { // the loop is 'most inner'
					injectInnerLoop(methodCode, curStartScope + 1 + offset, injCount);
					offset = (offset*2 + 1); // TODO: explain why 
//					injCount++;
				} else { // the loop is outer loop
					injectOutterLoopEntryPoint(methodCode, curStartScope + 1 + offset, injCount); // shift all the code below one index down
					offset++;
					injectOutterLoopExitPoint(methodCode, curEndScope + offset, injCount); // shift all the code below one index down
					injCount++;
				}

				String line = methodCode.get(curStartScope + offset).toString();
				if (line.matches(ForStartExp)) { // might be unnecessary check
					System.out.println(line.toString());
					injectCounter(methodCode, curEndScope + offset, injCount+1); // shift all the code below one index down
					offset++;
				} else if (line.matches(containsEmptyScopeLoop.pattern())) {
					injectEmptyScopeLoop(methodCode, curEndScope + offset, injCount+1); // shift all the code below two index down
					offset += 2;
				}
				injCount++;
			}
		}
		return injCount;
	}

	private static void injectInnerLoop(ArrayList<StringBuilder> methodCode, int startLine, int injCount) {
		StringBuilder injectionCode = 
				new StringBuilder(injectedCounterVarName+".incCount("+(injCount-1)+");\n" +
						injectedCounterVarName+".setEntryTrue("+(injCount-1)+");");

		methodCode.add(startLine, injectionCode);
	}

	private static void injectOutterLoopEntryPoint(ArrayList<StringBuilder> methodCode, int startLine, int injCount) {
		StringBuilder injectionCode = 
				new StringBuilder(injectedCounterVarName+".setEntryTrue("+(injCount)+");\n"+
						injectedCounterVarName+".setEntryFalse("+(injCount+1)+");");
		methodCode.add(startLine, injectionCode);
	}

	private static void injectOutterLoopExitPoint(ArrayList<StringBuilder> methodCode, int startLine, int injCount) {
		StringBuilder injectionCode = 
				new StringBuilder("if(!"+injectedCounterVarName+".getEntryValue("+(injCount+1)+")){\n"+
						injectedCounterVarName+".incCount("+(injCount)+");\n}\n");
		methodCode.add(startLine, injectionCode);
	}

	/**
	 * Handle the case of empty scope for loop separate the pair of curly braces
	 * to separate lines inject the code between the two curly braces
	 */
	private static void injectEmptyScopeLoop(ArrayList<StringBuilder> codeBuffer, int location, int injCount) {
		StringBuilder injectionCodeEndLoop = new StringBuilder(injectedCounterVarName + ".incCount(" + (injCount - 1) + ");");
		StringBuilder line = codeBuffer.get(location);
		line.setLength(line.length() - 1);
		codeBuffer.add(location + 1, injectionCodeEndLoop);
		codeBuffer.add(location + 2, new StringBuilder("}"));

	}

	
	
	
	
	
	
	
	
	
	/**
	 * @param allPeirs 
	 * 				all the index-pairs of found for loops (dependent).
	 * @param mostInnerPeirs 
	 * 				empty list, initialized with the most inner (with no loops in them) index-pairs loops. 
	 * @param outerPeirs 
	 * 				empty list, initialized with index-pairs for loops that contains for loops in them.
	 * 
	 * */
	@SuppressWarnings("unused")
	private static void getMostInnerPairs(
			ArrayList<Integer[]> allPeirs, ArrayList<Integer[]> mostInnerPeirs, ArrayList<Integer[]> outerPeirs) {
		for(int i = 0; i < allPeirs.size()-1; i++) {
			int n = allPeirs.get(i)[1];
			if(n < allPeirs.get(i+1)[1]) {
				mostInnerPeirs.add(allPeirs.get(i));
			}
			else {
				outerPeirs.add(allPeirs.get(i));			
			}
		} 
		mostInnerPeirs.add(allPeirs.get(allPeirs.size()-1));
	}
	
	/**
	 * Recursion, scan the 'currentScope' for line match the regEx of for loop
	 * if find line that match - find the end of that loop scope, and call
	 * findMostInnerFor again with the current index and the index of the end of
	 * the new found for loop scope, when scan finished with no match (we are in
	 * the most inner for loop) we increment the injCount and inject the code
	 * line
	 */
	@SuppressWarnings("unused")
	private static int findMostInnerFor(ArrayList<StringBuilder> currentScope, int startLine, int endLine, int injCount) {
		boolean foundFor = false;
		for (int i = startLine; i < endLine; i++) {
			String line = currentScope.get(i).toString();
			if (line.matches(ForStartExp)) {
				foundFor = true;
				int endScope = MethodOperations.findEndOfScope(currentScope, i);
				injCount = findMostInnerFor(currentScope, i + 1, endScope, injCount);
				i = endScope;
			} else if (line.matches(containsEmptyScopeLoop.pattern())) {
				foundFor = true;
				injCount++;
				injectEmptyScopeLoop(currentScope, i, injCount);
				i += 2;
			}
		}
		if (!foundFor) {
			injCount++;
			injectCounter(currentScope, endLine, injCount);
		}
		return injCount;
	}

	/**
	 * inject the code line to 'currentScope'
	 */
	private static void injectCounter(ArrayList<StringBuilder> currentScope, int endLine, int injCount) {
		StringBuilder injectionCodeEndLoop = new StringBuilder(injectedCounterVarName + ".incCount(" + (injCount - 1) + ");");
		currentScope.add(endLine, injectionCodeEndLoop);
	}

	
}
