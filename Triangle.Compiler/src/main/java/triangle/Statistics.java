package triangle;

/**
 * Class to store and display statistics about the compiled triangle program
 * 
 * @version 1.0 06/11/2022
 * @author Alexander Crowe
 */
public class Statistics {
	
	// stat counter declaration 
	private static int binExpAmount = 0;
	private static int ifComAmount = 0;
	private static int whileComAmount = 0;
	
	/**
	 * Print the counted stats to console
	 */
	public void printStats() {
		System.out.println("*-----Program Statistics-----*");
		System.out.println("Binary Expressions: " + binExpAmount);
		System.out.println("If Commands: " + ifComAmount);
		System.out.println("While loops: " + whileComAmount);
		System.out.println("*-----------End--------------*");
	}
	
	/**
	 * Increment binary expression counter
	 */
	public static void binExpVisited() {
		binExpAmount++;
	}
	
	/**
	 * Increment if command counter
	 */
	public static void ifComVisited() {
		ifComAmount++;
	}
	
	/**
	 * Increment while command counter
	 */
	public static void whileComVisited() {
		whileComAmount++;
	}
	
}
