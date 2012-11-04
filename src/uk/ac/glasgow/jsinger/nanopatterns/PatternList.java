package uk.ac.glasgow.jsinger.nanopatterns;

public class PatternList {

	public boolean noParams;
	public boolean noReturn;
	public boolean isRecursive;
	public boolean isSameNameCaller;
	public boolean isLeaf;
	public boolean isObjectCreator;
	public boolean isThisInstanceFieldReader;
	public boolean isThisInstanceFieldWriter;
	public boolean isOtherInstanceFieldReader;
	public boolean isOtherInstanceFieldWriter;
	public boolean isStaticFieldReader;
	public boolean isStaticFieldWriter;
	public boolean isTypeManipulator;
	public boolean isStraightLineCode;
	public boolean isLoopingCode;
	public boolean isSwitcher;
	public boolean throwsExceptions;
	public boolean isLocalVarReader;
	public boolean isLocalVarWriter;
	public boolean isArrayCreator;
	public boolean isArrayReader;
	public boolean isArrayWriter;
	public boolean isPolymorphic;
	public boolean isSingleReturner;
	public boolean isMultipleReturner;
	public boolean isClient;
	public boolean isJdkClient;
	public boolean isTailCaller;
	
	public void printResultsAsBooleans() {
		printBooleanValue(noParams);
		printBooleanValue(noReturn);
		printBooleanValue(isRecursive);
		printBooleanValue(isSameNameCaller);
		printBooleanValue(isLeaf);
		printBooleanValue(isObjectCreator);
		printBooleanValue(isThisInstanceFieldReader);
		printBooleanValue(isThisInstanceFieldWriter);
		printBooleanValue(isOtherInstanceFieldReader);
		printBooleanValue(isOtherInstanceFieldWriter);
		printBooleanValue(isStaticFieldReader);
		printBooleanValue(isStaticFieldWriter);
		printBooleanValue(isTypeManipulator);
		printBooleanValue(isStraightLineCode);
		printBooleanValue(isLoopingCode);
		printBooleanValue(isSwitcher);
		printBooleanValue(throwsExceptions);
		printBooleanValue(isLocalVarReader);
		printBooleanValue(isLocalVarWriter);
		printBooleanValue(isArrayCreator);
		printBooleanValue(isArrayReader);
		printBooleanValue(isArrayWriter);
		printBooleanValue(isPolymorphic);
		printBooleanValue(isSingleReturner);
		printBooleanValue(isMultipleReturner);
		printBooleanValue(isClient);
		printBooleanValue(isJdkClient);
		printBooleanValue(isTailCaller);
	}
	
	/**
	 * trivial support for MP-tool style reporting of exhibited nanopatterns
	 */
	public static void printBooleanValue(boolean value) {
		if (value) {
			System.out.print(" 1");
		} else {
			System.out.print(" 0");
		}
	}
	
}
