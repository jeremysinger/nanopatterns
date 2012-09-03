// TypeManipulatorPatternSpotter.java
// Jeremy Singer
// 18 Nov 08

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;

/**
 * simple method scanner (asm-speak adapter) to 
 * check whether a method has any 
 * type-manipulating behaviour
 * (defined by Hoste as static occurrences of
 * the checkcast and instanceof 
 * bytecode operations in a method
 */

public class TypeManipulatorPatternSpotter extends MethodAdapter{

    private boolean checkCastPresent;
    private boolean instanceOfPresent;

    public TypeManipulatorPatternSpotter(MethodVisitor mv) {
	super(mv);
	this.checkCastPresent = false;
	this.instanceOfPresent = false;
    }
    
    // @Override
    public void visitTypeInsn(int opcode,
			      String type) {
	// check whether this is a new object creation
	if (opcode == Opcodes.CHECKCAST) {
	    this.checkCastPresent = true;
	}
	else if (opcode == Opcodes.INSTANCEOF) {
	    this.instanceOfPresent = true;
	}
    }
    
    
    // accessor methods for computed meta-data about method

    public boolean isTypeManipulator() {
	return 
	    this.checkCastPresent ||
	    this.instanceOfPresent;
    }

}
