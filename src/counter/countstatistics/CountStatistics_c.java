package counter.countstatistics;

import java.util.LinkedList;
import java.util.List;

import counter.filestatistics.FileStatistics_c;

public class CountStatistics_c extends CountStatistics{


	public boolean seekingMacroname;
	public boolean seekingAssignname;
	public List<String> macroList;
	/*
	 * containMacroAssign is used to tell if the right part
	 *  of an assignment contains an macro
	 * containsOnlyMacroAssign = is used to tell if the 
	 * right part of an assignment contains only macro and
	 * no other variable.
	 * At first, containOnlyMacroAssign is set to true;
	 * as long as a variable name that is not a macro exist
	 * in an assignment, it is set to false.
	 * containMacroAssign is set to false at first, if a macro
	 * in an assignment exist, it is set to true.
	 * So if an assignment does not contain only macro assign,
	 * it is not a const assignment. But if it contains a macro
	 * and contains only the macro, it is an const assignment.
	 */
	public boolean containMacroAssign = false;
	public boolean containOnlyMacroAssign = true;
	public boolean containMacroDefinition = false;
	public CountStatistics_c(Boolean saveop){
		saveOperator = saveop;
		macroList = new LinkedList<String>();
	}

	@Override
	public void startUnit(String fileName){
		currentFile = new FileStatistics_c();
		currentFile.setFileName(fileName);
		unitlevel++;
	}
	@Override
	public void startStruct() {
		currentFile.increaseNumStruct();
	}

	@Override
	public void startGoto() {
		currentFile.increaseNumGoto();
	}

	@Override
	public void startLabel() {
		currentFile.increaseNumLabel();
	}
	
	public void startCppdefine(){
		seekingMacroname = true;
		containMacroDefinition = true;
	}
	
	public void clearMacroList(){
		macroList.clear();
		containMacroDefinition = false;
	}
	
	public void seekMacroname(){
		if(seekingMacroname||seekingAssignname) {
			collectChars = true;
			charbucket = null;
		}
	}
	
	public void setMacroConstAssign(){
		if(containMacroDefinition&&containMacroAssign&&containOnlyMacroAssign){
			currentFile.numConstAssign += numassign;
		}
	}
	
	public void seekAssignname(boolean b){
		seekingAssignname = b;
	}
	
	public void addMacroname(){
		if(seekingMacroname){
			macroList.add(charbucket.trim());
			seekingMacroname = false;
			collectChars = false;
		}
	}
	
	public void checkMacroAssign(){
		if(seekingAssignname){
			if(charbucket!=null&&!macroList.contains(charbucket.trim())){
				containOnlyMacroAssign = false;
				seekingAssignname = false;
			}
			if(!incall&&macroList.contains(charbucket.trim())){
				containMacroAssign = true;
			}
		}
	}
	
	public void addMacroAssign(){
		if(containMacroDefinition&&!isConstAssign
				&&containMacroAssign&&containOnlyMacroAssign&&numOperator==0){
			currentFile.numConstAssign += numassign;
		}
	}

}
