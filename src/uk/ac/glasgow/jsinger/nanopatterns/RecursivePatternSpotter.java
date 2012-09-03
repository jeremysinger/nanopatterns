// RecursivePatternSpotter.java
// Jeremy Singer
// 18 Nov 08

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;


/**
 * simple method scanner (asm-speak adapter) to 
 * check whether a method issues any recursive calls
 * to itself
 * - (statically) - in which case this
 * method exhibits the recursive nanopattern
 */

public class RecursivePatternSpotter extends MethodAdapter {

    /**
     * following three fields required to
     * identify recursive method calls
     */
    private String className;
    private String methodName;
    private String methodDescriptor;

    /**
     * following field used to identify leaf methods
     */
    private int numMethodCalls;
    
    /**
     * following field used to identify recursive method calls
     */
    private boolean recursive;
  
  /** 
   * does this method call a different method with the same
   * name?
   */
  private boolean sameNameCall;

    public RecursivePatternSpotter(MethodVisitor mv,
				   String className,
				   String methodName,
				   String methodDescriptor) {
	super(mv);
	this.className = className;
	this.methodName = methodName;
	this.methodDescriptor = methodDescriptor;
	this.numMethodCalls = 0;
	this.recursive = false;
	this.sameNameCall = false;
    }
    
    //@Override
    public void visitMethodInsn(int opcode, String owner,
				String name, String desc) {
	
// 	System.out.println("RPS: found method call: " +
// 			   owner + ":" +
// 			   name + ":" +
// 			   desc);

	this.numMethodCalls++;
	

	if (owner.equals(this.className) &&
	    name.equals(this.methodName) &&
	    desc.equals(this.methodDescriptor)) {
	    this.recursive = true;
	}
	else if (name.equals(this.methodName) &&
		 (!(owner.equals(this.className)) ||
		  !(desc.equals(this.methodDescriptor)))) {
	  // method calls another (distinct) method
	  // with the same name
	  // (Nanopattern from Hoste - different from 
	  // recursive nanopattern above)
	  this.sameNameCall = true;
	}

	mv.visitMethodInsn(opcode, owner, name, desc);
    }
    


    /**
     * is this method recursive? i.e. is there a call
     * to itself in the static bytecode of the method?
     */
    public boolean isRecursive() {
	return this.recursive;
    }

  /**
   * does this method call another distinct method
   * with the same name as itself?
   * (Nanopattern from Hoste paper)
   */
  public boolean isSameNameCaller() {
    return this.sameNameCall;
  }

    /**
     * is this method a leaf method? i.e. does it not call
     * any other methods (including itself)?
     */
    public boolean isLeaf() {
	return (this.numMethodCalls == 0);
    }

}
