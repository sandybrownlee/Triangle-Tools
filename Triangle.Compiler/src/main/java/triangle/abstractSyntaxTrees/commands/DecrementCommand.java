package triangle.abstractSyntaxTrees.commands;

import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.syntacticAnalyzer.SourcePosition;

public class DecrementCommand extends Command {

	/**
	 * @param eAST
	 * @param cAST
	 * @param position
	 */
	
	public DecrementCommand(Expression eAST, Command cAST, SourcePosition position) {
		super(position);
		E = eAST;
		C = eAST - 1;
	}

	public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> v, TArg arg) {
		return v.visitDecrementCommand(this, arg);
	}

	public final Expression E;
	public final Command C;
}
