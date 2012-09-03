// PolymorphicPatternSpotter.java
// Jeremy Singer
// 23 Apr 09

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;


/**
 * simple method scanner (asm-speak adapter) to 
 * check whether a method issues any polymorphic calls.
 * Since all our analysis is static, we cannot detect
 * virtual calls to overriding methods, so we only look
 * for calls that are made to abstract methods, i.e.
 * through interfaces and methods in abstract
 * classes.
 */


public class PolymorphicPatternSpotter extends MethodAdapter {

  /**
   * following field used to record whether the method
   * makes any polymorphic calls
   */
  private boolean polymorphic;

  
  public PolymorphicPatternSpotter(MethodVisitor mv) {
    super(mv);
    polymorphic = false;
  }
    
  //@Override
  public void visitMethodInsn(int opcode, String owner,
			      String name, String desc) {

    // abstract method called
    // through interface
    if (opcode == Opcodes.INVOKEINTERFACE) {
      // System.out.println("method " + name + " is called thru interface");
      polymorphic = true;
    }
    
    if (opcode == Opcodes.INVOKEVIRTUAL) {
      // check callee to see if it is an abstract method
      // (from an abstract class)
      // (NOTE: This is comparatively heavyweight analysis,
      // so we only perform it if we have not yet established
      // that the method issues any polymorphic calls.
      if (!polymorphic) {
	try {
	  ClassReader testAbstractCR = new ClassReader(owner);
	  TestForAbstractMethod testAbsMethod = new
	    TestForAbstractMethod(name, desc);
	  testAbstractCR.accept(testAbsMethod, ClassReader.SKIP_DEBUG);
	  
	  if (testAbsMethod.getIsAbstractMethod()) {
	    polymorphic = true;
	  }
	}
	catch (Exception e) {
	  // bail out - assume this is not
	  // an abstract method call
	  
	  // reasons for bailout?
	  // 1) we cannot locate the class containing
	  //    callee method.
	}
      }
    }
    

  } // visitMethodInsn()

  /*
   * getter method for polymorphic
   * property
   */
  public boolean isPolymorphic() {
    return this.polymorphic;
  }
  
}