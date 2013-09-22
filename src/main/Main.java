package main;


public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		Engine e = new Engine("2");
		
		e.join("TestRoom");
		e.send("User 2 says yo!");
		
		e.startThread();
		
		
		/*
		Create Engine for DH
		DHEngine e = new DHEngine();
		
		int[] intArray = new int[10];
		for(int i = 0 ; i<10; i++){
			e.generate();
			e.go();
			intArray[i] = e.GetValue();
			Thread.sleep(300);
		}
		
		for (int i: intArray){
			System.out.println(i);
		}
		
		*/
	}
	
}