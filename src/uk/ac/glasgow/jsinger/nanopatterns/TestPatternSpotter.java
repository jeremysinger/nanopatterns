// TestPatternSpotter.java
// Jeremy Singer
// 10 Nov 08

package uk.ac.glasgow.jsinger.nanopatterns;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class TestPatternSpotter {

	/** Analyser modes (helps reusing existing methods) **/
	public static final int ANALYSE_CLASS_FILE = 0;
	public static final int ANALYSE_JAR_FILE = 1;
	public static final int ANALYSE_CLASS_FROM_CLASSPATH = 2;
	public static final int ANALYSE_METHOD_FROM_CLASSPATH = 3;
	public static final int TEST_MODE = 4;

	public static int mode;
	
	/** Argument - class or jar file, class or method name **/
	private static String arg;

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("usage: java TestPatternSpotter classname\n (or) java TestPatternSpotter -help\n (or) java -jar np.jar classname|-help\n");
			System.exit(-1);
		}

		if (args[0].equals("-help")) {
			System.out.println("This program (either TestPatternSpotter class or \nnp.jar Java archive) detects fundamental nano-patterns in bytecode \nclass files. See the paper at \n  http://www.dcs.gla.ac.uk/~jsinger/pdfs/nanopatterns.pdf \nfor more details about nano-patterns.");
			System.exit(0);
		}

		System.out.println("class method typesig numInstrs noparams void recursive samename leaf objCreator thisInstanceFieldReader thisInstanceFieldWriter otherInstanceFieldReader otherInstanceFieldWriter staticFieldReader staticFieldWriter typeManipulator straightLine looper switcher exceptions localReader localWriter arrCreator arrReader arrWriter polymorphic singleReturner multipleReturner client jdkClient tailCaller");

		for (int i = 0; i < args.length; i++) {

			arg = args[i];

			if (arg.endsWith(".class")) {
				mode = ANALYSE_CLASS_FILE;
				analyseClassFile(arg);
				continue;
			}

			if (arg.endsWith(".jar")) {
				mode = ANALYSE_JAR_FILE;
				scanJar(arg);
				continue;
			}

			if (arg.contains(":")) {
				mode = ANALYSE_METHOD_FROM_CLASSPATH;
				scanClassPath();
				continue;
			}

			mode = ANALYSE_CLASS_FROM_CLASSPATH;

			if (arg.charAt(0) == 'L') {
				arg = arg.substring(1, arg.length());
			}
			arg = arg.replace(".", "/");
			arg = arg.replace("\\", "/");
			scanClassPath();
		}

	}

	/** SCANNERS **/

	public static void scanClassPath() {
		String list = System.getProperty("java.class.path");
		for (String path : list.split(";")) {
			File thing = new File(path);
			if (thing.isDirectory()) {
				scanDirectory(thing);
			} else if (path.endsWith(".class")) {
				analyseClassFile(path);
			} else if (path.endsWith(".jar")) {
				scanJar(path);
			}
		}
	}

	public static void scanDirectory(File directory) {
		for (String entry : directory.list()) {
			String path = directory.getPath() + "\\" + entry;
			File thing = new File(path);
			if (thing.isDirectory()) {
				scanDirectory(thing);
			} else if (thing.isFile() && path.endsWith(".class")) {
				analyseClassFile(path);
			} else if (thing.isFile() && path.endsWith(".jar")) {
				scanJar(path);
			}
		}
	}

	public static void scanJar(String path) {
		try {
			JarFile jar = new JarFile(path);
			Enumeration<JarEntry> enums = jar.entries();
			while (enums.hasMoreElements()) {
				JarEntry file = enums.nextElement();
				if (!file.isDirectory() && file.getName().endsWith(".class")) {
					analyseInputStream(jar.getInputStream(file));
				}
			}
			jar.close();
		} catch (IOException e) {
			System.out.println("Failed to open following JAR file: " + path);
		}
	}

	/** ANALYSERS **/

	public static void analyseClassFile(String path) {
		try {
			FileInputStream f = new FileInputStream(path);
			analyseInputStream(f);
		} catch (IOException e) {
			System.out.println("File was not found: " + path);
		}
	}

	public static void analyseInputStream(InputStream is) {
		try {
			ClassReader cr = new ClassReader(is);
			ClassNode cn = new ClassNode();
			cr.accept(cn, ClassReader.SKIP_DEBUG);

			if (mode == ANALYSE_CLASS_FROM_CLASSPATH && !cn.name.equals(arg)) {
				return;
			}

			List methods = cn.methods;
			for (int i = 0; i < methods.size(); ++i) {
				analyseMethod(cn, (MethodNode) methods.get(i));
			}
		} catch (IOException e) {
		}
	}

	public static PatternList analyseMethod(ClassNode cn, MethodNode method) {
		try {
			if (mode == ANALYSE_METHOD_FROM_CLASSPATH) {
				String s = method.name + ":" + method.desc;
				if (!arg.equals(s))
					return null;
			}
	
			RecursivePatternSpotter rps = new RecursivePatternSpotter(
					new EmptyVisitor(), cn.name, method.name, method.desc);
			OOAccessPatternSpotter ops = new OOAccessPatternSpotter(
					new EmptyVisitor());
			TypeManipulatorPatternSpotter tps = new TypeManipulatorPatternSpotter(
					new EmptyVisitor());
			ControlFlowPatternSpotter cps = new ControlFlowPatternSpotter(
					new EmptyVisitor());
			ArrayAccessPatternSpotter aps = new ArrayAccessPatternSpotter(
					new EmptyVisitor());
			PolymorphicPatternSpotter pps = new PolymorphicPatternSpotter(
					new EmptyVisitor());
			ReturnPatternSpotter retps = new ReturnPatternSpotter(
					new EmptyVisitor());
			MethodPatternSpotter mps = new MethodPatternSpotter(new EmptyVisitor());
			// check following
			// properties directly from
			// method descriptor
			boolean noParams = false;
			boolean noReturn = false;
			boolean throwsExceptions = false;
			if (method.desc.startsWith("()")) {
				noParams = true;
			}
			if (method.desc.endsWith(")V")) {
				noReturn = true;
			}
			if (method.exceptions.size() > 0) {
				throwsExceptions = true;
			}
			if (method.instructions.size() > 0) {
				for (int j = 0; j < method.instructions.size(); ++j) {
					Object insn = method.instructions.get(j);
					((AbstractInsnNode) insn).accept(rps);
					((AbstractInsnNode) insn).accept(ops);
					((AbstractInsnNode) insn).accept(tps);
					((AbstractInsnNode) insn).accept(cps);
					((AbstractInsnNode) insn).accept(aps);
					((AbstractInsnNode) insn).accept(mps);
	
				}
				int numInstrs = method.instructions.size();
				System.out.print("" + cn.name + " " + method.name + " "
						+ method.desc + " " + numInstrs);
	
				PatternList resultList = new PatternList();
				resultList.noParams = noParams;
				resultList.noReturn = noReturn;
				resultList.isRecursive = rps.isRecursive();
			    resultList.isSameNameCaller = rps.isSameNameCaller();
			    resultList.isLeaf = rps.isLeaf();
			    resultList.isObjectCreator = ops.isObjectCreator();
			    resultList.isThisInstanceFieldReader = ops.isThisInstanceFieldReader();
			    resultList.isThisInstanceFieldWriter = ops.isThisInstanceFieldWriter();
			    resultList.isOtherInstanceFieldReader = ops.isOtherInstanceFieldReader();
			    resultList.isOtherInstanceFieldWriter = ops.isOtherInstanceFieldWriter();
			    resultList.isStaticFieldReader = ops.isStaticFieldReader();
			    resultList.isStaticFieldWriter = ops.isStaticFieldWriter();
			    resultList.isTypeManipulator = tps.isTypeManipulator();
			    resultList.isStraightLineCode = cps.isStraightLineCode();
			    resultList.isLoopingCode = cps.isLoopingCode();
			    resultList.isSwitcher = cps.isSwitcher();
			    resultList.throwsExceptions = throwsExceptions;
			    resultList.isLocalVarReader = aps.isLocalVarReader();
			    resultList.isLocalVarWriter = aps.isLocalVarWriter();
			    resultList.isArrayCreator = aps.isArrayCreator();
			    resultList.isArrayReader= aps.isArrayReader();
			    resultList.isArrayWriter = aps.isArrayWriter();
			    resultList.isPolymorphic = pps.isPolymorphic();
			    resultList.isSingleReturner= retps.isSingleReturner();
			    resultList.isMultipleReturner= retps.isMultipleReturner();
			    resultList.isClient = mps.isClient();
			    resultList.isJdkClient = mps.isJdkClient();
			    resultList.isTailCaller = mps.isTailCaller();
			    resultList.printResultsAsBooleans();
				System.out.println("");
				return resultList;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}
