// TestForAbstractMethod.java
// Jeremy Singer
// 10 Nov 08

package uk.ac.glasgow.jsinger.nanopatterns;

import org.objectweb.asm.*;

// Search for a single method, with given name and
// signature. Check to see whether this method is
// abstract. Use this code to detect abstract callee
// methods in RecursivePatternSpotter

public class TestForAbstractMethod implements ClassVisitor {

  // meta-data for the method we want to check,
  // to see whether it is abstract or not
  private String methodName;
  private String methodSig;

  private boolean isAbstractMethod;

  public TestForAbstractMethod(String methodName, String methodSig) {
    this.methodName = methodName;
    this.methodSig = methodSig;
    isAbstractMethod = false;
  }

  public boolean getIsAbstractMethod() {
    return this.isAbstractMethod;
  }

    public void visit(int version, int access, String name,
		      String signature, String superName,
		      String [] interfaces) {

    }

    public void visitSource(String source, String debug) {
    }

    public void visitOuterClass(String owner, String name, String desc) {
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
	return null;
    }

    public void visitAttribute(Attribute attr) {
    }

    public void visitInnerClass(String name, String outerName,
				String innerName, int access) {
    }

    public FieldVisitor visitField(int access,
				   String name,
				   String desc,
				   String signature,
				   Object value) {
	return null;
    }

    public MethodVisitor visitMethod(int access,
				     String name,
				     String desc,
				     String signature,
				     String [] exceptions) {

      if (this.methodName.equals(name) &&
	  this.methodSig.equals(desc)) {

	// System.out.println("located method " + name + "." + desc);


	if ((access & Opcodes.ACC_ABSTRACT) != 0) {
	  // System.out.println("-> abstract method");
	  this.isAbstractMethod = true;
	}
      }
	// having found this method, is there any quick
	// way to break out of the visitor?
	
      return null;
      
      
    }
      
    public void visitEnd() {
    }
    
}
