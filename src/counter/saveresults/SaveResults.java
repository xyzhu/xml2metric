package counter.saveresults;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import counter.filestatistics.*;



public abstract class SaveResults {
	public int numFile;
	public int numTotalLine;
	public int numCommentLine;
	public int numBlankLine;
	public int numFunction;
	public int numFunctionDecl;
	public int numBlock;
	public int numDeclstmt;
	public int numDecl;
	public int numExprstmt;
	public int numExpr;
	public int numIf;
	public int numElse;
	public int numWhile;
	public int numFor;
	public int numContinue;
	public int numBreak;
	public int numDo;
	public int numSwitch;
	public int numCase;
	public int numReturn;
	public int numCall;
	public int numParamList;
	public int numParam;
	public int numArguList;
	public int numArgument;
	public int numAssignment;
	public int numLocalFunctionCall = 0;
	public int numLibFunctionCall = 0;
	public int numZeroOpAssign = 0;
	public int numZeroOpCallAssign = 0;
	public int numConstAssign = 0;


	public void writeResult(String fileName,
			List<FileStatistics> fileList, List<String> functionList,
			List<String> functionCallList, List<String> classList, 
			Boolean saveFunction, Boolean saveOperator) {
		FileStatistics fileStatistics;
		String fileInfo;
		try{
			int index = fileName.indexOf(".xml");
			String outFileName = fileName.substring(0, index);
			//create file
			FileWriter fw = new FileWriter(outFileName + ".txt");
			BufferedWriter writer = new BufferedWriter(fw);
			StringBuilder fileBuilder = new StringBuilder();
			StringBuilder totalBuilder = new StringBuilder();
			Iterator<FileStatistics> it = fileList.iterator();
			while(it.hasNext()){
				numFile++;
				fileStatistics = it.next();
				fileInfo = getFileStatistics(fileStatistics);
				fileBuilder.append(fileInfo);
				fileBuilder.append("\n");
			}
			getLocalMethodCallNumber(functionList, functionCallList, classList);
			numLibFunctionCall = functionCallList.size() - numLocalFunctionCall;
			String totalInfo = getTotalStatistics();
			totalBuilder.append(totalInfo);
			writer.write(totalBuilder.toString());
			writer.write(fileBuilder.toString());
			writer.close();
			if(saveFunction){
				writeFunction(fileName, functionList, functionCallList);
			}
			if(saveOperator){
				writeOperator(fileName, fileList);
			}
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}		
	}

	public void writeFunction(String fileName, List<String> functionList,
			List<String> functionCallList) {
		try{
			int index = fileName.indexOf(".");
			String outFileName = fileName.substring(0, index);
			//create file
			FileWriter fw_function = new FileWriter(outFileName +"_functions.txt");
			FileWriter fw_functioncall = new FileWriter(outFileName +"_functioncalls.txt");
			BufferedWriter writer_function = new BufferedWriter(fw_function);
			BufferedWriter writer_functioncall = new BufferedWriter(fw_functioncall);
			Iterator<String> it_function = functionList.iterator();
			StringBuilder functionBuilder = new StringBuilder();
			while(it_function.hasNext()){
				functionBuilder.append(it_function.next());
				functionBuilder.append("\n");
			}
			writer_function.write(functionBuilder.toString());
			writer_function.close();
			Iterator<String> it_functioncall = functionCallList.iterator();
			StringBuilder functioncallBuilder = new StringBuilder();
			while(it_functioncall.hasNext()){
				functioncallBuilder.append(it_functioncall.next());
				functioncallBuilder.append("\n");
			}
			writer_functioncall.write(functioncallBuilder.toString());
			writer_functioncall.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}		
	}

	public void writeOperator(String fileName, 
			List<FileStatistics> fileList) {
		FileStatistics fileStatistics;
		try{
			int index = fileName.indexOf(".");
			String outFileName = fileName.substring(0, index);
			//create file
			FileWriter fw_operator = new FileWriter(outFileName +"_operator.txt");
			BufferedWriter writer_operator = new BufferedWriter(fw_operator);
			String operatorInfo = "";
			Iterator<FileStatistics> it = fileList.iterator();
			while(it.hasNext()){
				numFile++;
				fileStatistics = it.next();
				operatorInfo = getOperatorInfo(fileStatistics.numOperatorList);
				writer_operator.write(fileStatistics.fileName);
				writer_operator.write("\n");
				writer_operator.write(operatorInfo);
			}
			writer_operator.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}		
	}


