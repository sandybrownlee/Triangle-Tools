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
import triangle.optimiser.Statistics;
import triangle.syntacticAnalyzer.Parser;
import triangle.syntacticAnalyzer.Scanner;
import triangle.syntacticAnalyzer.SourceFile;
import triangle.treeDrawer.Drawer;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

import java.util.List;

/**
 * The main driver class for the Triangle compiler.
 *
 * @version 2.1 7 Oct 2003
 * @author Deryck F. Brown
 */
public class Compiler {

	/** The source name of the program */
	@Argument(alias = "s", description = "The source name", required = true)
	static String sourceName="";

	/** The filename for the object program, normally obj.tam. */
	@Argument(alias = "o", description = "The filename for the object program", required = false)
	static String objectName = "obj.tam";

	/** The keyword for showing an AST tree diagram */
	@Argument(alias = "t", description = "Show the program's tree instead of executing it", required = false)
	static boolean showTree = false;

	/** The keyword to fold the program before execution. */
	@Argument(alias = "f", description = "Perform folding on the program", required = false)
	static boolean folding = false;

	/** The keyword for showing an AST tree diagram after folding. */
	@Argument(alias = "fT", description = "Show the program's tree after folding instead of executing it", required = false)
	static boolean showFoldedTree = false;

	/** The keyword for showing some statistics generated on the program*/
	@Argument(alias = "stats", description = "Show some program's statistics", required = false)
	static boolean statistics = false;

	private static Scanner scanner;
	private static Parser parser;
	private static Checker checker;
	private static Encoder encoder;
	private static Emitter emitter;
	private static ErrorReporter reporter;
	private static Drawer drawer, foldDrawer;
	private static Statistics statisticsGenerator;

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
	static boolean compileProgram(String sourceName, String objectName, boolean showingAST, boolean showFoldedTree, boolean showingTable) {

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
		foldDrawer = new Drawer();
		statisticsGenerator = new Statistics();

		// scanner.enableDebugging();
		theAST = parser.parseProgram(); // 1st pass
		if (reporter.getNumErrors() == 0) {
			 //if (showingAST) {
			 //drawer.draw(theAST);
			 //}
			System.out.println("Contextual Analysis ...");
			checker.check(theAST); // 2nd pass
			if (showingAST) {
				drawer.draw(theAST);
			}
			if (folding) {
				theAST.visit(new ConstantFolder());
				if (showFoldedTree){
					foldDrawer.draw(theAST);
				}
			}  else if (showFoldedTree) {
				System.out.println("Error: Folded AST can only be printed after folding");
			}
			if (statistics){
				theAST.visit(statisticsGenerator);
				System.out.println(statisticsGenerator.printStatistics());
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

		if (args.length < 1) {
			//System.out.println("Usage: tc filename [-o=outputfilename] [tree] [folding]");
			System.out.println("Usage: tc -s filename [-o=outputfilename] [-t=tree] [-f=folding] [-fT=foldTree]");
			System.exit(1);
		}

		//String [] unparsed = Args.parseOrExit(Loader.class, args).toArray(new String[0]);

		Args.parseOrExit(Compiler.class, args);

		var compiledOK = compileProgram(sourceName, objectName, showTree, showFoldedTree, false);

		if (!showTree && !showFoldedTree) {
			System.exit(compiledOK ? 0 : 1);
		}

	}

}
