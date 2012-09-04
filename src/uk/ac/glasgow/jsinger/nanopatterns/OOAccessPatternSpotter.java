// RecursivePatternSpotter.java
// Jeremy Singer
// 18 Nov 08

package uk.ac.glasgow.jsinger.nanopatterns;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;

/**
 * simple method scanner (asm-speak adapter) to 
 * check whether a method has any object-oriented
 * behaviour (field sets / gets) and new object
 * instantiations.
 * - (statically) - in which case this
 * scanner reports that the class implements the 
 * appropriate micro-patterns
 */

public class OOAccessPatternSpotter extends MethodAdapter{

  private boolean createsNewObjects;
  private boolean getsThisInstanceFields;
  private boolean setsThisInstanceFields;
  private boolean getsOtherInstanceFields;
  private boolean setsOtherInstanceFields;
  private boolean getsStaticFields;
  private boolean setsStaticFields;
  /* @jsinger
   * Now we attempt to distinguish between get/set field
   * operations on "this" object, and on "other" objects.
   * We monitor instances of "ALOAD_0" which loads the this
   * ptr on the stack. If this occurs immediately prior to
   * a getfield, or 2 instrs prior to a setfield, then we
   * know it is an access through the "this" ptr. Otherwise,
   * we conservatively assume that it is an "other" ptr access.
   * (To be any more complex would require simulating the
   * operand stack, and doing alias analysis.
   */
  private int instrsSinceLastALOAD0;
  

  public OOAccessPatternSpotter(MethodVisitor mv) {
    super(mv);
    this.createsNewObjects = false;
    this.getsThisInstanceFields = false;
    this.setsThisInstanceFields = false;
    this.getsOtherInstanceFields = false;
    this.setsOtherInstanceFields = false;
    this.getsStaticFields = false;
    this.setsStaticFields = false;
    this.instrsSinceLastALOAD0 = Integer.MAX_VALUE;
  }
    
  //@Override
  public void visitFieldInsn(int opcode,
			     String owner,
			     String name,
			     String desc) {
    instrsSinceLastALOAD0++;
    
    // check whether this is a get or a set
    // (i.e. read or write)
    if (opcode == Opcodes.GETFIELD) {
      if (instrsSinceLastALOAD0 == 1) {
	this.getsThisInstanceFields = true;
      }
      else {
	this.getsOtherInstanceFields = true;
      }
    }
    else if (opcode == Opcodes.GETSTATIC) {
      this.getsStaticFields = true;
    }
    else if (opcode == Opcodes.PUTFIELD) {
      if (instrsSinceLastALOAD0 == 2) {
	this.setsThisInstanceFields = true;
      }
      else {
	this.setsOtherInstanceFields = true;
      }
    }
    else if (opcode == Opcodes.PUTSTATIC) {
      this.setsStaticFields = true;
    }
  }

  // @Override
  public void visitVarInsn(int opcode,
			   int index) {
    instrsSinceLastALOAD0++;

    // look for ALOAD0  (loads "this" ptr onto stack)
    if (opcode==Opcodes.ALOAD &&
	index==0) {
      
      instrsSinceLastALOAD0 = 0;
    }
  }
  
  // @Override
  public void visitInsn(int opcode) {
    // @jsinger - This gets called for many (but not all)
    // bytecode instrs...
    // See http://asm.ow2.org/doc/tutorial.html for details.
    instrsSinceLastALOAD0++;
  }
      
      

  // @Override
  public void visitTypeInsn(int opcode,
			    String type) {
    // check whether this is a new object creation
    if (opcode == Opcodes.NEW) {
      this.createsNewObjects = true;
    }
  }

    
  // accessor methods for computed meta-data about method

  public boolean isObjectCreator() {
    return this.createsNewObjects;
  }

  public boolean isThisInstanceFieldReader() {
    return this.getsThisInstanceFields;
  }

  public boolean isThisInstanceFieldWriter() {
    return this.setsThisInstanceFields;
  }

  public boolean isOtherInstanceFieldReader() {
    return this.getsOtherInstanceFields;
  }

  public boolean isOtherInstanceFieldWriter() {
    return this.setsOtherInstanceFields;
  }

  public boolean isStaticFieldReader() {
    return this.getsStaticFields;
  }

  public boolean isStaticFieldWriter() {
    return this.setsStaticFields;
  }

}
