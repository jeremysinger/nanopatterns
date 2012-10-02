// MethodPatternSpotter.java
// Jeremy Singer
// 2 Oct 12

package uk.ac.glasgow.jsinger.nanopatterns;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;


/**
 * simple method scanner (asm-speak adapter) to 
 * check for nanopatterns in a method's calling
 * activity.
 * @SeeAlso RecursivePatternSpotter
 */

public class MethodPatternSpotter extends MethodAdapter {

    private int numMethodCalls;
    private int numInterfaceMethodCalls;
    // private int numDynamicMethodCalls; - @TODO

    private boolean jdkClient;
    private boolean lastInstructionWasMethodCall;
    private boolean tailCaller;

    public MethodPatternSpotter(MethodVisitor mv) {
	super(mv);
	this.numMethodCalls = 0;
	this.numInterfaceMethodCalls = 0;
	// this.numDynamicMethodCalls = 0; @TODO
	this.jdkClient = false;
	this.lastInstructionWasMethodCall = false;
	this.tailCaller = false;
    }
    
    //@Override
    public void visitMethodInsn(int opcode, String owner,
				String name, String desc) {
	
// 	System.out.println("RPS: found method call: " +
// 			   owner + ":" +
// 			   name + ":" +
// 			   desc);

	this.numMethodCalls++;
	if (opcode == Opcodes.INVOKEINTERFACE) {
	    this.numInterfaceMethodCalls++;
	}
	
	// set lastInstrIsCall to true...
	// then check for RETURN instruction next to see
	// whether it works or not...
	this.lastInstructionWasMethodCall = true;

	// check whether the method call is to a 
	// Java SDK method...
	if (owner.contains("java")) {
	    this.jdkClient = true;
	}
	
	mv.visitMethodInsn(opcode, owner, name, desc);
    }
    
    
    /**
     * NOTE - this way to find tail calls is _imprecise_
     * since we do not turn off the lastInstructionWasMethodCall
     * for IInc instructions, Ldc instructions, Switch instrs, etc...
     * might need to think about fixing this!!! @TODO
     */
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
	    if (this.lastInstructionWasMethodCall) {
		this.tailCaller = true;
	    }
	default:
	    this.lastInstructionWasMethodCall = false;
	    break;
	}

    }
	
    
    // accessor methods for computed nanopattern metadata

    /**
     * is this method a client? i.e. does it issue
     * all its calls through interfaces, rather than 
     * implementations?
     */
    public boolean isClient() {
	return (this.numInterfaceMethodCalls > 0 &&
		this.numMethodCalls== this.numInterfaceMethodCalls);
    }

    /**
     * is this method a JDK client? I.e. does it call
     * methods from the JDK standard library (java.*)
     */
    public boolean isJdkClient() {
	return this.jdkClient;
    }
    
    /**
     * does this method issue any tail calls, i.e.
     * Method call followed immediately by return stmt
     */
    public boolean isTailCaller() {
	return this.tailCaller;
    }

}