	private String getOperatorInfo(List<Integer> numOperatorList) {
		StringBuilder opBuilder = new StringBuilder();
		Iterator <Integer> opIt = numOperatorList.iterator();
		int n = 0;
		while(opIt.hasNext()){
			n = opIt.next();
			opBuilder.append(n);
			opBuilder.append("\n");
		}
		return opBuilder.toString();
	}


	private void getLocalMethodCallNumber(List<String> functionList,
			List<String> functionCallList, List<String> classList) {
		Set<String> methodSet = new HashSet<String>();
		Iterator<String> it_method = functionList.iterator();
		while(it_method.hasNext()){
			methodSet.add(it_method.next());
		}
		int clsize = 0;
		Set<String> classSet = null;
		if(classList!=null){
			clsize = classList.size();
			classSet = new HashSet<String>();
		}
		if(clsize!=0){
			Iterator<String> it_class = classList.iterator();
			while(it_class.hasNext()){
				classSet.add(it_class.next());
			}
		}
		Iterator<String> it_call = functionCallList.iterator();
		String callname;
		while(it_call.hasNext()){
			callname = it_call.next();
			if (methodSet.contains(callname)){
				numLocalFunctionCall++;
			}
			if(clsize!=0){
				if(classSet.contains(callname)){
					numLocalFunctionCall++;
				}
				else if(callname.startsWith("~")){
					if(classSet.contains(callname.substring(1, callname.length()))){
						numLocalFunctionCall++;
					}
				}
			}
		}
	}

	public String getFileStatistics(FileStatistics fileStatistics){
		String samepart = getSameFileStatistics(fileStatistics);
		String diffpart = getDiffFileStatistics(fileStatistics);
		return samepart+diffpart;
	}

	private String getSameFileStatistics(FileStatistics fileStatistics) {
		FileStatistics fs = fileStatistics;
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("*****  File Statistics  *****");
		sBuilder.append("\n");
		sBuilder.append("\n");
		sBuilder.append("File name: [[[ " + fs.fileName + " ]]]");
		sBuilder.append("\n");
		sBuilder.append("\n");
		sBuilder.append("Total line: " + fs.numTotalLine);
		numTotalLine += fs.numTotalLine;
		sBuilder.append("\n");
		sBuilder.append("Comment line: " + fs.numCommentLine);
		numCommentLine += fs.numCommentLine;
		sBuilder.append("\n");
		sBuilder.append("Blank line: " + fs.numBlankLine);
		numBlankLine += fs.numBlankLine;
		sBuilder.append("\n");
		sBuilder.append("Function declaration: " + fs.numFunctionDecl);
		numFunctionDecl += fs.numFunctionDecl;
		sBuilder.append("\n");
		sBuilder.append("Function: " + fs.numFunction);
		numFunction += fs.numFunction;
		sBuilder.append("\n");
		sBuilder.append("Declaration statement: " + fs.numDeclstmt);
		numDeclstmt += fs.numDeclstmt;
		sBuilder.append("\n");
		sBuilder.append("Declaration:" + fs.numDecl);
		numDecl += fs.numDecl;
		sBuilder.append("\n");
		sBuilder.append("Blocks: " + fs.numBlock);
		numBlock += fs.numBlock;
		sBuilder.append("\n");
		sBuilder.append("Expressions: " + fs.numExpr);
		numExpr += fs.numExpr;
		sBuilder.append("\n");
		sBuilder.append("Expression statements: " + fs.numExprstmt);
		numExprstmt += fs.numExprstmt;
		sBuilder.append("\n");
		sBuilder.append("Call: " + fs.numCall);
		numCall += fs.numCall;
		sBuilder.append("\n");
		sBuilder.append("Continue: " + fs.numContinue);
		numContinue += fs.numContinue;
		sBuilder.append("\n");
		sBuilder.append("Break: " + fs.numBreak);
		numBreak += fs.numBreak;
		sBuilder.append("\n");
		sBuilder.append("Return: " +fs.numReturn);
		numReturn += fs.numReturn;
		sBuilder.append("\n");
		sBuilder.append("For: " + fs.numFor);
		numFor += fs.numFor;
		sBuilder.append("\n");
		sBuilder.append("If: " + fs.numIf);
		numIf += fs.numIf;
		sBuilder.append("\n");
		sBuilder.append("Else: " + fs.numElse);
		numElse += fs.numElse;
		sBuilder.append("\n");
		sBuilder.append("While: " + fs.numWhile);
		numWhile += fs.numWhile;
		sBuilder.append("\n");
		sBuilder.append("Do: " + fs.numDo);
		numDo += fs.numDo;
		sBuilder.append("\n");
		sBuilder.append("Switch: " + fs.numSwitch);
		numSwitch += fs.numSwitch;
		sBuilder.append("\n");
		sBuilder.append("Case: " + fs.numCase);
		numCase += fs.numCase;
		sBuilder.append("\n");
		sBuilder.append("Parameter list: " + fs.numParamList);
		numParamList += fs.numParamList;
		sBuilder.append("\n");
		sBuilder.append("Param: " + fs.numParam);
		numParam += fs.numParam;
		sBuilder.append("\n");
		sBuilder.append("Argument list: " + fs.numArguList);
		numArguList += fs.numArguList;
		sBuilder.append("\n");
		sBuilder.append("Argument: " + fs.numArgu);
		numArgument += fs.numArgu;
		sBuilder.append("\n");
		sBuilder.append("Assignment: " + fs.numAssignment);
		numAssignment += fs.numAssignment;
		sBuilder.append("\n");
		sBuilder.append("Zero operator assignment: " + fs.numZeroOpAssign);
		numZeroOpAssign += fs.numZeroOpAssign;
		sBuilder.append("\n");
		sBuilder.append("Zero operator call assignment: " + fs.numZeroOpCallAssign);
		numZeroOpCallAssign += fs.numZeroOpCallAssign;
		sBuilder.append("\n");
		sBuilder.append("Const assignment: " + fs.numConstAssign);
		numConstAssign += fs.numConstAssign;
		sBuilder.append("\n");
		return sBuilder.toString();

	}

