package triangle.optimiser;

import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.optimiser.ConstantFolder;
import triangle.abstractSyntaxTrees.commands.IfCommand;
import triangle.abstractSyntaxTrees.commands.WhileCommand;
import triangle.abstractSyntaxTrees.expressions.BinaryExpression;


public class Counter extends ConstantFolder {
	private int ifCount = 0;
	private int whileCount = 0;
	private int binaryCount = 0;

	@Override
	public AbstractSyntaxTree visitIfCommand(IfCommand ast, Void arg) {
		ifCount++;
		ast.C1.visit(this);
		ast.C2.visit(this);
		ast.E.visit(this);
		return null;
	}
	@Override
	public AbstractSyntaxTree visitWhileCommand(WhileCommand ast, Void arg) {
		whileCount++;
		ast.C.visit(this);
		ast.E.visit(this);
		return null;
	}
	@Override
	public AbstractSyntaxTree visitBinaryExpression(BinaryExpression ast, Void arg) {
		binaryCount++;
		AbstractSyntaxTree replacement1 = ast.E1.visit(this);
		AbstractSyntaxTree replacement2 = ast.E2.visit(this);
		ast.O.visit(this);
		return null;
	}

	public String toString() {
		return "Stats:"
				+ "\n If Commands: " + ifCount
				+ "\n While Commands: " + whileCount
				+ "\n Binary Expressions: " + binaryCount;
	}
}
