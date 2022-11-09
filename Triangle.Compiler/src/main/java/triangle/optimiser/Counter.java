/*
 * @(#)Drawer.java                        2.1 2003/10/07
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

import triangle.abstractSyntaxTrees.Program;
import triangle.abstractSyntaxTrees.expressions.IfExpression;
import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.optimiser.ConstantFolder;


public class Counter extends ConstantFolder {

	@Override
	public AbstractSyntaxTree visitProgram(Program ast, Void arg) {
		ast.C.visit(this);
		return null;
	}
	// Draw the AST representing a complete program.

	public void countIf(Program ast) {
		var a = 0;
		while(ast.C.visit(this) != null){
			if(ast.C.visit(this) instanceof IfExpression){
				a=a+1;
			}
		}

		System.out.println(a);
	}
}
