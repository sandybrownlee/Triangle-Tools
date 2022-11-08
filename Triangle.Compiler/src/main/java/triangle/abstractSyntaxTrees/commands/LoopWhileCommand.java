package triangle.abstractSyntaxTrees.commands;

import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.syntacticAnalyzer.SourcePosition;

public class LoopWhileCommand extends Command {
    public LoopWhileCommand(Command cAST, Expression eAST, Command c2AST, SourcePosition position) {
        super(position);
        E = eAST;
        C1 = cAST;
        C2 = c2AST;
    }

    public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> v, TArg arg) {
        return v.visitLoopWhileCommand(this, arg);
    }

    public Expression E;
    public final Command C1;
    public final Command C2;
}
