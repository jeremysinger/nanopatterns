public class Foo {

    private boolean testField;
    
    int f(int x) {
	return g();
    }

    int g() {
	int x = g();
	return 2*x;
    }

    void h() {
      h(42);
    }
  
  void h(int x) {
  }

    void j() {
	Object o = new Object();
	if (o instanceof Foo) {
	    Foo f = (Foo)o;
	    f.g();
	}
    }

    void k() throws Exception {
	testField = true;
	throw new Exception();
    }

    void l() {
	boolean b = testField;
    }

    void looper() {
	for(int i=0; i<10; i++) {
	    System.out.println("" + i);
	}

	while (testField != true) {
	    testField |= testField;
	}
    }
}
