package triangle.optimiser;

import com.sun.jdi.VoidType;
import triangle.StdEnvironment;
import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.abstractSyntaxTrees.Program;
import triangle.abstractSyntaxTrees.actuals.*;
import triangle.abstractSyntaxTrees.aggregates.MultipleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.MultipleRecordAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleRecordAggregate;
import triangle.abstractSyntaxTrees.commands.*;
import triangle.abstractSyntaxTrees.declarations.*;
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
import triangle.abstractSyntaxTrees.types.*;
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

import java.util.ArrayList;

public class ConstantFolder implements ActualParameterVisitor<Void, AbstractSyntaxTree>,
		ActualParameterSequenceVisitor<Void, AbstractSyntaxTree>, ArrayAggregateVisitor<Void, AbstractSyntaxTree>,
		CommandVisitor<Void, AbstractSyntaxTree>, DeclarationVisitor<Void, AbstractSyntaxTree>,
		ExpressionVisitor<Void, AbstractSyntaxTree>, FormalParameterSequenceVisitor<Void, AbstractSyntaxTree>,
		IdentifierVisitor<Void, AbstractSyntaxTree>, LiteralVisitor<Void, AbstractSyntaxTree>,
		OperatorVisitor<Void, AbstractSyntaxTree>, ProgramVisitor<Void, AbstractSyntaxTree>,
		RecordAggregateVisitor<Void, AbstractSyntaxTree>, TypeDenoterVisitor<Void, AbstractSyntaxTree>,
		VnameVisitor<Void, AbstractSyntaxTree> {
	{

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
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
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
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
		ast.I.visit(this);
		ast.RA.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSingleRecordAggregate(SingleRecordAggregate ast, Void arg) {
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
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
		AbstractSyntaxTree replacement1 = ast.E1.visit(this);
		AbstractSyntaxTree replacement2 = ast.E2.visit(this);
		ast.O.visit(this);

		if (replacement1 != null && replacement2 != null) {
			return foldBinaryExpression(replacement1, replacement2, ast.O);
		} else if (replacement1 != null) {
			ast.E1 = (Expression) replacement1;
		} else if (replacement2 != null) {
			ast.E2 = (Expression) replacement2;
		}

		// if we get here, we can't fold any higher than this level
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
		AbstractSyntaxTree replacement1 = ast.E1.visit(this);
		if (replacement1 != null) {
			ast.E1 = (Expression) replacement1;
		}
		AbstractSyntaxTree replacement2 = ast.E2.visit(this);
		if (replacement2 != null) {
			ast.E2 = (Expression) replacement2;
		}
		AbstractSyntaxTree replacement3 = ast.E3.visit(this);
		if (replacement3 != null) {
			ast.E3 = (Expression) replacement3;
		}

		return null;
	}

	@Override
	public AbstractSyntaxTree visitIntegerExpression(IntegerExpression ast, Void arg) {
		return ast;
	}

	@Override
	public AbstractSyntaxTree visitLetExpression(LetExpression ast, Void arg) {
		ast.D.visit(this);
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
		return null;
	}

	@Override
	public AbstractSyntaxTree visitRecordExpression(RecordExpression ast, Void arg) {
		ast.RA.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitUnaryExpression(UnaryExpression ast, Void arg) {
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}

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
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
		ast.I.visit(this);
		return null;
	}

	@Override
	public AbstractSyntaxTree visitFuncDeclaration(FuncDeclaration ast, Void arg) {
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
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
		AbstractSyntaxTree replacement = ast.E.visit(this);

		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
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
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
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
		if (((ast).C1) instanceof WhileCommand) {
			AbstractSyntaxTree replacer = hoistWhile(ast);
			if (replacer != null) (ast).C1 = (Command) replacer;
		}
		else if (((ast).C2) instanceof WhileCommand) {
			AbstractSyntaxTree replacer = hoistWhile(ast);
			if (replacer != null) (ast).C2 = (Command) replacer;
		}
		else {
			ast.C1.visit(this);
			ast.C2.visit(this);
		}

		return null;
	}

	private SequentialCommand hoistWhile(AbstractSyntaxTree ast) {
		// Determine relevant branch
		AbstractSyntaxTree whileAST = ((SequentialCommand) ast).C2;
		AbstractSyntaxTree otherAST = ((SequentialCommand) ast).C1;
		if (otherAST instanceof WhileCommand) {
			otherAST = ((SequentialCommand) ast).C2;
			whileAST = ((SequentialCommand) ast).C1;
		}
		// Receive while loop's expression
		ArrayList<WhileEntry> entries = new ArrayList<>();
		ArrayList<AbstractSyntaxTree> hoistASTs = new ArrayList<>();	// Template for recursion
		WhileEntry expressionEntry = null;

		// Receive variable of the defining expression for the loop
		if (((WhileCommand) whileAST).E instanceof BinaryExpression && ((WhileCommand) whileAST).C instanceof SequentialCommand) {
			String exprName = "";
			String exprValue = "";
			if ((((BinaryExpression) ((WhileCommand) whileAST).E).E1) instanceof VnameExpression) {
				AbstractSyntaxTree abTree = (((((VnameExpression) (((BinaryExpression) ((WhileCommand) whileAST).E).E1)).V)));
				exprName = ((SimpleVname) abTree).I.spelling;
			}
			if ((((BinaryExpression) ((WhileCommand) whileAST).E).E2) instanceof VnameExpression) {
				AbstractSyntaxTree abTree = (((((VnameExpression) (((BinaryExpression) ((WhileCommand) whileAST).E).E2)).V)));
				exprName = ((SimpleVname) abTree).I.spelling;
			}
			expressionEntry = new WhileEntry(exprName, exprValue, ""); // Cannot have second value

			// Search hoistable variables
			preHoistSeqCommand(((WhileCommand) whileAST).C, entries, expressionEntry.vName, hoistASTs);

			if (hoistASTs.size() != 0) { // Means hoisting: yes
				return (SequentialCommand) assembleHoistedAST(hoistASTs, expressionEntry, whileAST, otherAST, ((WhileCommand) whileAST).C, entries);
			}
		}
		return null;
	}

	private AbstractSyntaxTree assembleHoistedAST(ArrayList<AbstractSyntaxTree> hoistASTs, WhileEntry expressionEntry, AbstractSyntaxTree whileAST, AbstractSyntaxTree otherAST, AbstractSyntaxTree wh2AST, ArrayList<WhileEntry> entries) {

		// Fetched values from actual hoist statement
		String tmp1Val = "";
		String tmp2Val = "";
		AbstractSyntaxTree hoistAST = hoistASTs.get(0);
		SimpleVname simVn = (SimpleVname) ((AssignCommand) hoistAST).V; // simVname ast
		Operator oAST = new Operator("+", hoistAST.getPosition());
		oAST.decl = StdEnvironment.addDecl;

		if ((((AssignCommand) hoistAST).E) instanceof VnameExpression) {
			VnameExpression vne = (VnameExpression) ( ((AssignCommand) hoistAST).E);
			simVn = (SimpleVname) (vne).V;
		}

		// Fetch variables' relevant information
		if (entries.size() != 0) {
			for(int i = 0; i < entries.size(); i++) {
				if ((!entries.get(i).v1Value.equals(expressionEntry.vName) && (!entries.get(i).v2Value.equals(expressionEntry.vName))
						&& ((!(entries.get(i).v1Value.equals(entries.get(i).vName))
						&& (entries.get(i).v2Value != null) && (entries.get(i).v2Value != ""))))
						&& (!(entries.get(i).v1Value.equals(entries.get(i).vName)) && (entries.get(i).v1Value != null) && (entries.get(i).v1Value != ""))) {
					tmp1Val = entries.get(i).v1Value;
					tmp2Val = entries.get(i).v2Value;
				}
			}
		}
		// Check for previous declaration of hoisted invariant
		ArrayList<AbstractSyntaxTree> hoistsFound = new ArrayList<>();
		if (otherAST instanceof SequentialCommand) {
			hoistsFound = findHoistValue(otherAST, tmp1Val, new ArrayList<>());
			if (hoistsFound != null) {
			}
		}

		// Compose the replacement

		// Declaration:		-- always the same, no matter the hoist process --
		Identifier idf = new Identifier("tmp", wh2AST.getPosition());
		idf.type = StdEnvironment.charType;
		idf.decl = StdEnvironment.charDecl;
		IntegerLiteral il = new IntegerLiteral("Integer", wh2AST.getPosition());
		idf.type = StdEnvironment.integerType;
		idf.decl = StdEnvironment.integerDecl;
		IntTypeDenoter inTy = new IntTypeDenoter(wh2AST.getPosition());
		idf.type = StdEnvironment.integerType;
		idf.decl = StdEnvironment.integerDecl;
		IntegerExpression in2f = new IntegerExpression(il, wh2AST.getPosition());
		in2f.type = StdEnvironment.integerType;
		AbstractSyntaxTree dAST = new VarDeclaration(idf, inTy, wh2AST.getPosition());

		AbstractSyntaxTree exprAST = null;
		if (hoistsFound.size() != 0) {
			// From the top seq command's other subtree (second check: hoistsFound)
			AbstractSyntaxTree hoistV = hoistsFound.get(0);
			SimpleVname subHoist = (SimpleVname) ((AssignCommand) hoistV).V;
			VnameExpression vnE = new VnameExpression(subHoist, otherAST.getPosition());

			// Expression kind defines what invariant is assigned to
			Boolean isInt = true;
			IntegerLiteral inL = null;
			AbstractSyntaxTree expr = null;
			try {
				Integer.parseInt(tmp2Val);
				isInt =  true;
			} catch(NumberFormatException e){
				isInt = false;
			}
			if (isInt) {
				inL = new IntegerLiteral(tmp2Val, otherAST.getPosition());
				expr = new IntegerExpression(inL, otherAST.getPosition());
			} else {
				expr = vnE;
			}
			exprAST = new BinaryExpression(vnE, oAST, (Expression) expr, hoistAST.getPosition());
			((BinaryExpression)exprAST).E1.type = StdEnvironment.charType;
			((BinaryExpression)exprAST).E2.type = StdEnvironment.charType;
		} else {
			exprAST = new EmptyExpression(hoistAST.getPosition());

			// Value to assign invariant to
			if (((AssignCommand) (hoistAST)).E instanceof  BinaryExpression) {
				if ((((BinaryExpression) ((AssignCommand) (hoistAST)).E).E1 instanceof IntegerExpression)
				 && (((BinaryExpression) ((AssignCommand) (hoistAST)).E).E2 instanceof IntegerExpression)) {
					IntegerExpression in1E = (IntegerExpression) ((BinaryExpression) ((AssignCommand) (hoistAST)).E).E1;
					IntegerExpression in2E = (IntegerExpression) ((BinaryExpression) ((AssignCommand) (hoistAST)).E).E2;
					int result = Integer.parseInt(in1E.IL.spelling) + Integer.parseInt(in2E.IL.spelling);
					exprAST = in1E;
					in1E.IL.spelling = String.valueOf(result);

					VnameExpression vnE = new VnameExpression(((AssignCommand) hoistAST).V, otherAST.getPosition());
					vnE.type = StdEnvironment.charType;
					vnE.V.type = StdEnvironment.charType;
					((AssignCommand) (hoistAST)).E = vnE;
					((SimpleVname) ((AssignCommand) hoistAST).V).I.spelling = "tmp";
				}
			}
		}
		// Final assembly to return
		AssignCommand tmpAssign = new AssignCommand(simVn, (Expression) exprAST, otherAST.getPosition());
		tmpAssign.V.type = StdEnvironment.charType;
		tmpAssign.E.type = StdEnvironment.charType;
		AbstractSyntaxTree letAST = new LetCommand((Declaration) dAST, tmpAssign, otherAST.getPosition());

		return new SequentialCommand((Command) letAST, (Command) whileAST, wh2AST.getPosition());
	}

	@Override
	public AbstractSyntaxTree visitWhileCommand(WhileCommand ast, Void arg) {
		ast.C.visit(this);
		AbstractSyntaxTree replacement = ast.E.visit(this);
			if (replacement != null) {
				ast.E = (Expression) replacement;
			}
		return null;
	}

	@Override
	public AbstractSyntaxTree visitRepeatCommand(RepeatCommand ast, Void arg) {
		ast.C.visit(this);
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
		return null;
	}

	@Override
	public AbstractSyntaxTree visitLoopMiddleCheckCommand(LoopMiddleCheckCommand ast, Void arg) {
		ast.C1.visit(this);
		ast.C2.visit(this);
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
		return null;
	}

	@Override
	public AbstractSyntaxTree visitMultipleArrayAggregate(MultipleArrayAggregate ast, Void arg) {
		ast.AA.visit(this);
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
		return null;
	}

	@Override
	public AbstractSyntaxTree visitSingleArrayAggregate(SingleArrayAggregate ast, Void arg) {
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
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
		AbstractSyntaxTree replacement = ast.E.visit(this);
		if (replacement != null) {
			ast.E = (Expression) replacement;
		}
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

	public AbstractSyntaxTree foldBinaryExpression(AbstractSyntaxTree node1, AbstractSyntaxTree node2, Operator o) {

		// the only case we know how to deal with for now is two IntegerExpressions
		if ((node1 instanceof IntegerExpression) && (node2 instanceof IntegerExpression)) {

			int int1 = (Integer.parseInt(((IntegerExpression) node1).IL.spelling));
			int int2 = (Integer.parseInt(((IntegerExpression) node2).IL.spelling));
			Object foldedValue = null;
			
			if (o.decl == StdEnvironment.addDecl) {
				foldedValue = int1 + int2;
			} else if (o.decl == StdEnvironment.divideDecl) {
				foldedValue = int1 / int2;
			} else if (o.decl == StdEnvironment.moduloDecl) {
				foldedValue = int1 % int2;
			} else if (o.decl == StdEnvironment.multiplyDecl) {
				foldedValue = int1 * int2;
			} else if (o.decl == StdEnvironment.subtractDecl) {
				foldedValue = int1 - int2;
			} else if (o.decl == StdEnvironment.lessDecl) {
				if (int1 < int2) {
					foldedValue = true;
				} else {
					foldedValue = false;
				}
			} else if (o.decl == StdEnvironment.notgreaterDecl) {
				if (int1 <= int2) {
					foldedValue = true;
				} else {
					foldedValue = false;
				}
			} else if (o.decl == StdEnvironment.greaterDecl) {
				if (int1 > int2) {
					foldedValue = true;
				} else {
					foldedValue = false;
				}
			} else if (o.decl == StdEnvironment.notlessDecl) {
				if (int1 >= int2) {
					foldedValue = true;
				} else {
					foldedValue = false;
				}
			} else if (o.decl == StdEnvironment.equalDecl) {
				if (int1 == int2) {
					foldedValue = true;
				} else {
					foldedValue = false;
				}
			} else if (o.decl == StdEnvironment.unequalDecl) {
				if (int1 != int2) {
					foldedValue = true;
				} else {
					foldedValue = false;
				}
			}

			if (foldedValue instanceof Integer) {
				IntegerLiteral il = new IntegerLiteral(foldedValue.toString(), node1.getPosition());
				IntegerExpression ie = new IntegerExpression(il, node1.getPosition());
				ie.type = StdEnvironment.integerType;
				return ie;
			} else if (foldedValue instanceof Boolean) {

				Identifier idf = new Identifier("true", node1.getPosition());
				if (((Boolean) foldedValue).booleanValue() == false) {
					idf.spelling = "false";
				}

				idf.type = StdEnvironment.booleanType;
				idf.decl = StdEnvironment.trueDecl;
				SimpleVname siVname = new SimpleVname(idf, node1.getPosition());
				VnameExpression vnExpression = new VnameExpression(siVname, node1.getPosition());
				vnExpression.type = StdEnvironment.booleanType;
				return vnExpression;
			}
		}

		// any unhandled situation (i.e., not foldable) is ignored
		return null;
	}

	public ArrayList<AbstractSyntaxTree> preHoistSeqCommand(AbstractSyntaxTree ast, ArrayList<WhileEntry> entries, String exprVname, ArrayList<AbstractSyntaxTree> hoistASTs) {
		AbstractSyntaxTree subtree = (((SequentialCommand) ast).C1);
		String subHoist = null;

		// Recurse to get subtrees
		if ((((SequentialCommand) ast).C1) instanceof SequentialCommand) {
			preHoistSeqCommand((((SequentialCommand) ast).C1), entries, exprVname, hoistASTs);
		}
		if ((((SequentialCommand) ast).C2) instanceof SequentialCommand) {
			preHoistSeqCommand((((SequentialCommand) ast).C2), entries, exprVname, hoistASTs);
		}
		if(((((SequentialCommand) ast).C1) instanceof AssignCommand) || ((((SequentialCommand) ast).C2) instanceof AssignCommand)) {
			if((((SequentialCommand) ast).C2) instanceof AssignCommand) {	subtree = (((SequentialCommand) ast).C2); }

			// Check subbranch for hoisting
			subHoist = preHoistAssignCommand(subtree, entries, exprVname, hoistASTs);
			if (subHoist != null) {
				hoistASTs.add((subtree));

				AbstractSyntaxTree binExpr = (((AssignCommand) subtree).E); // Binary Expression
				if (binExpr instanceof BinaryExpression) {

					// Draw the hoist value out of the loop
					if (((BinaryExpression) binExpr).E1 instanceof VnameExpression || ((BinaryExpression) binExpr).E2 instanceof VnameExpression) {
						AbstractSyntaxTree vnVAST = null;
						if (((BinaryExpression) (((AssignCommand) subtree).E)).E1 instanceof VnameExpression) {
							vnVAST = (((BinaryExpression) (((AssignCommand) subtree).E)).E1); // Vname Expr.
						}
						if (((BinaryExpression) (((AssignCommand) subtree).E)).E2 instanceof VnameExpression) {
							vnVAST = (((BinaryExpression) (((AssignCommand) subtree).E)).E2); // Vname Expr.
						}
						AbstractSyntaxTree simVAST = ((VnameExpression) vnVAST).V; 			  // SimpleVName
						((SimpleVname) simVAST).I.spelling = "tmp";
						(((AssignCommand) subtree).E) = (Expression) vnVAST;
					}
				}
			}
		}
		return hoistASTs;
	}

	public String preHoistAssignCommand(AbstractSyntaxTree ast, ArrayList<WhileEntry> assignEntries, String exprVname, ArrayList<AbstractSyntaxTree> hoistASTs) {
		String foundName = "";
		String v1Value = "";
		String v2Value = "";
		Boolean appearedBefore = false;
		Boolean assignmentHoistable = false;
		AbstractSyntaxTree assignVCom = (((AssignCommand) ast).V);

		// Possible invariant's vname
		if (assignVCom instanceof SimpleVname) {
			foundName = (((SimpleVname) assignVCom).I.spelling);
		}
		// Assignment deciding if invariant
		AbstractSyntaxTree assignECom = (((AssignCommand) ast).E);
		if (assignECom instanceof BinaryExpression) {
			// Subtree 1
			AbstractSyntaxTree b1Tree = ((((BinaryExpression) assignECom).E1));
			if (b1Tree instanceof IntegerExpression) {
				v1Value = (((IntegerExpression) b1Tree).IL.spelling);
			}
			if (b1Tree instanceof VnameExpression) {
				AbstractSyntaxTree vTree = (((VnameExpression) b1Tree).V);
				v1Value = (((SimpleVname) vTree).I.spelling);
			}
			//Subtree 2
			AbstractSyntaxTree b2Tree = ((((BinaryExpression) assignECom).E2));
			if (b2Tree instanceof IntegerExpression) {
				v2Value = (((IntegerExpression) b2Tree).IL.spelling);
			}
			if (b2Tree instanceof VnameExpression) {
				AbstractSyntaxTree vTree = (((VnameExpression) b2Tree).V);
				v2Value = (((SimpleVname) vTree).I.spelling);
			}

			if (assignEntries.size() != 0) { // Safety check - catch from fail
				for (int i = 0; i < assignEntries.size(); i++) { // If appeared before, it is not hoistable
					if ((foundName != "") && ((assignEntries.get(i).vName.equals(v1Value) || (assignEntries.get(i).vName.equals(v2Value))))) {
						appearedBefore = true;
					}
				}
			}
			// THE check
			if ((!v1Value.equals(exprVname) && !v2Value.equals(exprVname))					 // None equals expression's vname
					&& (!(v1Value.equals(foundName) && (v2Value != null) && (v2Value != ""))
					&& !(v2Value.equals(foundName) && (v1Value != null) && (v2Value != ""))) // None equals own vname while other value present
					&& !appearedBefore) { 													 // None appeared before
				assignmentHoistable = true;
			}
		}
		WhileEntry assignEntry = new WhileEntry(foundName,v1Value,v2Value);

		assignEntries.add(assignEntry);
		if(assignmentHoistable) {
			return foundName;
		} else {
			return null;
		}
	}

	public ArrayList<AbstractSyntaxTree> findHoistValue(AbstractSyntaxTree ast, String vName, ArrayList<AbstractSyntaxTree> hoists) {
		AbstractSyntaxTree c1AST = ((SequentialCommand)ast).C1;

		// Recurse  (- means, not yet AssignCommand)
		if (((SequentialCommand)ast).C1 instanceof SequentialCommand) {
			findHoistValue(((SequentialCommand)ast).C1, vName, hoists);
		}
		if (((SequentialCommand)ast).C2 instanceof SequentialCommand) {
			findHoistValue(((SequentialCommand)ast).C2, vName,  hoists);
		}
		// AssignCommand - check for hoisted value
		if (((SequentialCommand)ast).C1 instanceof AssignCommand || ((SequentialCommand)ast).C2 instanceof AssignCommand) {
			if (((SequentialCommand)ast).C2 instanceof AssignCommand) { c1AST = ((SequentialCommand)ast).C2; }
			AbstractSyntaxTree returnAST = findHoistAssign(c1AST,vName);

			if (returnAST != null) {
				hoists.add(returnAST);
				return hoists;
			}
		}
		return hoists;
	}
	public AbstractSyntaxTree findHoistAssign(AbstractSyntaxTree ast, String vName) {
		AbstractSyntaxTree c1AST = ((AssignCommand)ast).V;

		// Get variants name - return this if applicable
		if (c1AST instanceof SimpleVname) {
			if (((SimpleVname) c1AST).I.spelling.equals(vName)) {
				return ast;
			}
		}
		return null;
	}
}
