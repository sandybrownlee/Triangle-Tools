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

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {

	/** The name of the program */
	@Argument(alias = "n", description = "File Name", required = true)
	static String sourceName;
	/** The filename for the object program, normally obj.tam. */
	@Argument(alias = "obj", description = "Object filename", required = false)
	static String objectName = "obj.tam";
	/** Display AST tree */
	@Argument(alias = "ast", description = "Display ast", required = false)
	static boolean showAst = false;
	/** Perform optimization with folding */
	@Argument(alias = "fold", description = "Optimization: Folding", required = false)
	static boolean folding = false;
	/** Display AST after folding. */
	@Argument(alias = "fast", description = "Display AST after Folding", required = false)
	static boolean showFoldingAst = false;
	// added
	/** Display statistics */
	@Argument(alias = "stats", description = "Show Statistics", required = false)
	static boolean stats = false;

	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawer;
	private static Drawer foldingDrawer;

	private static Statistics statistics;

	/** The AST representing the source program. */
	private static Program theAST;

	/**
	 * Compile the source program to TAM machine code.
	 *
	 * @param sourceName   the name of the file containing the source program.
	 * @param objectName   the name of the file containing the object program.
	 * @param showingAST   true iff the AST is to be displayed after contextual
	 *                     analysis
	 * @param showingFoldingAST true iff the ast is to be displayed after folding
	 * @param showingTable true iff the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true iff the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showingFoldingAST, boolean showingTable) {

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
		//added
		foldingDrawer = new Drawer();
		statistics = new Statistics();

		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {
			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass
			if (showingAST) {
				drawer.draw(theAST);
			}
			if (folding) {
				theAST.visit(new ConstantFolder());
				// added
				if (showingFoldingAST) {
					foldingDrawer.draw(theAST);
				}
			}

			if (reporter.getNumErrors() == 0) {
				System.out.println("Code Generation ...");
				encoder.encodeRun(theAST, showingTable); // 3rd pass
			}

			// added
			if(stats) {
				theAST.visit(statistics);
				System.out.println("Binary Expressions visited: " + statistics.counterBinaryExpressions);
				System.out.println("If Commands visited: " + statistics.counterIfCommands);
				System.out.println("While Commands visited: " + statistics.counterWhileCommands);
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

		if (args.length < 1) {
			System.out.println("Usage: tc [-n = filename] [-o=outputfilename] [-ast = tree] [-fold = folding] [-fast = foldedAst] [-stats = statistics]");
			System.exit(1);
		}

		Args.parseOrExit(Compiler.class, args);

		var compiledOK = compileProgram(sourceName, objectName, showAst, showFoldingAst, false);

		if (!showAst && !showFoldingAst) {
			System.exit(compiledOK ? 0 : 1);
		}
	}

	/*
	private static void parseArgs(String[] args) {
		for (String s : args) {
			var sl = s.toLowerCase();
			if (sl.equals("tree")) {
				showTree = true;
			} else if (sl.startsWith("-o=")) {
				objectName = s.substring(3);
			}
		}
	} */
}
