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

import java.util.List;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {

	/** The filename for the object program, normally obj.tam. */
	@Argument(alias = "o", description = "The filename for the object program", required = false)
	static String objectName = "obj.tam";

	/** The flag for producing an AST tree diagram instead of executing it. */
	@Argument(alias = "tree", description = "Show the program's tree instead of executing it", required = false)
	static boolean showTree = false;

	/** The flag for performing folding on the program before execution. */
	@Argument(alias = "folding", description = "Perform folding on the program", required = false)
	static boolean folding = false;

	/** The flag for performing folding before producing an AST tree diagram. */
	@Argument(alias = "foldtree", description = "Perform folding on the program, then show the program's tree", required = false)
	static boolean foldTree = false;

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
	 * @param ASTAfterFold true if the AST is to be displayed after folding
	 * @param showingTable true iff the object description details are to be
	 *                     displayed during code generation (not currently
	 *                     implemented).
	 * @return true if the source program is free of compile-time errors, otherwise
	 *         false.
	 */
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean ASTAfterFold,
			boolean showingTable) {

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
			// As the specification states that a new option is required to perform folding
			// before an AST, the program ensures that using the folding and tree flags
			// together produces an unfolded tree diagram.
			if (!ASTAfterFold && showingAST) {// If the AST is being drawn before any folding
				drawer.draw(theAST);
			}
			if (folding || ASTAfterFold) {// If folding should be required
				theAST.visit(new ConstantFolder());
			}

			if (ASTAfterFold) {// If the AST is being drawn after folding
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
	 * @param args the only command-line argument to the program specifies the
	 *             source filename.
	 */
	public static void main(String[] args) {

		List<String> unparsed = Args.parseOrExit(Compiler.class, args);

		String sourceName = unparsed.get(0);

		var compiledOK = compileProgram(sourceName, objectName, showTree, foldTree, false);

		if (!showTree && !foldTree) {
			System.exit(compiledOK ? 0 : 1);
		}
	}
}
