package triangle.abstractSyntaxTrees.commands;

import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.syntacticAnalyzer.SourcePosition;

public class LoopCommand extends Command {
    public LoopCommand(Expression eAST, Command c1AST, Command c2AST, SourcePosition position) {
        super(position);
        E = eAST;
        C1 = c1AST;
        C2 = c2AST;
    }

    public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> v, TArg arg) {
        return v.visitLoopCommand(this, arg);
    }

    public Expression E;
    public final Command C1;
    public final Command C2;
}
