/*
 * @(#)IdentificationTable.java                2.1 2003/10/07
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

package triangle.optimiser;

import triangle.abstractSyntaxTrees.declarations.Declaration;

public final class StatsTable {

	private int binaryExpressions;
	private int ifCommands;
	private int whileCommands;


	public StatsTable() {
		binaryExpressions = 0;
		ifCommands = 0;
		whileCommands = 0;
	}

	public void addEntry(String kindToCount) {
		if (kindToCount == "BinaryExpression") {
			binaryExpressions++;
		}
		if (kindToCount == "IfCommand") {
			ifCommands++;
		}
		if (kindToCount == "WhileCommand") {
			whileCommands++;
		}
	}

	public int retrieve(String kindToPrint) {
		int count = 0;
		if (kindToPrint == "BinaryExpression") {
			count = binaryExpressions;
		}
		if (kindToPrint == "IfCommand") {
			count = ifCommands;
		}
		if (kindToPrint == "WhileCommand") {
			count = whileCommands;
		}
		return count;
	}

}
