package triangle.abstractSyntaxTrees.commands;

import triangle.abstractSyntaxTrees.terminals.Operator;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.abstractSyntaxTrees.vnames.Vname;
import triangle.syntacticAnalyzer.SourcePosition;

public class UnaryCommand extends Command {

    public UnaryCommand(Vname vAST, Operator oAST, SourcePosition position) {
        super(position);
        V = vAST;
        O = oAST;
    }

    public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> v, TArg arg) {
        return v.visitUnaryCommand(this, arg);
    }

    public final Vname V;
    public final Operator O;
}
