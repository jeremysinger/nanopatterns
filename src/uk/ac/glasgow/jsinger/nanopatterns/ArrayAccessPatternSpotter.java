// ArrayAccessPatternSpotter.java
// Jeremy Singer
// 18 Nov 08

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;

/**
 * simple method scanner (asm-speak adapter) to 
 * check whether a method has any 
 * array creation or access behaviour, or
 * any local variable access behaviour.
 * 
 * Potential nanopatterns to spot:
 *  loads local var
 *  stores local var
 *  creates array (newarray, anewarray, multianewarray)
 *  loads from array ref
 *  stores to array ref
 */

public class ArrayAccessPatternSpotter extends MethodAdapter {

    private boolean readsLocalVar;
    private boolean writesLocalVar;
    private boolean createsArray;
    private boolean readsArray;
    private boolean writesArray;

    public ArrayAccessPatternSpotter(MethodVisitor mv) {
	super(mv);
	this.readsLocalVar = false;
	this.writesLocalVar = false;
	this.createsArray = false;
	this.readsArray = false;
	this.writesArray = false;
    }

    // @Override
    public void visitInsn(int opcode) {
	if (opcode >= Opcodes.IALOAD &&
	    opcode <= Opcodes.SALOAD) {
	    this.readsArray = true;
	}
	else if (opcode >= Opcodes.IASTORE &&
	    opcode <= Opcodes.SASTORE) {
	    this.writesArray = true;
	}
    }
    
    // @Override
    public void visitVarInsn(int opcode, int var) {
	if (opcode >= Opcodes.ILOAD &&
	    opcode <= Opcodes.ALOAD) {
	    this.readsLocalVar = true;
	}
	else if (opcode >= Opcodes.ISTORE &&
	    opcode <= Opcodes.ASTORE) {
	    this.writesLocalVar = true;
	}
	
    }


    // @Override
    public void visitTypeInsn(int opcode,
			      String type) {
	// check whether this is a new array creation
	if (opcode == Opcodes.ANEWARRAY) {
	    this.createsArray = true;
	}
    }
    

    // @Override
    public void visitIntInsn(int opcode, int operand) {
	// check whether this is a new array creation
	if (opcode == Opcodes.NEWARRAY) {
	    this.createsArray = true;
	}
    }
    
    // @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
	this.createsArray = true;
    }
    
    // accessor methods for computed meta-data about method

    public boolean isLocalVarReader() {
	return this.readsLocalVar;
    }

    public boolean isLocalVarWriter() {
	return this.writesLocalVar;
    }

    public boolean isArrayCreator() {
	return this.createsArray;
	    
    }

    public boolean isArrayReader() {
	return this.readsArray;
    }

    public boolean isArrayWriter() {
	return this.writesArray;
    }

}
