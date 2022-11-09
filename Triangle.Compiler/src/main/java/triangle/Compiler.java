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
import triangle.statsAnalyzer.Statistics;
import triangle.hoister.Hoister;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {

	 /* static String sourceName = null;
	 static String objectName = "obj.tam";
	 static boolean showTree = false;
	 static boolean folding = false;
	 static boolean stats = false; */

	@Argument(required = true, alias = "s", description = "The name of the object file")
	public static String sourceName = "null";
	@Argument(alias = "o", description = "The name of the object file")
	public static String objectName = "obj.tam";
	@Argument(alias = "t", description = "Show the abstract syntax tree")
	public static boolean showTree = false;
	@Argument(alias = "h", description = "Enable hoisting")
	public static boolean hoisting = false;
	@Argument(alias = "f", description = "Enable constant folding")
	public static boolean folding = false;
	@Argument(alias = "S", description = "Show statistics")
	public static boolean stats = false;
	@Argument(alias = "p", description = "Show the abstract syntax tree post-folding")
	public static boolean postShowingAST;

	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawerBefore;
	private static Drawer drawerAfter;
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
	 * @param showingTable true iff the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true iff the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showingTable,
			boolean stats) {

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
		statistics = new Statistics();
		emitter = new Emitter(reporter);
		encoder = new Encoder(emitter, reporter);
		drawerBefore = new Drawer();
		drawerAfter = new Drawer();

		// scanner.enableDebugging();
		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {
			// if (showingAST) {
			// drawer.draw(theAST);
			// }
			System.out.println("Contextual Analysis ...");
			if (showingAST) {
				drawerBefore.draw(theAST);
			}
			checker.check(theAST); // 2nd pass
			if (hoisting) {
				System.out.println("Hoisting ...");
				theAST.visit(new Hoister());
			}
			if (folding) {
				theAST.visit(new ConstantFolder());
			}
			if (postShowingAST) {
				drawerAfter.draw(theAST);
			}
			if (stats) {
				statistics.read(theAST);
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
		Compiler compiler = new Compiler();

		Args.parseOrExit(compiler, args);

		System.out.println("Source file: " + sourceName);

		if (args.length < 1) {
			System.out.println("Usage: tc filename [-o=outputfilename] [tree] [folding]");
			System.exit(1);
		}

		// parseArgs(args);

		// String sourceName = args[0];

		var compiledOK = compileProgram(sourceName, objectName, showTree, false, stats);

		if (!showTree) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
}
