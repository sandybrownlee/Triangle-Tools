package triangle.syntacticAnalyser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import triangle.ErrorReporter;
import triangle.syntacticAnalyzer.Parser;
import triangle.syntacticAnalyzer.Scanner;
import triangle.syntacticAnalyzer.SourceFile;

import java.io.*;


public class TestScanner {

	@Test
	public void testHi() throws IOException {
		compileExpectSuccess("/hi.tri");

		String actualOutput = readTAMOutput("hi.tam");
		String expectedOutput = "Hi!";
		assertEquals(expectedOutput,actualOutput);
	}

	@Test
	public void testLoopWhile() throws IOException {
		compileExpectSuccess("/loopwhile.tri");

		String actualOutput = readTAMOutput("loopwhile.tam");
		String expectedOutput = "ababababa";
		assertEquals(expectedOutput,actualOutput);

		//Test the folded version of the program
		String actualOutputFolded = readTAMOutput("loopwhilefolded.tam");
		String expectedOutputFolded = "ababababa";
		assertEquals(expectedOutputFolded,actualOutputFolded);
	}

	@Test
	public void testWhileCurly() throws IOException {
		compileExpectSuccess("/while-curly.tri");

		String actualOutput = readTAMOutput("while-curly.tam");
		String expectedOutput = "aaaaa";
		assertEquals(expectedOutput,actualOutput);

		//Test the folded version of the program
		String actualOutputFolded = readTAMOutput("while-curlyfolded.tam");
		String expectedOutputFolded = "aaaaa";
		assertEquals(expectedOutputFolded,actualOutputFolded);
	}
	@Test
	public void testDecrement() throws IOException {
		compileExpectSuccess("/decrement.tri");

		//Test with the number to decrement being 5
		String actualOutput = readTAMOutput("dectestpositivenum.tam");
		String expectedOutput = "43";
		assertEquals(expectedOutput,actualOutput);

		//Test with the number to decrement being 0
		String actualOutput2 = readTAMOutput("dectestzero.tam");
		String expectedOutput2 = "-1-2";
		assertEquals(expectedOutput2,actualOutput2);

		//Test with the number to decrement being -5
		String actualOutput3 = readTAMOutput("dectestnegativenum.tam");
		String expectedOutput3 = "-6-7";
		assertEquals(expectedOutput3,actualOutput3);

		//TEST THE FOLDED VERSION OF THE PROGRAMS

		//Test with the number to decrement being 5
		String actualOutputFolded = readTAMOutput("dectestpositivenumfolded.tam");
		String expectedOutputFolded = "43";
		assertEquals(expectedOutputFolded,actualOutputFolded);

		//Test with the number to decrement being 0
		String actualOutputFolded2 = readTAMOutput("dectestzerofolded.tam");
		String expectedOutputFolded2 = "-1-2";
		assertEquals(expectedOutputFolded2,actualOutputFolded2);

		//Test with the number to decrement being -5
		String actualOutputFolded3 = readTAMOutput("dectestnegativenumfolded.tam");
		String expectedOutputFolded3 = "-6-7";
		assertEquals(expectedOutputFolded3,actualOutputFolded3);

		//Test with the number being above the word limit
		String actualOutput4 = readTAMOutput("decrementoverflow.tam");
		String expectedOutput4 = "Program has failed due to overflow.";
		assertEquals(expectedOutput4,actualOutput4);

	}

	@Test
	public void testHiNewComment() {
		compileExpectFailure("/hi-newcomment.tri");
	}
	

	@Test
	public void testHiNewComment2() {
		compileExpectFailure("/hi-newcomment2.tri");
	}
	

	@Test
	public void testBarDemo() {
		compileExpectSuccess("/bardemo.tri");
	}
	

	@Test
	public void testRepeatUntil() {
		compileExpectFailure("/repeatuntil.tri");
	}

	
	private void compileExpectSuccess(String filename) {
		// build.gradle has a line sourceSets.test.resources.srcDir file("$rootDir/programs")
		// which adds the programs directory to the list of places Java can easily find files
		// getResource() below searches for a file, which is in /programs 
		//SourceFile source = SourceFile.ofPath(this.getClass().getResource(filename).getFile().toString());
		SourceFile source = SourceFile.fromResource(filename);
		
		Scanner scanner = new Scanner(source);
		ErrorReporter reporter = new ErrorReporter(true);
		Parser parser = new Parser(scanner, reporter);
		
		parser.parseProgram();
		
		// we should get to here with no exceptions
		
		assertEquals("Problem compiling " + filename, 0, reporter.getNumErrors());
	}
	
	private void compileExpectFailure(String filename) {
		//SourceFile source = SourceFile.ofPath(this.getClass().getResource(filename).getFile().toString());
		SourceFile source = SourceFile.fromResource(filename);
		Scanner scanner = new Scanner(source);
		ErrorReporter reporter = new ErrorReporter(true);
		Parser parser = new Parser(scanner, reporter);

		// we expect an exception here as the program has invalid syntax
		assertThrows(RuntimeException.class, new ThrowingRunnable() {
			public void run(){
				parser.parseProgram();
			}
		});
		
		// currently this program will fail
		assertNotEquals("Problem compiling " + filename, 0, reporter.getNumErrors());
	}



	private String readTAMOutput(String filename) throws IOException {

		//This creates a new process which is meant to enter the command prompt and execute the commands to run the program tam files
		//Since the build.gradle file sets the programs directory as test directory we have to call "cd.." to go back to the main directory first
		ProcessBuilder commandPrompt = new ProcessBuilder(
				"cmd.exe", "/c", "cd.. && java -cp build/libs/Triangle-Tools.jar triangle.abstractMachine.Interpreter" +" " + filename);

		commandPrompt.redirectErrorStream(true);
		Process p = commandPrompt.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String programOutput = "";
		String currentLine = "";
        Boolean stopReadingOutput = false;

		while (!(stopReadingOutput)) {
			currentLine = r.readLine();

			if (currentLine.equals("********** TAM Interpreter (Java Version 2.1) **********")) {
				    currentLine = r.readLine(); //Discard the line containing TAM Interpreter (Java Version 2.1)
				while(!(currentLine.equals("Program has halted normally."))) {

					if(currentLine.equals("Program has failed due to overflow.")){
						return currentLine;
					}
					programOutput = programOutput.concat(currentLine); // Add each line to the output String
					currentLine = r.readLine();
				}
				stopReadingOutput = true; //Upon reading all the file output stop reading the rest of the lines
			}
		}
		return programOutput;
	}
}
