/*
 * @(#)WhileLoopCommand.java                        2.1 2003/10/07
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package triangle.abstractSyntaxTrees.commands;

import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.syntacticAnalyzer.SourcePosition;

public class LoopWhileCommand extends Command{
	
	public Expression E;
	public final Command C1, C2;
	
	public LoopWhileCommand(Expression eAST, Command cAST_1, Command cAST_2, SourcePosition position) {
		super(position);
		E = eAST;
		C1 = cAST_1;
		C2 = cAST_2;
	}
	
	public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> v, TArg arg) {
		return v.visitLoopWhileCommand(this, arg);
	}
}
