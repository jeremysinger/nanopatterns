// ReturnPatternSpotter.java
// Jeremy Singer
// 2 Oct 12

package uk.ac.glasgow.jsinger.nanopatterns;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;

/**
 * simple method scanner (asm-speak adapter) to 
 * count how many return instructions there are
 * in a method.
 * Two nanopatterns: SingleReturnInstr and MultipleReturnInstrs
 * These are mutually exclusive - I guess each method should
 * implement exactly one of these two nanopatterns.
 */

public class ReturnPatternSpotter extends MethodAdapter {

    /**
     * number of return instructions present
     * in bytecode vector of method
     */
    private int numReturnInstructions;

    public ReturnPatternSpotter(MethodVisitor mv) {
	super(mv);
	this.numReturnInstructions = 0;
    }

    // @Override
    public void visitInsn(int opcode) {
	// @jsinger - This gets called for all zero-optcode
	// bytecode instrs.
	// See http://asm.ow2.org/asm40/javadoc/user/index.html?org/objectweb/asm/MethodVisitor.html for details.
	
	switch (opcode) {
	case Opcodes.RETURN:
	case Opcodes.IRETURN:
	case Opcodes.LRETURN:
	case Opcodes.FRETURN:
	case Opcodes.DRETURN:
	case Opcodes.ARETURN:
	    this.numReturnInstructions++;
	    break;
	default:
	    break;
	}
    }

    // accessor methods for computed meta-data about method

    public boolean isSingleReturner() {
	return (this.numReturnInstructions==1);
    }
    
    public boolean isMultipleReturner() {
	return (this.numReturnInstructions>1);
    }

}