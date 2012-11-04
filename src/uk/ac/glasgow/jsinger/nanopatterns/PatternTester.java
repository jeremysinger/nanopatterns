package uk.ac.glasgow.jsinger.nanopatterns;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class PatternTester {

	private static Map<String, MethodNode> methodMap;
	private static String testClassName;
	private static ClassNode classNode;
	
	
	/** Loads methods from test class once before all tests **/
	
	@BeforeClass
	public static void setUpMethodMap() {
		testClassName = "uk.ac.glasgow.jsinger.nanopatterns.Foo";
		
		TestPatternSpotter.mode = TestPatternSpotter.TEST_MODE;
		methodMap = new HashMap<String, MethodNode>();
		
		if (testClassName.charAt(0) == 'L') {
			testClassName = testClassName.substring(1, testClassName.length());
		}
		testClassName = testClassName.replace(".", "/");
		testClassName = testClassName.replace("\\", "/");
		scanClassPath();
	}
	
	/** SCANNERS **/

	public static void scanClassPath() {
		String list = System.getProperty("java.class.path");
		for (String path : list.split(";")) {
			File thing = new File(path);
			if (thing.isDirectory()) {
				scanDirectory(thing);
			} else if (path.endsWith(".class")) {
				scanClassFile(path);
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
				scanClassFile(path);
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
					scanInputStream(jar.getInputStream(file));
				}
			}
			jar.close();
		} catch (IOException e) {
			System.out.println("Failed to open following JAR file: " + path);
		}
	}

	public static void scanClassFile(String path) {
		try {
			FileInputStream f = new FileInputStream(path);
			scanInputStream(f);
		} catch (IOException e) {
			System.out.println("File was not found: " + path);
		}
	}

	public static void scanInputStream(InputStream is) {
		try {
			ClassReader cr = new ClassReader(is);
			ClassNode cn = new ClassNode();
			cr.accept(cn, ClassReader.SKIP_DEBUG);
			
			if (!cn.name.equals(testClassName)) {
				return;
			}
			classNode = cn; 
			List methods = cn.methods;
			for (int i = 0; i < methods.size(); ++i) {
				MethodNode method = (MethodNode) methods.get(i);
				String methodKey = method.name + ":" + method.desc;
				methodMap.put(methodKey, method);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** TESTS **/
	
	@Test
	public void testNoParams() {
		String methodName = "g:()I";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertTrue(resultList.noParams);
		} else {
			fail("Program did not execute properly");
		}
	}

	@Test
	public void testHasParams() {
		String methodName = "f:(I)I";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertFalse(resultList.noParams);
		} else {
			fail("Program did not execute properly");
		}
	}

	@Test
	public void testNoReturn() {
		String methodName = "h:()V";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertTrue(resultList.noReturn);
		} else {
			fail("Program did not execute properly");
		}
	}

	@Test
	public void testHasReturn() {
		String methodName = "f:(I)I";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertFalse(resultList.noReturn);
		} else {
			fail("Program did not execute properly");
		}
	}

	@Test
	public void testIsRecursive() {
		String methodName = "g:()I";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertTrue(resultList.isRecursive);
		} else {
			fail("Program did not execute properly");
		}
	}

	@Test
	public void testIsNotRecursive() {
		String methodName = "f:(I)I";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertFalse(resultList.isRecursive);
		} else {
			fail("Program did not execute properly");
		}
	}
	
	@Test
	public void testIsSameNameCaller() {
		String methodName = "h:()V";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertTrue(resultList.isSameNameCaller);
		} else {
			fail("Program did not execute properly");
		}
	}
	
	@Test
	public void testNotSameNameCaller() {
		String methodName = "f:(I)I";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertFalse(resultList.isSameNameCaller);
		} else {
			fail("Program did not execute properly");
		}
	}
	
	@Test
	public void testIsLeaf() {
		String methodName = "h:(I)V";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertTrue(resultList.isLeaf);
		} else {
			fail("Program did not execute properly");
		}
	}
	
	@Test
	public void testNotLeaf() {
		String methodName = "h:()V";
		MethodNode method = methodMap.get(methodName);
		PatternList resultList = TestPatternSpotter.analyseMethod(classNode, method);
		if (resultList != null) {
			assertFalse(resultList.isLeaf);
		} else {
			fail("Program did not execute properly");
		}
	}
	

}
