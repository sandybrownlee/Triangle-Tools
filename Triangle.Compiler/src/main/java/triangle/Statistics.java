package triangle;

public class Statistics {

	private static int binExpAmount = 0;
	private static int ifComAmount = 0;
	private static int whileComAmount = 0;
	
	public Statistics() {
		
	}
	
	public void printStats() {
		System.out.println("*-----Program Statistics-----*");
		System.out.println("Binary Expressions: " + binExpAmount);
		System.out.println("If Commands: " + ifComAmount);
		System.out.println("While loops: " + whileComAmount);
		System.out.println("*-----------End--------------*");
	}
	
	public static void binExpVisited() {
		binExpAmount++;
	}
	
	public static void ifComVisited() {
		ifComAmount++;
	}
	
	public static void whileComVisited() {
		whileComAmount++;
	}
	
}
