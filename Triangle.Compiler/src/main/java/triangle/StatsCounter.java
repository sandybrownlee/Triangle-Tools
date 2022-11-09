package triangle;

import triangle.abstractSyntaxTrees.commands.*;
import triangle.abstractSyntaxTrees.expressions.*;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.abstractSyntaxTrees.visitors.ExpressionVisitor;


public class StatsCounter implements CommandVisitor<Void, Void>, ExpressionVisitor<Void, Void> {

    // Declare private variables to hold the number of if, while and binary exp occurances
    private int ifCount = 0;
    private int whileCount = 0;
    private int binaryExpCount = 0;


    public StatsCounter() {
    }

    // Getters and setters

    public int getBinaryExpCount() {
        return binaryExpCount;
    }

    public void setBinaryExpCount(int binaryExpCount) {
        this.binaryExpCount = binaryExpCount;
    }

    public int getIfCount() {
        return ifCount;
    }

    public void setIfCount(int ifCount) {
        this.ifCount = ifCount;
    }

    public int getWhileCount() {
        return whileCount;
    }

    public void setWhileCount(int whileCount) {
        this.whileCount = whileCount;
    }

    public void printStatistics() {
        System.out.println("");
        System.out.println("--- Summary Statistics ---");
        System.out.println("If Count: " + ifCount);
        System.out.println("While Count: " + whileCount);
        System.out.println("Binary Exp Count: " + binaryExpCount);
        System.out.println("");

    }

    @Override
    public Void visitAssignCommand(AssignCommand ast, Void unused) {
        return null;
    }

    @Override
    public Void visitCallCommand(CallCommand ast, Void unused) {
        return null;
    }

    @Override
    public Void visitEmptyCommand(EmptyCommand ast, Void unused) {
        return null;
    }

    @Override
    public Void visitIfCommand(IfCommand ast, Void unused) {
        ifCount++;
        return null;
    }

    @Override
    public Void visitLetCommand(LetCommand ast, Void unused) {
        return null;
    }

    @Override
    public Void visitSequentialCommand(SequentialCommand ast, Void unused) {
        return null;
    }

    @Override
    public Void visitWhileCommand(WhileCommand ast, Void unused) {
        whileCount++;
        return null;
    }

    @Override
    public Void visitWhileCenterCommand(WhileCenterCommand ast, Void unused) {
        return null;
    }

    @Override
    public Void visitArrayExpression(ArrayExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression ast, Void unused) {
        binaryExpCount++;
        return null;
    }

    @Override
    public Void visitCallExpression(CallExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitCharacterExpression(CharacterExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitEmptyExpression(EmptyExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitIfExpression(IfExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitIntegerExpression(IntegerExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitLetExpression(LetExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitRecordExpression(RecordExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression ast, Void unused) {
        return null;
    }

    @Override
    public Void visitVnameExpression(VnameExpression ast, Void unused) {
        return null;
    }
}
