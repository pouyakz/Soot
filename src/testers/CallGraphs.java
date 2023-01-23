package testers;

public class CallGraphs {
	
	public static void main(String[] args) {
			func();
	}
	
	public static void func() {
		new Test1().one();
    }
}

class Test1{
	public void one() {
		two();
	}
	
	public void two() {
		new Test2().three();
	}
	
}

class Test2{
	public void three() {
		new Test3().five();
		four();
	}
	public void four() {
	}
		
}

class Test3{
	public void five() {
		
	}
}




