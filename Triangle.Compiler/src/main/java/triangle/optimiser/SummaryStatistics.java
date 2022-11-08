package triangle.optimiser;

public class SummaryStatistics {
	private static int binExpressionCount = 0;
	private static int ifCommandCount = 0;
	private static int whileCommandCount = 0;

	public int getBinExpressionCount() {
		return binExpressionCount;
	}

	public void setBinExpressionCount(int newBinExpressionCount) {
		binExpressionCount = newBinExpressionCount;
	}

	public int getIfCommandCount() {
		return ifCommandCount;
	}

	public void setIfCommandCount(int newIfCommandCount) {
		ifCommandCount = newIfCommandCount;
	}

	public int getWhileCommandCount() {
		return whileCommandCount;
	}

	public void setWhileCommandCount(int newWhileCommandCount) {
		whileCommandCount = newWhileCommandCount;
	}

	public static void addToCount(String var) {
		if (var.equals("bin")) {
			binExpressionCount++;
		}
		if (var.equals("if")) {
			ifCommandCount++;
		}
		if (var.equals("while")) {
			whileCommandCount++;
		}
	}

	public static String printOut() {
		return "The number of BinaryExpressions: " + binExpressionCount + " The number of if commands: "
				+ ifCommandCount + " The while command count: " + whileCommandCount;

	}

}
