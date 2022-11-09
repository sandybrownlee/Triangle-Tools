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
import triangle.optimiser.StatsFolder;
import triangle.optimiser.StatsTable;
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

	/** The filename for the object program, normally obj.tam. */

	@Argument(alias = "-o", description = "String; name of the object, use .tam format ", required = true)
	static String objectName;

	@Argument(alias = "tree", description = "Boolean; whether to display the tree", required = false)
	static boolean show1Tree = false;

	@Argument(alias = "tree2", description = "Boolean; whether to display the tree after folding", required = false)
	static boolean show2Tree = false;

	@Argument(alias = "folding", description = "Boolean, whether to perform constant folding", required = false)
	static boolean folding = false;

	@Argument(alias = "stats", description = "Boolean; whether to show the statistics", required = false)
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
	 * @param showingAST  true if the AST is to be displayed after contextual
	 *                     analysis
	 * @param folding	   true if call also runs folding
	 * @param showingTable true if the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true if the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean folding, boolean showingTable) {

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
		StatsTable useCount = new StatsTable();
		StatsFolder statsChecker = new StatsFolder(useCount);

		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {

			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass

			if (folding) {
				theAST.visit(new ConstantFolder());
			}
			if (showingAST) {
				drawer.draw(theAST);
			}
			// Check through the AST and assemble statistics
			if (stats) {
				statsChecker.count(theAST);
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

		if (stats) { // New check - to be printed at the very end
			System.out.println("\n~ The Statistics of " + objectName.substring(0, objectName.length()-4) + ".tri ~");
			System.out.print("Binary Expressions:\t");
			System.out.println(useCount.retrieve("BinaryExpression"));
			System.out.print("If Commands:\t\t");
			System.out.println(useCount.retrieve("IfCommand"));
			System.out.print("While Commands:\t\t");
			System.out.println(useCount.retrieve("WhileCommand"));
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

		Args.parseOrExit(Compiler.class, args).toArray(new String[3]);

		String sourceName = args[0];

		// If o n l y 'tree2' specified, show one tree but folded
		if (show1Tree == false && show2Tree == true) {
			show1Tree = true;
			show2Tree = false;
			folding = true;
		}

		var compiledOK = compileProgram(sourceName, objectName, show1Tree, folding, false);
		if (show2Tree) { // Opposite of previous folding's value
			compileProgram(sourceName, objectName, show1Tree, !folding, false);
		}

		if (!show1Tree) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
}
