package triangle.optimiser;

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
import triangle.abstractSyntaxTrees.vnames.Vname;

import java.util.ArrayList;
import java.util.HashMap;

public class Hoister implements ActualParameterVisitor<Void, AbstractSyntaxTree>,
        ActualParameterSequenceVisitor<Void, AbstractSyntaxTree>, ArrayAggregateVisitor<Void, AbstractSyntaxTree>,
        CommandVisitor<Void, AbstractSyntaxTree>, DeclarationVisitor<Void, AbstractSyntaxTree>,
        ExpressionVisitor<Void, AbstractSyntaxTree>, FormalParameterSequenceVisitor<Void, AbstractSyntaxTree>,
        IdentifierVisitor<Void, AbstractSyntaxTree>, LiteralVisitor<Void, AbstractSyntaxTree>,
        OperatorVisitor<Void, AbstractSyntaxTree>, ProgramVisitor<Void, AbstractSyntaxTree>,
        RecordAggregateVisitor<Void, AbstractSyntaxTree>, TypeDenoterVisitor<Void, AbstractSyntaxTree>,
        VnameVisitor<Void, AbstractSyntaxTree> {

    private ArrayList<Vname> updated = new ArrayList<>();
    private HashMap<AssignCommand, AssignCommand> hoisted = new HashMap<>();
    private int pass = 0;
    private int tmpVarCount = 0;

    /**
     * Determine if a specific command is a while command
     *
     * @param command
     * @return
     */
    public boolean isCommandWhile(Command command) {
        return command instanceof WhileCommand;
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
        // Expressions only return something if they are invariant
        // might be unnecessary, test later
        if (ast.E.visit(this) != null) {
            return ast;
        }
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleArrayAggregate(SingleArrayAggregate ast, Void unused) {
        // Expressions only return something if they are invariant
        // might be unnecessary, test later
        if (ast.E.visit(this) != null) {
            return ast;
        }
        return null;
    }

    @Override
    public AbstractSyntaxTree visitAssignCommand(AssignCommand ast, Void unused) {
        if (pass == 0)
            updated.add(ast.V);
        if (pass == 1 && ast.E.visit(this) != null) {
            // This code is part of the failed implementation for Hoisting. It is commented out to allow the project
            // to build, as it contains errors
            /*Identifier tmpIdentifier = new Identifier("reservedTmpHoistVar" + tmpVarCount);
            SimpleVname tmpVariable = new SimpleVname(tmpIdentifier);
            AssignCommand tmpAssign = new AssignCommand(tmpVariable, ast.E);
            hoisted.put(ast, tmpAssign);*/
        }
        // This code is part of the failed implementation for Hoisting. It is commented out to allow the project to
        // build, as it contains errors
        //if (pass == 3 && hoisted.containsKey(ast)) {
            // Replace the expression with the temporary variable that has been assigned outside the loop
            //ast.E = new VnameExpression(hoisted.get(ast).V);
        //}
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
        updated.clear();
        hoisted.clear();
        pass = 0;
        SequentialCommand new_ast;
        WhileCommand new_while = ast;
        ast.C.visit(this);
        ast.E.visit(this);
        pass = 1;
        ast.C.visit(this);
        ast.E.visit(this);
        // only
        if (!hoisted.isEmpty()) {
            pass = 2;
            ast.C.visit(this);
            ast.E.visit(this);
            // This code is part of the failed implementation for Hoisting. It is commented out to allow the project to
            // build, as it contains errors
            //for (AssignCommand curAssignCom : hoisted.keySet()) {
            //
            //}
            //return new_ast;
        }
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
        //ast.AA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitBinaryExpression(BinaryExpression ast, Void unused) {
        if (pass == 1) {
            AbstractSyntaxTree expression1 = ast.E1.visit(this);
            AbstractSyntaxTree expression2 = ast.E2.visit(this);
            // Expressions only return something if they are invariant; if this is true for both expressions, we return
            // the ast to signify this whole BinaryExpression is invariant
            if (expression1 != null && expression2 != null) {
                return ast;
            }
            //ast.O.visit(this);
        }
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCallExpression(CallExpression ast, Void unused) {
        //ast.APS.visit(this);
        //ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCharacterExpression(CharacterExpression ast, Void unused) {
        return ast;
    }

    @Override
    public AbstractSyntaxTree visitEmptyExpression(EmptyExpression ast, Void unused) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIfExpression(IfExpression ast, Void unused) {
        // Expressions only return something if they are invariant; if this is the case for all these expressions, then
        // the if expression as a whole is invariant
        if (
                pass == 1 &&
                ast.E1.visit(this) != null
                && ast.E2.visit(this) != null
                && ast.E3.visit(this) != null
        ) {
            return ast;
        }
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIntegerExpression(IntegerExpression ast, Void unused) {
        return ast;
    }

    @Override
    public AbstractSyntaxTree visitLetExpression(LetExpression ast, Void unused) {
        /*ast.D.visit(this);
        // Expressions only return something if they are invariant
        if (ast.E.visit(this) != null) {
            return ast;
        }*/
        return null;
    }

    @Override
    public AbstractSyntaxTree visitRecordExpression(RecordExpression ast, Void unused) {
        //ast.RA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitUnaryExpression(UnaryExpression ast, Void unused) {
        // Expressions only return something if they are invariant
        if (ast.E.visit(this) != null) {
            return ast;
        }
        //ast.O.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVnameExpression(VnameExpression ast, Void unused) {
        if (pass == 1 && !updated.contains(ast.V))
            return ast;
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
        ast.C.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleRecordAggregate(MultipleRecordAggregate ast, Void unused) {
        // Expressions only return something if they are invariant
        if (ast.E.visit(this) != null) {
            return ast;
        }
        ast.I.visit(this);
        ast.RA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleRecordAggregate(SingleRecordAggregate ast, Void unused) {
        // Expressions only return something if they are invariant
        if (ast.E.visit(this) != null) {
            return ast;
        }
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
        // Expressions only return something if they are invariant
        if (ast.E.visit(this) != null) {
            return ast;
        }
        ast.V.visit(this);
        return null;
    }
}
