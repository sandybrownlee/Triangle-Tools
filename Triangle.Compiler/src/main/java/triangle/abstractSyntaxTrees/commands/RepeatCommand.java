/*
 * @(#)WhileCommand.java                        2.1 2003/10/07
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package triangle.abstractSyntaxTrees.commands;

import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.syntacticAnalyzer.SourcePosition;

public class RepeatCommand extends Command {

	public RepeatCommand(Expression eAST, Command cAST, SourcePosition position) {
		super(position);
		E = eAST;
		C = cAST;
	}

	public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> v, TArg arg) {
		return v.visitRepeatCommand(this, arg);
	}

	public Expression E;
	public final Command C;
}
