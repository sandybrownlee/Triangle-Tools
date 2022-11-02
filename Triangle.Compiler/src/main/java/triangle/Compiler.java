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

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

import triangle.abstractSyntaxTrees.Program;
import triangle.codeGenerator.Emitter;
import triangle.codeGenerator.Encoder;
import triangle.contextualAnalyzer.Checker;
import triangle.optimiser.ConstantFolder;
import triangle.syntacticAnalyzer.Parser;
import triangle.syntacticAnalyzer.Scanner;
import triangle.syntacticAnalyzer.SourceFile;
import triangle.treeDrawer.Drawer;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {

	@Argument(alias = "i", description = "Input File Name", required = true)
	static String sourceName;
	@Argument(alias = "o", description = "Output File Name", required = false)
	static String objectName = "obj.tam";
	@Argument(alias = "tree", description = "Show the Tree before optimisations occur", required = false)
	static boolean showTree = false;
	@Argument(description = "Use the Constant-Folder optimisation", required = false)
	static boolean folding = false;
	@Argument(alias = "treeAfter",description = "Show the tree after optimisations occur", required = false)
	static boolean showTreeOptimised = false;

	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawer;

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
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showingASTAfter ,boolean showingTable) {

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

		// scanner.enableDebugging();
		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {
			// if (showingAST) {
			// drawer.draw(theAST);
			// }
			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass
			if (showingAST) {
				drawer.draw(theAST);
			}
			if (folding) {
				theAST.visit(new ConstantFolder());
			}
			if (showingASTAfter) {
				drawer.draw(theAST);
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
	 * @param args The only required argument specifies the input file path. The
	 *             optional arguments are for specifying the output file name and
	 *             flags for showing trees or folding.
	 */
	public static void main(String[] args) {
		Compiler compiler = new Compiler();
		Args.parseOrExit(compiler, args);

		var compiledOK = compileProgram(sourceName, objectName, showTree, showTreeOptimised, false);

		if (!showTree && !showTreeOptimised) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
}
