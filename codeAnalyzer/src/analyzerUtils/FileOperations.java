package analyzerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import analyzedClass.analyzedMethod.MethodInfo;
import analyzedClass.analyzedMethod.MethodsDataSet;

public class FileOperations {
	
	// public static Pattern slashStarComment = Pattern.compile("^(\\s|\\t)*/*(.*?)*/$");
	// public static Pattern dpubleSlashComment = Pattern.compile("^(\\s|\\t)*//(.*?)*$");
	
	public static File createNewFile(String fileName, String filePathOnly) {
		Path filePath = (filePathOnly != null ? Paths.get(filePathOnly + fileName) : Paths.get(fileName));
		File newFile = null;
        try {
            newFile = (Files.createFile(filePath)).toFile();
            
        } catch (IOException e) {
            System.err.println("already exists: " + e.getMessage());
        }
        return newFile;
    }

	public static void writeCodeToFile(ArrayList<StringBuffer> injectedCode, File outputCodeFile) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(outputCodeFile);
			for(StringBuffer line : injectedCode) {
				writer.write(line + "\n");
			}
		} catch (IOException e) {
			System.err.println("error writing to the file" + e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		}
	}
	
	public static void writeCodeToFileString(ArrayList<String> injectedCode, File outputCodeFile) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(outputCodeFile);
			for(String line : injectedCode) {
				writer.write(line + "\n");
			}
		} catch (IOException e) {
			System.err.println("error writing to the file" + e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		}
	}
	
	public static ArrayList<StringBuilder> fileContentToArrayListString(File javaFile, boolean noEmptyLines) {
		BufferedReader reader = null;
		String line = null;
		ArrayList<StringBuilder> javaCode = new ArrayList<StringBuilder>();
		try {
			reader = new BufferedReader(new FileReader(javaFile));
			if(noEmptyLines) {
				while ((line = reader.readLine()) != null) {
					if(!line.matches("^((\\t)|(\\s))*$")) {
						javaCode.add(new StringBuilder(line));
					}
				}
			} else {
				while ((line = reader.readLine()) != null) {
					javaCode.add(new StringBuilder(line));
				}	
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return javaCode;
	}

	
	public static ArrayList<String> fileContentToArrayListStringOnly(File javaFile, boolean noEmptyLines) {
		BufferedReader reader = null;
		String line = null;
		ArrayList<String> javaCode = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(javaFile));
			if(noEmptyLines) {
				while ((line = reader.readLine()) != null) {
					if(!line.matches("^((\\t)|(\\s))*$")) {
						javaCode.add(new String(line));
					}
				}
			} else {
				while ((line = reader.readLine()) != null) {
					javaCode.add(new String(line));
				}	
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return javaCode;
	}
//	public static void writeCodeToFile(MethodsDataSet methodsDataSet, File outputCodeFile) {
//		FileWriter writer = null;
//		try {
//			writer = new FileWriter(outputCodeFile);
//			// first write the code lines before all declarations
//			for(StringBuffer line : methodsDataSet.codeBeforMethods) {
//				writer.write(line + "\n");
//			}
//			
//			// write the methods code lines 
//			for(Entry<String,MethodInfo> entry : methodsDataSet.getEntrySet()) {
//				for(StringBuffer line : entry.getValue().methodCodeLines) {
//					writer.write(line + "\n");
//				}
//			}
//			writer.write("}\n"); // closing the class code
//			
//		} catch (IOException e) {
//			System.err.println("error writing to the file" + e.getMessage());
//		} finally {
//			if (writer != null) {
//				try {
//					writer.flush();
//					writer.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//
//			}
//		}
//	}


}
