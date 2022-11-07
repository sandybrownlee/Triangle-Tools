package triangle.optimiser;

import java.util.ArrayList;
import java.util.List;

import triangle.StdEnvironment;
import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.abstractSyntaxTrees.Program;
import triangle.abstractSyntaxTrees.actuals.ConstActualParameter;
import triangle.abstractSyntaxTrees.actuals.EmptyActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.FuncActualParameter;
import triangle.abstractSyntaxTrees.actuals.MultipleActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.ProcActualParameter;
import triangle.abstractSyntaxTrees.actuals.SingleActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.VarActualParameter;
import triangle.abstractSyntaxTrees.aggregates.MultipleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.MultipleRecordAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleRecordAggregate;
import triangle.abstractSyntaxTrees.commands.AssignCommand;
import triangle.abstractSyntaxTrees.commands.CallCommand;
import triangle.abstractSyntaxTrees.commands.Command;
import triangle.abstractSyntaxTrees.commands.EmptyCommand;
import triangle.abstractSyntaxTrees.commands.IfCommand;
import triangle.abstractSyntaxTrees.commands.LetCommand;
import triangle.abstractSyntaxTrees.commands.LoopWhileCommand;
import triangle.abstractSyntaxTrees.commands.RepeatCommand;
import triangle.abstractSyntaxTrees.commands.SequentialCommand;
import triangle.abstractSyntaxTrees.commands.WhileCommand;
import triangle.abstractSyntaxTrees.declarations.BinaryOperatorDeclaration;
import triangle.abstractSyntaxTrees.declarations.ConstDeclaration;
import triangle.abstractSyntaxTrees.declarations.Declaration;
import triangle.abstractSyntaxTrees.declarations.FuncDeclaration;
import triangle.abstractSyntaxTrees.declarations.ProcDeclaration;
import triangle.abstractSyntaxTrees.declarations.SequentialDeclaration;
import triangle.abstractSyntaxTrees.declarations.UnaryOperatorDeclaration;
import triangle.abstractSyntaxTrees.declarations.VarDeclaration;
import triangle.abstractSyntaxTrees.expressions.ArrayExpression;
import triangle.abstractSyntaxTrees.expressions.BinaryExpression;
import triangle.abstractSyntaxTrees.expressions.CallExpression;
import triangle.abstractSyntaxTrees.expressions.CharacterExpression;
import triangle.abstractSyntaxTrees.expressions.EmptyExpression;
import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.expressions.IfExpression;
import triangle.abstractSyntaxTrees.expressions.IntegerExpression;
import triangle.abstractSyntaxTrees.expressions.LetExpression;
import triangle.abstractSyntaxTrees.expressions.RecordExpression;
import triangle.abstractSyntaxTrees.expressions.UnaryExpression;
import triangle.abstractSyntaxTrees.expressions.VnameExpression;
import triangle.abstractSyntaxTrees.formals.ConstFormalParameter;
import triangle.abstractSyntaxTrees.formals.EmptyFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.FuncFormalParameter;
import triangle.abstractSyntaxTrees.formals.MultipleFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.ProcFormalParameter;
import triangle.abstractSyntaxTrees.formals.SingleFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.VarFormalParameter;
import triangle.abstractSyntaxTrees.terminals.CharacterLiteral;
import triangle.abstractSyntaxTrees.terminals.Identifier;
import triangle.abstractSyntaxTrees.terminals.IntegerLiteral;
import triangle.abstractSyntaxTrees.terminals.Operator;
import triangle.abstractSyntaxTrees.types.AnyTypeDenoter;
import triangle.abstractSyntaxTrees.types.ArrayTypeDenoter;
import triangle.abstractSyntaxTrees.types.BoolTypeDenoter;
import triangle.abstractSyntaxTrees.types.CharTypeDenoter;
import triangle.abstractSyntaxTrees.types.ErrorTypeDenoter;
import triangle.abstractSyntaxTrees.types.IntTypeDenoter;
import triangle.abstractSyntaxTrees.types.MultipleFieldTypeDenoter;
import triangle.abstractSyntaxTrees.types.RecordTypeDenoter;
import triangle.abstractSyntaxTrees.types.SimpleTypeDenoter;
import triangle.abstractSyntaxTrees.types.SingleFieldTypeDenoter;
import triangle.abstractSyntaxTrees.types.TypeDeclaration;
import triangle.abstractSyntaxTrees.visitors.ActualParameterSequenceVisitor;
import triangle.abstractSyntaxTrees.visitors.ActualParameterVisitor;
import triangle.abstractSyntaxTrees.visitors.ArrayAggregateVisitor;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.abstractSyntaxTrees.visitors.DeclarationVisitor;
import triangle.abstractSyntaxTrees.visitors.ExpressionVisitor;
import triangle.abstractSyntaxTrees.visitors.FormalParameterSequenceVisitor;
import triangle.abstractSyntaxTrees.visitors.IdentifierVisitor;
import triangle.abstractSyntaxTrees.visitors.LiteralVisitor;
import triangle.abstractSyntaxTrees.visitors.OperatorVisitor;
import triangle.abstractSyntaxTrees.visitors.ProgramVisitor;
import triangle.abstractSyntaxTrees.visitors.RecordAggregateVisitor;
import triangle.abstractSyntaxTrees.visitors.TypeDenoterVisitor;
import triangle.abstractSyntaxTrees.visitors.VnameVisitor;
import triangle.abstractSyntaxTrees.vnames.DotVname;
import triangle.abstractSyntaxTrees.vnames.SimpleVname;
import triangle.abstractSyntaxTrees.vnames.SubscriptVname;
import triangle.abstractSyntaxTrees.vnames.Vname;

