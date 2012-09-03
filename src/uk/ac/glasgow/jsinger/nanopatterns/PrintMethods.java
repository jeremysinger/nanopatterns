// PrintMethods.java
// Jeremy Singer
// 21 Apr 09

// lets the MethodPrinter visitor
// visit all elements in a class.
// This is simply a wrapper class.


import org.objectweb.asm.ClassReader;
import java.io.FileInputStream;



public class PrintMethods {

  public static void main(String [] args) {
    
    try {
      if (args.length != 1) {
	System.err.println("usage: java PrintMethods classname");
	System.exit(-1);
      }
      String className = args[0];
      FileInputStream f = new FileInputStream(className);
      ClassReader cr = new ClassReader(f);
      MethodPrinter mp = new MethodPrinter();
      cr.accept(mp, ClassReader.SKIP_DEBUG);
    }
    catch (Exception e) {
      System.err.println(e);
    }
  }
  

}