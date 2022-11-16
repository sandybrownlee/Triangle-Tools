package triangle.treeDrawer;

import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.abstractSyntaxTrees.commands.AssignCommand;
import triangle.abstractSyntaxTrees.commands.CallCommand;
import triangle.abstractSyntaxTrees.commands.EmptyCommand;
import triangle.abstractSyntaxTrees.commands.IfCommand;
import triangle.abstractSyntaxTrees.commands.LetCommand;
import triangle.abstractSyntaxTrees.commands.LoopWhileCommand;
import triangle.abstractSyntaxTrees.commands.RepeatCommand;
import triangle.abstractSyntaxTrees.commands.SequentialCommand;
import triangle.abstractSyntaxTrees.commands.WhileCommand;
import triangle.abstractSyntaxTrees.expressions.ArrayExpression;
import triangle.abstractSyntaxTrees.expressions.BinaryExpression;
import triangle.abstractSyntaxTrees.expressions.CallExpression;
import triangle.abstractSyntaxTrees.expressions.CharacterExpression;
import triangle.abstractSyntaxTrees.expressions.EmptyExpression;
import triangle.abstractSyntaxTrees.expressions.IfExpression;
import triangle.abstractSyntaxTrees.expressions.IntegerExpression;
import triangle.abstractSyntaxTrees.expressions.LetExpression;
import triangle.abstractSyntaxTrees.expressions.RecordExpression;
import triangle.abstractSyntaxTrees.expressions.UnaryExpression;
import triangle.abstractSyntaxTrees.expressions.VnameExpression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.abstractSyntaxTrees.visitors.ExpressionVisitor;

public class Stats implements CommandVisitor<Void, AbstractSyntaxTree>, ExpressionVisitor<Void, AbstractSyntaxTree>{

	private int binaryExpressions, ifCommands, whileCommands;

	public int countBinaryExpressions(BinaryExpression ast, Void arg) {
		return binaryExpressions;
	}
	public int getIfCommands(IfCommand ast, Void arg) {
		return ifCommands;
	}
	public int getWhileCommands(WhileCommand ast, Void arg) {
		return whileCommands;
	}
	@Override
	public AbstractSyntaxTree visitBinaryExpression(BinaryExpression ast, Void arg) {
		binaryExpressions++;
		return null;
	}
	@Override
	public AbstractSyntaxTree visitIfCommand(IfCommand ast, Void arg) {
		ifCommands++;
		return null;
	}
	@Override
	public AbstractSyntaxTree visitWhileCommand(WhileCommand ast, Void arg) {
		whileCommands++;
		return null;
	}
	
	public String toString() {
		return "If Commands: " + ifCommands + "/n" +
			   "While Commands: " + whileCommands + "/n" +
			   "Binary Expressions: " + binaryExpressions;
	}
	@Override
	public AbstractSyntaxTree visitArrayExpression(ArrayExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitCallExpression(CallExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitCharacterExpression(CharacterExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitEmptyExpression(EmptyExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitIfExpression(IfExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitIntegerExpression(IntegerExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitLetExpression(LetExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitRecordExpression(RecordExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitUnaryExpression(UnaryExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitVnameExpression(VnameExpression ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitAssignCommand(AssignCommand ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitCallCommand(CallCommand ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitEmptyCommand(EmptyCommand ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitLetCommand(LetCommand ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitLoopWhileCommand(LoopWhileCommand ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitSequentialCommand(SequentialCommand ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbstractSyntaxTree visitRepeatCommand(RepeatCommand ast, Void arg) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
