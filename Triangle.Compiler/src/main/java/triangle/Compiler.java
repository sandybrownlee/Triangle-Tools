/*
 * @(#)Compiler.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package triangle;

import triangle.abstractSyntaxTrees.Program;
import triangle.codeGenerator.Emitter;
import triangle.codeGenerator.Encoder;
import triangle.contextualAnalyzer.Checker;
import triangle.optimiser.ConstantFolder;
import triangle.optimiser.SummaryStatisticsGenerator; //Task 6
import triangle.syntacticAnalyzer.Parser;
import triangle.syntacticAnalyzer.Scanner;
import triangle.syntacticAnalyzer.SourceFile;
import triangle.treeDrawer.Drawer;

//Task 1b implementation.
import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {


	// the cli parser library lets us specify command line arguments for the program

	/** The filename for the object program, normally obj.tam. */
	@Argument(alias = "object", description="The filename for the object program, normally obj.tam.")
	static String objectName = "obj.tam";

	@Argument(alias = "tree", description = "Show the programs Abstract Syntax Tree. Dont execute it")
	static boolean showTree = false;

	@Argument(alias = "fold", description = "Perform folding on the program.")
	static boolean folding = false;

	@Argument(alias = "foldTree", description = "Execute folding first and then display the Abstract Syntax Tree.")
	static boolean foldThenShowTree = false;

	//Task 6
	@Argument(alias = "stats", description = "Generate summary statistics on the amount of Binary Expressions, If Commands and While Commands.")
	static boolean GenerateSummaryStatics = false;


	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawer;
	private static SummaryStatisticsGenerator summarystatisticsgenerator; //Task 6

	/** The AST representing the source program. */
	private static Program theAST;

	/**
	 * Compile the source program to TAM machine code.
	 *
	 * @param sourceName   the name of the file containing the source program.
	 * @param objectName   the name of the file containing the object program.
	 * @param showingAST   true iff the AST is to be displayed after contextual
	 *                     analysis
	 * @param foldThenShowTree true if the AST is to be displayed after folding is complete
	 * @param showingTable true if the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true iff the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean foldThenShowTree, boolean showingTable) {

		System.out.println("********** " + "Triangle Compiler (Java Version 2.1)" + " **********");

		System.out.println("Syntactic Analysis ...");
		SourceFile source = SourceFile.ofPath(sourceName);

		if (source == null) {
			System.out.println("Can't access source file " + sourceName);
			System.exit(1);
		}

		scanner = new Scanner(source);
		reporter = new ErrorReporter(false);
		parser = new Parser(scanner, reporter);
		checker = new Checker(reporter);
		emitter = new Emitter(reporter);
		encoder = new Encoder(emitter, reporter);
		drawer = new Drawer();
		summarystatisticsgenerator = new SummaryStatisticsGenerator(); //Task 6

		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {

			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass
			if (!foldThenShowTree && showingAST) {
				drawer.draw(theAST);
			}

			if (folding) {
				theAST.visit(new ConstantFolder());
				if (foldThenShowTree && showingAST){
					drawer.draw(theAST);
				}
			}

			if(GenerateSummaryStatics){
			   System.out.println("Generating Summary Statistics...");
			   theAST.visit(summarystatisticsgenerator);
			   summarystatisticsgenerator.printSummaryStatistics();
				}

			if (reporter.getNumErrors() == 0) {
				System.out.println("Code Generation ...");
				encoder.encodeRun(theAST, showingTable); // 3rd pass
			}
		}

		boolean successful = (reporter.getNumErrors() == 0);
		if (successful) {
			emitter.saveObjectProgram(objectName);
			System.out.println("Compilation was successful.");
		} else {
			System.out.println("Compilation was unsuccessful.");
		}
		return successful;
	}

	/**
	 * Triangle compiler main program.
	 *
	 * @param args the only command-line argument to the program specifies the
	 *             source filename.
	 */
	public static void main(String[] args) {

		Args.parseOrExit(Compiler.class, args);

		String sourceName = args[0];

		var compiledOK = compileProgram(sourceName, objectName, showTree, foldThenShowTree, false);

		if (!showTree && !foldThenShowTree) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
}
