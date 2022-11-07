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
	// the cli parser library lets us make instance variables with annotations like this
	// that specify command line arguments for the program
	@Argument(alias = "objectFileName", description = "The name of the file containing the object program.")
	static String objectName = "obj.tam";

	@Argument(alias = "showTree", description = "True if the Abstract Syntax Tree is to be displayed after contextual analysis.")
	static boolean showTree = false;

	@Argument(alias = "showTreeAfterFolding", description = "True if the Abstract Syntax Tree is to be displayed after contextual analysis and folding is complete.")
	static boolean showTreeAfterFolding = false;

	@Argument(alias = "isFolding", description = "True if the program is to be folded.")
	static boolean folding = false;

	@Argument(alias = "sourceFileName", description = "The name of the file containing the source program.")
	static String sourceName = "";

	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawer, anotherDrawer;

	/** The AST representing the source program. */
	private static Program theAST;

	/**
	 * Compile the source program to TAM machine code.
	 *
	 * @param sourceName   the name of the file containing the source program.
	 * @param objectName   the name of the file containing the object program.
	 * @param showingAST   true if the AST is to be displayed after contextual
	 *                     analysis
	 * @param showingTable true if the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true if the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showTreeAfterFolding, boolean showingTable) {

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
		anotherDrawer = new Drawer();

		// scanner.enableDebugging();
		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {
			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass
			if (showingAST) {
				drawer.draw(theAST);
			}
			if (folding) {
				theAST.visit(new ConstantFolder());
				if (showTreeAfterFolding) {
					anotherDrawer.draw(theAST);
				}
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
		// this will parse the list of arguments passed into the program, and
		// populate the appropriate instance variables
		// if the required arguments were not found, it will gracefully exit.
		// Takes an instance or a class as first argument,
		// and the arguments you want to pass and populate as second argument
		Args.parseOrExit(Compiler.class, args);

		var compiledOK = compileProgram(sourceName, objectName, showTree, showTreeAfterFolding, false);

		if (!showTree && !showTreeAfterFolding) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
}
