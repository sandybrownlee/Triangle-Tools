/*
 * @(#)IdEntry.java                        2.1 2003/10/07
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

public class WhileEntry {

	protected String vName;
	protected String v1Value;
	protected String v2Value;

	WhileEntry(String vName, String v1Value, String v2Value) {
		this.vName = vName;
		this.v1Value = v1Value;
		this.v2Value = v2Value;
	}
}
