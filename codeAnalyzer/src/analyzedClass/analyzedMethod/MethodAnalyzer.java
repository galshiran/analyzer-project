package analyzedClass.analyzedMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class MethodAnalyzer {

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
	public static ArrayList<Integer[]> findDependentForStatements(Method method, ArrayList<StringBuilder> methodCode) {

		ArrayList<String> paramsNames = MethodAnalyzer.getParametersNames(method);
		ArrayList<Integer[]> orderedPairdependentForLoopLines = new ArrayList<Integer[]>();
		int lineIndex = 0;
		for (StringBuilder line : methodCode) {
			MethodAnalyzer.scanForMoreDependenciesParameters(line, paramsNames);
			if (MethodAnalyzer.isCodeLineIsForStatement(line)) {
				if (MethodAnalyzer.isForLoopDependent(line, paramsNames)) {
					int endScope = MethodOperations.findEndOfScope(methodCode, lineIndex);
					orderedPairdependentForLoopLines.add(new Integer[]{lineIndex,endScope});
				}
			}
			lineIndex++;
		}
		return orderedPairdependentForLoopLines;
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
	public static boolean isForLoopDependent(StringBuilder line, ArrayList<String> paramsNames) {
		boolean isDependent = false;
		// check if the method contains the code line receives any parameters
		if (paramsNames.size() > 0) {

			StringBuilder lineCopy = new StringBuilder(line);
			int startInnerExpretion = lineCopy.indexOf("for (");
			// if finds "for (" substring
			if (startInnerExpretion != -1) {
				// override lineCopy value with the substring from "for (" to
				// the end
				lineCopy.replace(0, lineCopy.length(), lineCopy.substring(startInnerExpretion));
				String statment = lineCopy.toString();
				// if classic for loop statement
				if (statment.matches("^.*(;).*(;).*$")) {
					// statement is "int i = 0; i < var; i++" ==>
					// parts[0]="int i = 0" ,parts[1]="i < var" ,parts[2]="i++"
					String[] parts = statment.split("(\\s)*;(\\s)*");
					if (MethodAnalyzer.isContainsParam(parts[1], paramsNames)) {
						isDependent = true;
					}
				}
				// if for each loop statement
				else if (statment.matches("^.*(:).*$")) {
					// statement is "String str : stringArray" ==>
					// parts[0] = "String str" , parts[1]= "stringArray"
					String[] parts = statment.split("(\\s)*:(\\s)*");
					if (MethodAnalyzer.isContainsParam(parts[1], paramsNames)) {
						isDependent = true;
					}
				}
			}
		}
		return isDependent;
	}

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

	private static boolean isCodeLineIsForStatement(StringBuilder line) {
		return line.toString().matches("^(\\s|\\t)*(for \\()(.*)$");
	}

}
