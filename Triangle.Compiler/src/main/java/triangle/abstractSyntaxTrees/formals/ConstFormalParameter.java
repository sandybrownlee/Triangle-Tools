/*
 * @(#)ConstFormalParameter.java                        2.1 2003/10/07
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

package triangle.abstractSyntaxTrees.formals;

import triangle.abstractSyntaxTrees.declarations.ConstantDeclaration;
import triangle.abstractSyntaxTrees.terminals.Identifier;
import triangle.abstractSyntaxTrees.types.TypeDenoter;
import triangle.abstractSyntaxTrees.visitors.DeclarationVisitor;
import triangle.abstractSyntaxTrees.syntacticAnalyzer.SourcePosition;

public class ConstFormalParameter extends FormalParameter implements ConstantDeclaration {

	public ConstFormalParameter(Identifier iAST, TypeDenoter tAST, SourcePosition position) {
		super(position);
		I = iAST;
		T = tAST;
	}

	@Override
	public TypeDenoter getType() {
		return T;
	}

	public <TArg, TResult> TResult visit(DeclarationVisitor<TArg, TResult> v, TArg arg) {
		return v.visitConstFormalParameter(this, arg);
	}

	@Override
	public boolean equals(Object fpAST) {
		if (fpAST instanceof ConstFormalParameter) {
			return T.equals(((ConstFormalParameter)fpAST).T);
		} else {
			return false;
		}
	}

	public final Identifier I;
	public TypeDenoter T;
}
