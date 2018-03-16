package analyzedClass.analyzedMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MethodAnalyzer {

	// note : all the patterns will match non empty scop loops
	private static Pattern containsForLoopStatement =  Pattern.compile("for \\((.*);(.*);(.*)\\).*\\{") ;
	private static int[] forLoopConditionLocations = {1,2};
	private static Pattern containsWhileLoopStatement = Pattern.compile("while \\((.*)\\).*\\{") ;
	private static int[] whileLoopConditionLocations = {1};
	private static Pattern containsForEachLoopStatement =  Pattern.compile("for \\((.*):(.*)\\).*\\{") ;
	private static int[] forEachLoopConditionLocations = {2};


	public MethodAnalyzer() {}

	/**
	 * finds the index of all the dependent for loop statements and the index of
	 * there ending lines.
	 * 
	 * @param method
	 *            instance of the referred method.s
	 * @param methodCode
	 *            list of the method's code.
	 * @return list of ordered pairs , first contains index of the dependent for
	 *         statements, the second contains the index of the end.
	 */
	public static ArrayList<Integer[]> findDependentLoopStatements(Method method, ArrayList<StringBuilder> methodCode) {

		// scan all the method's code lines

		ArrayList<String> paramsNames = MethodAnalyzer.getParametersNames(method);
		ArrayList<Integer[]> orderedPairdependentLoopLines = new ArrayList<Integer[]>();
		int lineIndex = 0;

		for (StringBuilder line : methodCode) {
			// step 1 - update the dependencies parameters
			MethodAnalyzer.scanForMoreDependenciesParameters(line, paramsNames);

			// step 2 - check for any loop satement

			// =========== regular for loop
			Matcher forStatementMatcher = isCodeLineMatchePattern(containsForLoopStatement, line);
			if (forStatementMatcher != null) {
				if (MethodAnalyzer.isStatementDependent(forStatementMatcher, forLoopConditionLocations, paramsNames)) {
					int endScope = MethodOperations.findEndOfScope(methodCode, lineIndex);
					orderedPairdependentLoopLines.add(new Integer[]{lineIndex,endScope});
				}			
				lineIndex++;
				continue; // next iteretion
			} 

			// =========== while loop
			Matcher whileStatementMatcher = isCodeLineMatchePattern(containsWhileLoopStatement, line);
			if (whileStatementMatcher != null) {
				if (MethodAnalyzer.isStatementDependent(whileStatementMatcher, whileLoopConditionLocations, paramsNames)) {
					int endScope = MethodOperations.findEndOfScope(methodCode, lineIndex);
					orderedPairdependentLoopLines.add(new Integer[]{lineIndex,endScope});
				}
				lineIndex++;
				continue; // next iteretion
			}

			// =========== forEach loop
			Matcher forEachStatementMatcher = isCodeLineMatchePattern(containsForEachLoopStatement, line);
			if (forEachStatementMatcher != null) {
				if (MethodAnalyzer.isStatementDependent(forEachStatementMatcher, forEachLoopConditionLocations, paramsNames)) {
					int endScope = MethodOperations.findEndOfScope(methodCode, lineIndex);
					orderedPairdependentLoopLines.add(new Integer[]{lineIndex,endScope});
				}
			}
			lineIndex++;
		}
		return orderedPairdependentLoopLines;
	}

	/**	Scan the current line and check if there is new assignments of method's parameters,
	 * if there is add then into ParamsNames ArrayList   
	 * 
	 * @param line current line that we check  
	 * @param paramsNames ArrayList of parameters names  
	 */
	public static void scanForMoreDependenciesParameters(StringBuilder line,ArrayList<String> paramsNames){
		//TODO :: Handle alignment on comments
		// TODO :: need to handle: ternary if , for
		//if(!(line.toString().matches("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/"))){//not work
		if(line.toString().matches("^((\\t|\\s)*[^for (](.*)(\\s\\=\\s)(.*));$")) {
			Boolean isDepend = false;
			// remove all line's operators
			String str = MethodAnalyzer.removeOperators(line.toString());

			String [] lineArr = str.toString().split(" ");

			String leftSide = "";
			String [] rightSide = null;
			StringBuilder rightS = new StringBuilder(); 
			// Extract the variable from the string
			for (int i = 0; i < lineArr.length; i++) {
				if(lineArr[i].equals("=")) {
					leftSide = lineArr[i - 1];
					//rightSide = new String[lineArr.length - (i + 1)];
					//int k = 0;
					for (int j = i +1; j < lineArr.length; j++) {
						//filter whitespace
						if(!lineArr[j].equals("")) {
							rightS.append(lineArr[j]);
							rightS.append(",");	
						}
						//rightS.append(" ");
						//rightSide[k++] = lineArr[j];
					}
					break;
				}

			}
			String right = MethodAnalyzer.removeOperators(rightS.toString());
			rightSide = right.split(" ");
			// check if right side variables appear in paramsName array
			for (String string : paramsNames) {
				String [] paramOptions = {string,string + ".size", string + ".size()", string + ".length", string + ".length()"};
				for (int i = 0; i < rightSide.length; i++) {
					for (int j = 0; j < paramOptions.length; j++) {
						if(rightSide[i].equals(paramOptions[j])) {
							isDepend = true;
							//check if left side not exist in paramsName array
							if(!paramsNames.contains(leftSide)) {
								paramsNames.add(leftSide);
								return;
							}
						}
					}
				}
			}
			// the assignment not contains dependencies and left side in paramsNames array
			if(paramsNames.contains(leftSide) && !(isDepend == true)) {
				paramsNames.remove(leftSide);
				return;
			}
		}

		//}

	}

	/**
	 * replace all possible operators into whitespace
	 * 
	 * @param line current line that being analyze 
	 * @return line without any operator
	 */
	public static String removeOperators(String line) {
		String str = "";
		try {
			str = line.toString().replaceAll(";", " ");
			str = str.replaceAll("[,]"," ");
			str = str.replaceAll("[+]"," ");
			str = str.replaceAll("[-]"," ");
			str = str.replaceAll("[:]", " ");
			str = str.replaceAll("[(]", " ");
			str = str.replaceAll("[)]", " ");
		}catch(Exception e) {

		}
		return str;
	}

	/**
	 * determine if the for code line effected by any of the parameters in
	 * paramsNames list
	 * 
	 * @param line
	 *            for statement code line.
	 * @param paramsNames
	 *            list of the parameters names the method receive.
	 * 
	 * @return true if the for code line effected by any of the parameters in
	 *         paramsNames list, else false.
	 */
	//	public static boolean isForLoopDependent(Matcher forStatementMatcher, ArrayList<String> paramsNames) {
	//		boolean isDependent = false;
	////		Matcher forLoopStatementContantMatcher = forLoopStatementContant.matcher(line);
	////		Matcher forEachLoopStatementContantMatcher = forEachLoopStatementContant.matcher(line);
	//
	//		// check if the method contains the code line receives any parameters
	//		if (paramsNames.size() > 0) {
	//			// same for - reqular for and forEach 
	//			String condition = forStatementMatcher.group(2);
	//			if (MethodAnalyzer.isContainsParam(condition, paramsNames)) {
	//				isDependent = true;
	//			}
	//		}
	//		/*
	//			StringBuilder lineCopy = new StringBuilder(line);
	//			int startInnerExpretion = lineCopy.indexOf("for (");
	//			 if finds "for (" substring
	//			if (forLoopStatementContantMatcher.find()) {
	//				// override lineCopy value with the substring from "for (" to
	//				// the end
	////				lineCopy.replace(0, lineCopy.length(), lineCopy.substring(startInnerExpretion));
	////				String statment = lineCopy.toString();
	//				// if classic for loop statement
	////				if (statment.matches("^.*(;).*(;).*$")) {
	//					// statement is "int i = 0; i < var; i++" ==>
	//					// parts[0]="int i = 0" ,parts[1]="i < var" ,parts[2]="i++"
	////					String[] parts = statment.split("(\\s)*;(\\s)*");
	//				String condition = forLoopStatementContantMatcher.group(2);
	//					if (MethodAnalyzer.isContainsParam(condition, paramsNames)) {
	//						isDependent = true;
	//					}
	//				}
	//				// if for each loop statement
	//				else if (forEachLoopStatementContantMatcher.find()) {
	//					// statement is "String str : stringArray" ==>
	//					// parts[0] = "String str" , parts[1]= "stringArray"
	////					String[] parts = statment.split("(\\s)*:(\\s)*");
	//					String condition = forEachLoopStatementContantMatcher.group(2);
	//					if (MethodAnalyzer.isContainsParam(condition, paramsNames)) {
	//						isDependent = true;
	//					}
	//				}
	//			}
	//			*/
	//		return isDependent;
	//	}


	private static boolean isStatementDependent(Matcher statementMatcher,int[] groupNumbers, ArrayList<String> paramsNames) {
		boolean isDependent = false;
		// check if the method contains the code line receives any parameters
		if (paramsNames.size() > 0) {
			for(int n : groupNumbers) {
				String condition = statementMatcher.group(n);
				if (MethodAnalyzer.isContainsParam(condition, paramsNames)) {
					isDependent = true;
					break;
				}
			}

		}
		return isDependent;
	}

	//	private static boolean isWhileLoopDependent(Matcher whileStatementMatcher, ArrayList<String> paramsNames) {
	//		boolean isDependent = false;
	//		// check if the method contains the code line receives any parameters
	//		if (paramsNames.size() > 0) {
	//			// same for - reqular for and forEach 
	//			String condition = whileStatementMatcher.group(1);
	//			if (MethodAnalyzer.isContainsParam(condition, paramsNames)) {
	//				isDependent = true;
	//			}
	//		}
	//		return isDependent;
	//	}

	/*
	 * ... 
	 */

	private static boolean isContainsParam(String line, ArrayList<String> paramsNames) {
		boolean result = false;
		for (String param : paramsNames) {
			// space - param - ( "[" or "." or "(" - anything ) or none
			if (line.matches(".*(\\s)?" + param + "(((\\[)|(\\.)|(\\()).*)?")) {
				result = true;
			}
		}
		return result;
	}

	private static ArrayList<String> getParametersNames(Method method) {
		ArrayList<String> paramsNames = new ArrayList<String>();
		Parameter[] parameters = method.getParameters();
		for (Parameter p : parameters) {
			paramsNames.add(p.getName());
		}

		return paramsNames;
	}


	// used to match any code line to for / forEach / while statement patterns 
	private static Matcher isCodeLineMatchePattern(Pattern patterm, StringBuilder line) {
		Matcher matcher = patterm.matcher(line);
		if(matcher.find()) {
			return matcher;
		} else {
			return null;
		}
	}




}
