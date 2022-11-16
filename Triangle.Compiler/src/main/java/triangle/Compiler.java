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
import triangle.treeDrawer.Stats;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {
	
	/** The filename for the object program, normally obj.tam. */
	static String objectName = "obj.tam";

	@Argument(alias = "-o", description = "TAM file")
	static String objectFile;
	@Argument(alias = "t", description = "Shows AST")
	static boolean showTree = false;
	@Argument(alias = "t", description = "Shows folded AST")
	static boolean showFoldedTree = false;
	@Argument(alias = "f", description = "Will this tree be folded")
	static boolean folding = false;
	@Argument(alias = "s", description = "Show stats of AST")
	static boolean stats = false;

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
	 * @param showingAST   true if the AST is to be displayed after contextual
	 *                     analysis
	 * @param showingAST2  true if the folded AST is to be displayed after contextual
	 *                     analysis
	 * @param showingTable true if the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true iff the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showingAST2, boolean showingTable) {

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
		Program fold = theAST;
		if (reporter.getNumErrors() == 0) {
			// if (showingAST) {
			// drawer.draw(theAST);
			// }
			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass
			
			if (!folding) {
				if (showingAST && !showingAST2) {
					drawer.draw(theAST);
				} 
				if (!showingAST && showingAST2) {
					drawer.draw(theAST);
					theAST.visit(new ConstantFolder());
				} 
				if (showingAST && showingAST2){
					drawer.draw(theAST);
					drawer.draw(fold);
					fold.visit(new ConstantFolder());
				}
			} else if (folding) {
				if ((showingAST && !showingAST2) || (!showingAST && showingAST2)) {
					drawer.draw(theAST);
					theAST.visit(new ConstantFolder());
				} else {
					drawer.draw(theAST);
					drawer.draw(fold);
					fold.visit(new ConstantFolder());
				}
			}
			
//			if (stats) {
//				theAST.visit(new Stats());
//			}
			
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
		
		if (args.length < 1) {
			System.out.println("Usage: tc filename [-o=outputfilename] [tree] [folding]");
			System.exit(1);
		}
		
		// Loads the arguments using the CLI Parser library and parses them
		Args.parseOrExit(compiler, args);
		
		parseArgs(args);

		String sourceName = args[0];
		
		var compiledOK = compileProgram(sourceName, objectName, showTree, showFoldedTree, false);

		if (!showTree && !showFoldedTree) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
	
	private static void parseArgs(String[] args) {
		for (String s : args) {
			var sl = s.toLowerCase();
			if (sl.equals("tree")) {
				showTree = true;
			} else if (sl.equals("tree2")) {
				showFoldedTree = true;
			} else if (sl.startsWith("-o=")) {
				objectName = s.substring(3);
			} else if (sl.equals("folding")) {
				folding = true;
			} else if (sl.equals("stats")) {
				stats = true;
			}
		}
	}
	
}
