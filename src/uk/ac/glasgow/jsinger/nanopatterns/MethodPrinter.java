// MethodPrinter.java
// Jeremy Singer
// 10 Nov 08

package uk.ac.glasgow.jsinger.nanopatterns;

import org.objectweb.asm.*;

// use the PrintMethods wrapper class to get this visitor
// operating on a single class file.

public class MethodPrinter implements ClassVisitor {

    /**
     * fully-qualified class name,
     * includes package prefix
     */
    String fqClassName; 

    public MethodPrinter() {
	fqClassName = "?";
    }

    public void visit(int version, int access, String name,
		      String signature, String superName,
		      String [] interfaces) {

	fqClassName = name;
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
	// print out method name...
	System.out.println("Visiting method " + fqClassName + "." +
			   name + desc);

	if ((access & Opcodes.ACC_ABSTRACT) != 0) {
	  System.out.println("abstract method " + name);
	}

	return null;
    }

    public void visitEnd() {
    }
    
}