	public String getTotalStatistics(){
		String samepart = getSameTotalStatistics();
		String diffpart = getDiffTotalStatistics();
		return samepart+diffpart;
	}

	private String getSameTotalStatistics() {
		StringBuilder total = new StringBuilder();
		total.append("\n");
		total.append("*** Total Statistics Information ***");
		total.append("\n");
		total.append("\n");
		total.append("File: " + numFile);
		total.append("\n");
		total.append("Total line: " + numTotalLine);
		total.append("\n");
		total.append("Comment line: " + numCommentLine);
		total.append("\n");
		total.append("Blank line: " + numBlankLine);
		total.append("\n");
		total.append("Function declaration: " + numFunctionDecl);
		total.append("\n");
		total.append("Function: " + numFunction);
		total.append("\n");
		total.append("Call: " + numCall);
		total.append("\n");
		total.append("Expression statement: " + numExprstmt);
		total.append("\n");
		total.append("Expression: " + numExpr);
		total.append("\n");
		total.append("Block: " + numBlock);
		total.append("\n");
		total.append("Declaration: " + numDecl);
		total.append("\n");
		total.append("Declaration statement: " + numDeclstmt);
		total.append("\n");
		total.append("Continue: " + numContinue);
		total.append("\n");
		total.append("Break: " + numBreak);
		total.append("\n");
		total.append("Return: " + numReturn);
		total.append("\n");
		total.append("For: " + numFor);
		total.append("\n");
		total.append("If: " + numIf);
		total.append("\n");
		total.append("Else: " + numElse);
		total.append("\n");
		total.append("While: " + numWhile);
		total.append("\n");
		total.append("Switch: " + numSwitch);
		total.append("\n");
		total.append("Case: " + numCase);
		total.append("\n");
		total.append("Parameter list: " + numParamList);
		total.append("\n");
		total.append("Argument list: " + numArguList);
		total.append("\n");
		total.append("Parameter: " + numParam);
		total.append("\n");
		total.append("Argument: " + numArgument);
		total.append("\n");
		total.append("Assignment: " + numAssignment);
		total.append("\n");
		total.append("Do: " + numDo);
		total.append("\n");
		total.append("Local function call: " + numLocalFunctionCall);
		total.append("\n");
		total.append("Library function call: " + numLibFunctionCall);
		total.append("\n");
		total.append("Zero operator assignment: " + numZeroOpAssign);
		total.append("\n");
		total.append("Zero operator call assignment: " + numZeroOpCallAssign);
		total.append("\n");
		total.append("Const assignment: " + numConstAssign);
		total.append("\n");
		return total.toString();
	}

	public abstract String getDiffTotalStatistics();
	public abstract String getDiffFileStatistics(FileStatistics fileStatistics);

}