public class WhileHoister implements ActualParameterVisitor<Void, AbstractSyntaxTree>,
		ActualParameterSequenceVisitor<Void, AbstractSyntaxTree>, ArrayAggregateVisitor<Void, AbstractSyntaxTree>,
		CommandVisitor<Void, AbstractSyntaxTree>, DeclarationVisitor<Void, AbstractSyntaxTree>,
		ExpressionVisitor<Void, AbstractSyntaxTree>, FormalParameterSequenceVisitor<Void, AbstractSyntaxTree>,
		IdentifierVisitor<Void, AbstractSyntaxTree>, LiteralVisitor<Void, AbstractSyntaxTree>,
		OperatorVisitor<Void, AbstractSyntaxTree>, ProgramVisitor<Void, AbstractSyntaxTree>,
		RecordAggregateVisitor<Void, AbstractSyntaxTree>, TypeDenoterVisitor<Void, AbstractSyntaxTree>,
		VnameVisitor<Void, AbstractSyntaxTree> {

	private ArrayList<String> assigned;
	private ArrayList<Expression> constant;
	private ArrayList<Identifier> hoistVariables;
	private boolean assignComplete = false;
	
	public WhileHoister() {
		assigned = new ArrayList<String>();
		constant = new ArrayList<Expression>();
		hoistVariables = new ArrayList<Identifier>();
		assignComplete = false;
	}
	
	public ArrayList<String> getAssigned() {
		return assigned;
	}
	
	public ArrayList<Expression> getConstant() {
		return constant;
	}
	
	public ArrayList<Identifier> getHoistVariables() {
		return hoistVariables;
	}
	
	public void completeAssignment() {
		assignComplete = true;
	}
	
	@Override
	public AbstractSyntaxTree visitConstFormalParameter(ConstFormalParameter ast, Void arg) {
		ast.I.visit(this);
		ast.T.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitFuncFormalParameter(FuncFormalParameter ast, Void arg) {
		ast.I.visit(this);
		ast.T.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitProcFormalParameter(ProcFormalParameter ast, Void arg) {
		ast.I.visit(this);
		ast.FPS.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitVarFormalParameter(VarFormalParameter ast, Void arg) {
		ast.I.visit(this);
		ast.T.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Void arg) {
		ast.FT.visit(this);
		ast.I.visit(this);
		ast.T.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Void arg) {
		ast.I.visit(this);
		ast.T.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitDotVname(DotVname ast, Void arg) {
		ast.I.visit(this);
		ast.V.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSimpleVname(SimpleVname ast, Void arg) {
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSubscriptVname(SubscriptVname ast, Void arg) {
		ast.E.visit(this);
		ast.V.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitAnyTypeDenoter(AnyTypeDenoter ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitArrayTypeDenoter(ArrayTypeDenoter ast, Void arg) {
		ast.IL.visit(this);
		ast.T.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitBoolTypeDenoter(BoolTypeDenoter ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitCharTypeDenoter(CharTypeDenoter ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitErrorTypeDenoter(ErrorTypeDenoter ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSimpleTypeDenoter(SimpleTypeDenoter ast, Void arg) {
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitIntTypeDenoter(IntTypeDenoter ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitRecordTypeDenoter(RecordTypeDenoter ast, Void arg) {
		ast.FT.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitMultipleRecordAggregate(MultipleRecordAggregate ast, Void arg) {
		ast.E.visit(this);
		ast.I.visit(this);
		ast.RA.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSingleRecordAggregate(SingleRecordAggregate ast, Void arg) {
		ast.E.visit(this);
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitProgram(Program ast, Void arg) {
		ast.C.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitOperator(Operator ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitCharacterLiteral(CharacterLiteral ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitIntegerLiteral(IntegerLiteral ast, Void arg) {
		return ast;
	}

	@Override
	public AbstractSyntaxTree visitIdentifier(Identifier ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Void arg) {
		ast.FP.visit(this);
		ast.FPS.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Void arg) {
		ast.FP.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitArrayExpression(ArrayExpression ast, Void arg) {
		ast.AA.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitBinaryExpression(BinaryExpression ast, Void arg) {
		ast.E1.visit(this);
		ast.E2.visit(this);
		ast.O.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitCallExpression(CallExpression ast, Void arg) {
		ast.APS.visit(this);
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitCharacterExpression(CharacterExpression ast, Void arg) {
		ast.CL.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitEmptyExpression(EmptyExpression ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitIfExpression(IfExpression ast, Void arg) {
		
		ast.E1.visit(this);
		ast.E2.visit(this);
		ast.E3.visit(this);

		return null;
	}

	@Override
	public AbstractSyntaxTree visitIntegerExpression(IntegerExpression ast, Void arg) {
		return ast;
	}

	@Override
	public AbstractSyntaxTree visitLetExpression(LetExpression ast, Void arg) {
		ast.D.visit(this);
		ast.E.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitRecordExpression(RecordExpression ast, Void arg) {
		ast.RA.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitUnaryExpression(UnaryExpression ast, Void arg) {
		ast.E.visit(this);
		ast.O.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitVnameExpression(VnameExpression ast, Void arg) {
		ast.V.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Void arg) {
		ast.ARG1.visit(this);
		ast.ARG2.visit(this);
		ast.O.visit(this);
		ast.RES.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitConstDeclaration(ConstDeclaration ast, Void arg) {
		ast.E.visit(this);
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitFuncDeclaration(FuncDeclaration ast, Void arg) {
		ast.E.visit(this);
		ast.FPS.visit(this);
		ast.I.visit(this);
		ast.T.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitProcDeclaration(ProcDeclaration ast, Void arg) {
		ast.C.visit(this);
		ast.FPS.visit(this);
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSequentialDeclaration(SequentialDeclaration ast, Void arg) {
		ast.D1.visit(this);
		ast.D2.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitTypeDeclaration(TypeDeclaration ast, Void arg) {
		ast.I.visit(this);
		ast.T.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Void arg) {
		ast.ARG.visit(this);
		ast.O.visit(this);
		ast.RES.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitVarDeclaration(VarDeclaration ast, Void arg) {
		ast.I.visit(this);
		ast.T.visit(this);
		return null;
	}
	
	@Override
	public AbstractSyntaxTree visitAssignCommand(AssignCommand ast, Void arg) {
		if (assignComplete) {
			HoistExpression hoister = new HoistExpression(assigned);
			ast.E.visit(hoister);
			if (hoister.isHoistable()) {
				constant.add(ast.E);
				int index = constant.indexOf(ast.E);
				Identifier i = new Identifier("hoist"+Integer.toString(index),ast.getPosition());
				i.decl = ((SimpleVname) ast.V).I.decl;
				hoistVariables.add(i);
				SimpleVname vn = new SimpleVname(i, ast.getPosition());
				VnameExpression ve = new VnameExpression(vn, ast.getPosition());
				ve.type = ast.E.type;
				ast.E = ve;
			}
		}
		ast.E.visit(this);
		SimpleVname temp = ((SimpleVname) ast.V);
		assigned.add(temp.I.spelling);
		ast.V.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitCallCommand(CallCommand ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitEmptyCommand(EmptyCommand ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitIfCommand(IfCommand ast, Void arg) {
		ast.C1.visit(this);
		ast.C2.visit(this);
		ast.E.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitLetCommand(LetCommand ast, Void arg) {
		ast.C.visit(this);
		ast.D.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSequentialCommand(SequentialCommand ast, Void arg) {
		ast.C1.visit(this);
		ast.C2.visit(this);
		return null;
	}
	
	@Override
	public AbstractSyntaxTree visitLoopWhileCommand(LoopWhileCommand ast, Void arg) {
		ast.C1.visit(this);
		ast.C2.visit(this);
		ast.E.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitWhileCommand(WhileCommand ast, Void arg) {
		ast.C.visit(this);
		ast.E.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitRepeatCommand(RepeatCommand ast, Void arg) {
		ast.C.visit(this);
		ast.E.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitMultipleArrayAggregate(MultipleArrayAggregate ast, Void arg) {
		ast.AA.visit(this);
		ast.E.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSingleArrayAggregate(SingleArrayAggregate ast, Void arg) {
		ast.E.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Void arg) {
		return null;
	}

	@Override
	public AbstractSyntaxTree visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Void arg) {
		ast.AP.visit(this);
		ast.APS.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSingleActualParameterSequence(SingleActualParameterSequence ast, Void arg) {
		ast.AP.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitConstActualParameter(ConstActualParameter ast, Void arg) {
		ast.E.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitFuncActualParameter(FuncActualParameter ast, Void arg) {
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitProcActualParameter(ProcActualParameter ast, Void arg) {
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitVarActualParameter(VarActualParameter ast, Void arg) {
		ast.V.visit(this);
		return null;
	}

}
