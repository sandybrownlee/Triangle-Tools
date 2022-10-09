/*
 * @(#)TypeDenoter.java                        2.1 2003/10/07
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

package triangle.abstractSyntaxTrees.types;

import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.abstractSyntaxTrees.visitors.TypeDenoterVisitor;
import triangle.abstractSyntaxTrees.syntacticAnalyzer.SourcePosition;

public abstract class TypeDenoter extends AbstractSyntaxTree {

	public TypeDenoter(SourcePosition position) {
		super(position);
	}

	@Override
	public abstract boolean equals(Object obj);

	public abstract <TArg, TResult> TResult visit(TypeDenoterVisitor<TArg, TResult> visitor, TArg arg);

	public <TArg, TResult> TResult visit(TypeDenoterVisitor<TArg, TResult> visitor) {
		return visit(visitor, null);
	}
	
	public abstract int getSize();
}
