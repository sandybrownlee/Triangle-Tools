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
import triangle.syntacticAnalyzer.Parser;
import triangle.syntacticAnalyzer.Scanner;
import triangle.syntacticAnalyzer.SourceFile;
import triangle.treeDrawer.Drawer;
import triangle.optimiser.SummaryStatistics;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {

	@Argument(alias = "o", description = "the name for the object filename .tam", required = false)
	/** The filename for the object program, normally obj.tam. */
	static String objectName = "obj.tam";
	
	@Argument(alias = "t", description = "if true it prints the Abstract Syntax Tree", required = false)
	static boolean showTree = false;
	
	@Argument(alias = "f", description = "should we fold the program", required = false)
	static boolean folding = false;
	
	//Add additional @Argument whether the program will show the tree after folding the program
	@Argument(alias = "fst", description = "fold program, then show tree", required = false)
	static boolean foldShowTree = false;

	@Argument(alias = "stats", description = "show summary statistics", required = false)
	static boolean stats = false;
	

	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawer;
	private static SummaryStatistics statistics;

	/** The AST representing the source program. */
	private static Program theAST;

	/**
	 * Compile the source program to TAM machine code.
	 *
	 * @param sourceName   the name of the file containing the source program.
	 * @param objectName   the name of the file containing the object program.
	 * @param showingAST   true iff the AST is to be displayed after contextual
	 *                     analysis
	 * @param showingTable true iff the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true iff the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showingTable, boolean foldThenShowTree) {

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
		statistics = new SummaryStatistics();

		// scanner.enableDebugging();
		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {
			// if (showingAST) {
			// drawer.draw(theAST);
			// }
			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass
			
			
			//If folding is chosen
			if (folding || foldThenShowTree) {
				theAST.visit(new ConstantFolder());
			}
			//If showing the tree is chosen
			if (foldThenShowTree) {
				drawer.draw(theAST);
			}
			else if (showingAST) {
				drawer.draw(theAST);
			}
			
			if (reporter.getNumErrors() == 0) {
				System.out.println("Code Generation ...");
				encoder.encodeRun(theAST, showingTable); // 3rd pass
			}
			
			//If showing statistics is chosen
			if (stats) {
				theAST.visit(statistics);
				System.out.println("Summary Statistics: ");
				statistics.printSummaryStatistics();
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

		Compiler compiler = new Compiler();
		
		Args.parseOrExit(compiler, args);

		String sourceName = args[0];
		
		var compiledOK = compileProgram(sourceName, objectName, showTree, false, foldShowTree);

		if (!showTree) {
			System.exit(compiledOK ? 0 : 1);
		}

	}
	
}
