/*
 * @(#)SimpleVname.java                        2.1 2003/10/07
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

package triangle.abstractSyntaxTrees.vnames;

import triangle.abstractSyntaxTrees.terminals.Identifier;
import triangle.abstractSyntaxTrees.visitors.VnameVisitor;
import triangle.syntacticAnalyzer.SourcePosition;

public class SimpleVname extends Vname {

	public SimpleVname(Identifier iAST, SourcePosition position) {
		super(position);
		I = iAST;
	}

	@Override
	public SimpleVname clone() {
		try {
			//Identifier cloned = new Identifier(this.type, this.decl);
			return (SimpleVname) super.clone();
		} catch (CloneNotSupportedException e) {
			return new SimpleVname(this.I, this.getPosition());
		}
	}

	public <TArg, TResult> TResult visit(VnameVisitor<TArg, TResult> v, TArg arg) {
		return v.visitSimpleVname(this, arg);
	}

	public Identifier I;
}
