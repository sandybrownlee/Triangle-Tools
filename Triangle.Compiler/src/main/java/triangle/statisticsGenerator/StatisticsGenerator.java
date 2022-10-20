package triangle.statisticsGenerator;

import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.abstractSyntaxTrees.Program;
import triangle.abstractSyntaxTrees.actuals.*;
import triangle.abstractSyntaxTrees.aggregates.MultipleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.MultipleRecordAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleRecordAggregate;
import triangle.abstractSyntaxTrees.commands.*;
import triangle.abstractSyntaxTrees.declarations.*;
import triangle.abstractSyntaxTrees.expressions.*;
import triangle.abstractSyntaxTrees.formals.*;
import triangle.abstractSyntaxTrees.terminals.CharacterLiteral;
import triangle.abstractSyntaxTrees.terminals.Identifier;
import triangle.abstractSyntaxTrees.terminals.IntegerLiteral;
import triangle.abstractSyntaxTrees.terminals.Operator;
import triangle.abstractSyntaxTrees.types.*;
import triangle.abstractSyntaxTrees.visitors.*;
import triangle.abstractSyntaxTrees.vnames.DotVname;
import triangle.abstractSyntaxTrees.vnames.SimpleVname;
import triangle.abstractSyntaxTrees.vnames.SubscriptVname;

