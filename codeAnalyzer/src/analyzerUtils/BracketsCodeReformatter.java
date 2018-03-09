package analyzerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BracketsCodeReformatter {
	static Pattern onlyForLoopDeclaration = Pattern.compile("^(\\s|\\t)*for(\\s)*\\((.*?)\\)");
	static Pattern onlyIfStatementDeclaration = Pattern.compile("^(\\s|\\t)*if(\\s)*\\((.*?)\\)");
	static Pattern inlineForLoopWithoutBrackets = Pattern.compile("^(\\s|\\t)*for(\\s)*\\((.*?)\\)(\\s)*+[^{}]+");
	static Pattern inlineIfStatementWithoutBrackets = Pattern.compile("^(\\s|\\t)*if(\\s)*\\((.*?)\\)(\\s)*+[^{}]+");
	static Pattern newlineForLoopWithoutBrackets = Pattern.compile("^(\\s|\\t)*for(\\s)*\\((.*?)\\)(\\s|\\t)*$");
	static Pattern newlineIfStatementWithoutBrackets = Pattern.compile("^(\\s|\\t)*if(\\s)*\\((.*?)\\)(\\s|\\t)*$");
	static Pattern commentForLoopWithoutBrackets = Pattern.compile("^(\\s|\\t)*for(\\s)*\\((.*?)\\)(\\s)*+(//)+");
	static Pattern commentIfStatementWithoutBrackets = Pattern.compile("^(\\s|\\t)*if(\\s)*\\((.*?)\\)(\\s)*+(//)+");

	/**
	 * 
	 * @param list
	 */
	
	private static void checkInlineForLoopDecleration(ArrayList<String> list) {
		int listSize = list.size();
		int currentCheckedIndex = 0;
		String currentCheckedLine;
		Matcher inlineLoop;
		boolean done;

		for (; currentCheckedIndex < listSize; currentCheckedIndex++) {
			currentCheckedLine = list.get(currentCheckedIndex);
			inlineLoop = inlineForLoopWithoutBrackets.matcher(currentCheckedLine);
			if (inlineLoop.find()) {
				done = inlineForFix(list, currentCheckedIndex);
				if (done) {
					listSize++;
				}
			}
		}
		checkInlineIfDecleration(list);
	}

	private static void checkInlineIfDecleration(ArrayList<String> list) {
		int listSize = list.size();
		int currentCheckedIndex = 0;
		String currentCheckedLine;
		Matcher inlineStatement;
		boolean done;

		for (; currentCheckedIndex < listSize; currentCheckedIndex++) {
			currentCheckedLine = list.get(currentCheckedIndex);
			inlineStatement = inlineIfStatementWithoutBrackets.matcher(currentCheckedLine);
			if (inlineStatement.find()) {
				done = inlineIfFix(list, currentCheckedIndex);
				if (done) {
					listSize++;
				}
			}
		}
	}

	private static boolean inlineForFix(ArrayList<String> list, int currentCheckedIndex) {
		Matcher loopWithComment, onlyForLoopDec;
		String currentCheckedLine = list.get(currentCheckedIndex);

		loopWithComment = commentForLoopWithoutBrackets.matcher(currentCheckedLine);
		if (!loopWithComment.find()) {
			onlyForLoopDec = onlyForLoopDeclaration.matcher(currentCheckedLine);
			if (onlyForLoopDec.find()) {
				splitInlineStatement(list, currentCheckedIndex, onlyForLoopDec.group(0), true);
				return true;
			}
		}
		return false;
	}

	private static boolean inlineIfFix(ArrayList<String> list, int currentCheckedIndex) {
		Matcher StatementWithComment, onlyStatementDec;
		String currentCheckedLine = list.get(currentCheckedIndex);

		StatementWithComment = commentIfStatementWithoutBrackets.matcher(currentCheckedLine);
		if (!StatementWithComment.find()) {
			onlyStatementDec = onlyIfStatementDeclaration.matcher(currentCheckedLine);
			if (onlyStatementDec.find()) {
				splitInlineStatement(list, currentCheckedIndex, onlyStatementDec.group(0), false);
				return true;
			}
		}
		return false;
	}

	private static void splitInlineStatement(ArrayList<String> list, int currentCheckedIndex, String statementOnly, boolean isFor) {
		String lineWithoutStatementDec;
		String currentCheckedLine = list.get(currentCheckedIndex);
		list.remove(currentCheckedIndex);
		if (isFor) {
			lineWithoutStatementDec = currentCheckedLine.replaceFirst("^(\\s|\\t)*for(\\s)*\\((.*?)\\)", "");
		} else {
			lineWithoutStatementDec = currentCheckedLine.replaceFirst("^(\\s|\\t)*if(\\s)*\\((.*?)\\)", "");
		}
		list.add(currentCheckedIndex, statementOnly);
		list.add(currentCheckedIndex + 1, lineWithoutStatementDec);
	}

	private static void checkDeclarationsWithoutBrackets(ArrayList<String> list) {
		String currentCheckedLine;
		int currentCheckedIndex = 0;
		int listSize = list.size();

		for (; currentCheckedIndex < listSize; currentCheckedIndex++) {
			currentCheckedLine = list.get(currentCheckedIndex);
			if (recursiveCheck(currentCheckedLine)) {
				listSize += addBrackets(list, currentCheckedIndex);
			}
		}
	}

	private static int addBrackets(ArrayList<String> list, int currentCheckedIndex) {
		int numOfPotentialBrackets = 1;
		int closerBracketIndex = currentCheckedIndex + 2;
		String currentCheckedLine = list.get(currentCheckedIndex);
		String bracketLine = null;
		Matcher matcher = null;;

		if (commentForLoopWithoutBrackets.matcher(currentCheckedLine).find()) {
			matcher = onlyForLoopDeclaration.matcher(currentCheckedLine);
			if (matcher.find()) {
				bracketLine = matcher.group(0);
				currentCheckedLine = currentCheckedLine.replaceFirst("^((\\s|\\t)*for(\\s)*\\((.*?)\\))", bracketLine + " {");
			}
		} else if (commentIfStatementWithoutBrackets.matcher(currentCheckedLine).find()) {
			matcher = onlyIfStatementDeclaration.matcher(currentCheckedLine);
			if (matcher.find()) {
				bracketLine = matcher.group(0);
				currentCheckedLine = currentCheckedLine.replaceFirst("^(\\s|\\t)*if(\\s)*\\((.*?)\\)", bracketLine + " {");
			}
		} else {
			currentCheckedLine = currentCheckedLine + " {";
		}
		list.remove(currentCheckedIndex);
		list.add(currentCheckedIndex, currentCheckedLine);

		for (int i = currentCheckedIndex + 1; i < list.size() && recursiveCheck(list.get(i)); i++) {
			currentCheckedLine = list.get(i);
			currentCheckedLine = currentCheckedLine + " {";
			list.remove(i);
			list.add(i, currentCheckedLine);
			numOfPotentialBrackets++;
			closerBracketIndex = i + 2;
		}

		for (int i = 0; i < numOfPotentialBrackets; i++) {
			list.add(closerBracketIndex, "}");
		}

		return numOfPotentialBrackets;
	}

	private static boolean recursiveCheck(String line) {
		if (newlineForLoopWithoutBrackets.matcher(line).find() || commentForLoopWithoutBrackets.matcher(line).find()
				|| newlineIfStatementWithoutBrackets.matcher(line).find()
				|| commentIfStatementWithoutBrackets.matcher(line).find()) {
			return true;
		}
		return false;
	}

	public static void bracketsReformatter(String inputFilePath, String reformattedFilePath) {
		ArrayList<String> list = analyzerUtils.FileOperations
				.fileContentToArrayListStringOnly(new File(inputFilePath), true);

		checkInlineForLoopDecleration(list);
		checkDeclarationsWithoutBrackets(list);
		analyzerUtils.FileOperations.writeCodeToFileString(list, new File(reformattedFilePath));
	}

}
