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

//cli-parser libraries
import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {

	/** Command line arguments */
	 @Argument(alias = "tree", description = "Print AST", required = false)
	 	protected String treePrint = "tree";
	 @Argument(alias = "tree2", description = "Print AST after folding", required = false)
	 	protected String foldPrint = "tree2";
	 @Argument(alias = "folding", description = "run folding procedure", required = false)
	 	protected String fold = "folding";
	
	/** The filename for the object program, normally obj.tam. */
	static String objectName = "obj.tam";
	
	/** Argument flags */
	static boolean showTree = false;
	static boolean folding = false;
	static boolean showFoldTree = false;

	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawer;
	private static Drawer drawer2;

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
	 * @param showingFoldAST   true if the AST is to be displayed after folding
	 * @return true if the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showingFoldAST, boolean showingTable) {

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
		drawer2 = new Drawer();

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
				if (showingFoldAST) {
					drawer2.draw(theAST);
				}
			} else if (showingFoldAST) {
				System.out.println("Error: Cannot print folded AST if not folding.");
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
		
		//cli parse arguments
		Args.parseOrExit(compiler, args);
		parseArgs(args);

		String sourceName = args[0];
		
		var compiledOK = compileProgram(sourceName, objectName, showTree, showFoldTree, false);

		//exit if neither tree printed
		if (!showTree && !showFoldTree) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
	
	private static void parseArgs(String[] args) {
		for (String s : args) {
			var sl = s.toLowerCase();
			if (sl.equals("tree")) {
				showTree = true;
			} else if (sl.equals("tree2")) {
				showFoldTree = true;
			} else if (sl.startsWith("-o=")) {
				objectName = s.substring(3);
			} else if (sl.equals("folding")) {
				folding = true;
			}
		}
	}
}