public class StatisticsGenerator implements ActualParameterVisitor<Void, AbstractSyntaxTree>,
        ActualParameterSequenceVisitor<Void, AbstractSyntaxTree>, ArrayAggregateVisitor<Void, AbstractSyntaxTree>,
        CommandVisitor<Void, AbstractSyntaxTree>, DeclarationVisitor<Void, AbstractSyntaxTree>,
        ExpressionVisitor<Void, AbstractSyntaxTree>, FormalParameterSequenceVisitor<Void, AbstractSyntaxTree>,
        IdentifierVisitor<Void, AbstractSyntaxTree>, LiteralVisitor<Void, AbstractSyntaxTree>,
        OperatorVisitor<Void, AbstractSyntaxTree>, ProgramVisitor<Void, AbstractSyntaxTree>,
        RecordAggregateVisitor<Void, AbstractSyntaxTree>, TypeDenoterVisitor<Void, AbstractSyntaxTree>,
        VnameVisitor<Void, AbstractSyntaxTree> {
    {

    }

    private int binaryCount = 0;
    private int ifCount = 0;
    private int whileCount = 0;

    public String toString() {
        return "Statistics report:"
                + "\n  - Binary Expressions: " + binaryCount
                + "\n  - If Commands: " + ifCount
                + "\n  - While Commands: " + whileCount;
    }

    @Override
    public AbstractSyntaxTree visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Void unused) {
        ast.AP.visit(this);
        ast.APS.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleActualParameterSequence(SingleActualParameterSequence ast, Void unused) {
        ast.AP.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitConstActualParameter(ConstActualParameter ast, Void unused) {
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitFuncActualParameter(FuncActualParameter ast, Void unused) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitProcActualParameter(ProcActualParameter ast, Void unused) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVarActualParameter(VarActualParameter ast, Void unused) {
        ast.V.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleArrayAggregate(MultipleArrayAggregate ast, Void unused) {
        ast.AA.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleArrayAggregate(SingleArrayAggregate ast, Void unused) {
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitAssignCommand(AssignCommand ast, Void unused) {
        ast.E.visit(this);
        ast.V.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCallCommand(CallCommand ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitEmptyCommand(EmptyCommand ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIfCommand(IfCommand ast, Void unused) {
        ifCount++;
        ast.C1.visit(this);
        ast.C2.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitLetCommand(LetCommand ast, Void unused) {
        ast.C.visit(this);
        ast.D.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitRepeatCommand(RepeatCommand ast, Void unused) {
        ast.C.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSequentialCommand(SequentialCommand ast, Void unused) {
        ast.C1.visit(this);
        ast.C2.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitWhileCommand(WhileCommand ast, Void unused) {
        whileCount++;
        ast.C.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitLoopWhileCommand(LoopWhileCommand ast, Void unused) {
        ast.C1.visit(this);
        ast.E.visit(this);
        ast.C2.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Void unused) {
        ast.ARG1.visit(this);
        ast.ARG2.visit(this);
        ast.O.visit(this);
        ast.RES.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitConstDeclaration(ConstDeclaration ast, Void unused) {
        ast.E.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitFuncDeclaration(FuncDeclaration ast, Void unused) {
        ast.E.visit(this);
        ast.FPS.visit(this);
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitProcDeclaration(ProcDeclaration ast, Void unused) {
        ast.C.visit(this);
        ast.FPS.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSequentialDeclaration(SequentialDeclaration ast, Void unused) {
        ast.D1.visit(this);
        ast.D2.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitTypeDeclaration(TypeDeclaration ast, Void unused) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Void unused) {
        ast.ARG.visit(this);
        ast.O.visit(this);
        ast.RES.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVarDeclaration(VarDeclaration ast, Void unused) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitArrayExpression(ArrayExpression ast, Void unused) {
        ast.AA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitBinaryExpression(BinaryExpression ast, Void unused) {
        binaryCount++;
        ast.E1.visit(this);
        ast.E2.visit(this);
        ast.O.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCallExpression(CallExpression ast, Void unused) {
        ast.APS.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCharacterExpression(CharacterExpression ast, Void unused) {
        ast.CL.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitEmptyExpression(EmptyExpression ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIfExpression(IfExpression ast, Void unused) {
        ast.E1.visit(this);
        ast.E2.visit(this);
        ast.E3.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIntegerExpression(IntegerExpression ast, Void unused) {
        return ast;
    }

    @Override
    public AbstractSyntaxTree visitLetExpression(LetExpression ast, Void unused) {
        ast.D.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitRecordExpression(RecordExpression ast, Void unused) {
        ast.RA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitUnaryExpression(UnaryExpression ast, Void unused) {
        ast.E.visit(this);
        ast.O.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVnameExpression(VnameExpression ast, Void unused) {
        ast.V.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Void unused) {
        ast.FT.visit(this);
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Void unused) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Void unused) {
        ast.FP.visit(this);
        ast.FPS.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Void unused) {
        ast.FP.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitConstFormalParameter(ConstFormalParameter ast, Void unused) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitFuncFormalParameter(FuncFormalParameter ast, Void unused) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitProcFormalParameter(ProcFormalParameter ast, Void unused) {
        ast.I.visit(this);
        ast.FPS.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVarFormalParameter(VarFormalParameter ast, Void unused) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIdentifier(Identifier ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCharacterLiteral(CharacterLiteral ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIntegerLiteral(IntegerLiteral ast, Void unused) {
        return ast;
    }

    @Override
    public AbstractSyntaxTree visitOperator(Operator ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitProgram(Program ast, Void unused) {
        ast.C.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleRecordAggregate(MultipleRecordAggregate ast, Void unused) {
        ast.E.visit(this);
        ast.I.visit(this);
        ast.RA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleRecordAggregate(SingleRecordAggregate ast, Void unused) {
        ast.E.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitAnyTypeDenoter(AnyTypeDenoter ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitArrayTypeDenoter(ArrayTypeDenoter ast, Void unused) {
        ast.IL.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitBoolTypeDenoter(BoolTypeDenoter ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCharTypeDenoter(CharTypeDenoter ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitErrorTypeDenoter(ErrorTypeDenoter ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSimpleTypeDenoter(SimpleTypeDenoter ast, Void unused) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIntTypeDenoter(IntTypeDenoter ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitRecordTypeDenoter(RecordTypeDenoter ast, Void unused) {
        ast.FT.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitDotVname(DotVname ast, Void unused) {
        ast.I.visit(this);
        ast.V.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSimpleVname(SimpleVname ast, Void unused) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSubscriptVname(SubscriptVname ast, Void unused) {
        ast.E.visit(this);
        ast.V.visit(this);
        return null;
    }
}