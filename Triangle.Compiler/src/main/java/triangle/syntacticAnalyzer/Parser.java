/*
 * @(#)Parser.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package triangle.syntacticAnalyzer;

import triangle.ErrorReporter;
import triangle.abstractSyntaxTrees.Program;
import triangle.abstractSyntaxTrees.actuals.ActualParameter;
import triangle.abstractSyntaxTrees.actuals.ActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.ConstActualParameter;
import triangle.abstractSyntaxTrees.actuals.EmptyActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.FuncActualParameter;
import triangle.abstractSyntaxTrees.actuals.MultipleActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.ProcActualParameter;
import triangle.abstractSyntaxTrees.actuals.SingleActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.VarActualParameter;
import triangle.abstractSyntaxTrees.aggregates.ArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.MultipleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.MultipleRecordAggregate;
import triangle.abstractSyntaxTrees.aggregates.RecordAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleRecordAggregate;
import triangle.abstractSyntaxTrees.commands.AssignCommand;
import triangle.abstractSyntaxTrees.commands.CallCommand;
import triangle.abstractSyntaxTrees.commands.Command;
import triangle.abstractSyntaxTrees.commands.EmptyCommand;
import triangle.abstractSyntaxTrees.commands.IfCommand;
import triangle.abstractSyntaxTrees.commands.LetCommand;
import triangle.abstractSyntaxTrees.commands.SequentialCommand;
import triangle.abstractSyntaxTrees.commands.WhileCommand;
import triangle.abstractSyntaxTrees.declarations.ConstDeclaration;
import triangle.abstractSyntaxTrees.declarations.Declaration;
import triangle.abstractSyntaxTrees.declarations.FuncDeclaration;
import triangle.abstractSyntaxTrees.declarations.ProcDeclaration;
import triangle.abstractSyntaxTrees.declarations.SequentialDeclaration;
import triangle.abstractSyntaxTrees.declarations.VarDeclaration;
import triangle.abstractSyntaxTrees.expressions.ArrayExpression;
import triangle.abstractSyntaxTrees.expressions.BinaryExpression;
import triangle.abstractSyntaxTrees.expressions.CallExpression;
import triangle.abstractSyntaxTrees.expressions.CharacterExpression;
import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.expressions.IfExpression;
import triangle.abstractSyntaxTrees.expressions.IntegerExpression;
import triangle.abstractSyntaxTrees.expressions.LetExpression;
import triangle.abstractSyntaxTrees.expressions.RecordExpression;
import triangle.abstractSyntaxTrees.expressions.UnaryExpression;
import triangle.abstractSyntaxTrees.expressions.VnameExpression;
import triangle.abstractSyntaxTrees.formals.ConstFormalParameter;
import triangle.abstractSyntaxTrees.formals.EmptyFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.FormalParameter;
import triangle.abstractSyntaxTrees.formals.FormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.FuncFormalParameter;
import triangle.abstractSyntaxTrees.formals.MultipleFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.ProcFormalParameter;
import triangle.abstractSyntaxTrees.formals.SingleFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.VarFormalParameter;
import triangle.abstractSyntaxTrees.terminals.CharacterLiteral;
import triangle.abstractSyntaxTrees.terminals.Identifier;
import triangle.abstractSyntaxTrees.terminals.IntegerLiteral;
import triangle.abstractSyntaxTrees.terminals.Operator;
import triangle.abstractSyntaxTrees.types.ArrayTypeDenoter;
import triangle.abstractSyntaxTrees.types.FieldTypeDenoter;
import triangle.abstractSyntaxTrees.types.MultipleFieldTypeDenoter;
import triangle.abstractSyntaxTrees.types.RecordTypeDenoter;
import triangle.abstractSyntaxTrees.types.SimpleTypeDenoter;
import triangle.abstractSyntaxTrees.types.SingleFieldTypeDenoter;
import triangle.abstractSyntaxTrees.types.TypeDeclaration;
import triangle.abstractSyntaxTrees.types.TypeDenoter;
import triangle.abstractSyntaxTrees.vnames.DotVname;
import triangle.abstractSyntaxTrees.vnames.SimpleVname;
import triangle.abstractSyntaxTrees.vnames.SubscriptVname;
import triangle.abstractSyntaxTrees.vnames.Vname;
import triangle.abstractSyntaxTrees.commands.RepeatCommand;

public class Parser {

	private Scanner lexicalAnalyser;
	private ErrorReporter errorReporter;
	private Token currentToken;
	private SourcePosition previousTokenPosition;

	public Parser(Scanner lexer, ErrorReporter reporter) {
		lexicalAnalyser = lexer;
		errorReporter = reporter;
		previousTokenPosition = new SourcePosition();
	}

	// accept checks whether the current token matches tokenExpected.
	// If so, fetches the next token.
	// If not, reports a syntactic error.

	void accept(int tokenExpected) throws SyntaxError {
		if (currentToken.kind == tokenExpected) {
			previousTokenPosition = currentToken.position;
			currentToken = lexicalAnalyser.scan();
		} else {
			syntacticError("\"%\" expected here", Token.spell(tokenExpected));
		}
	}

	void acceptIt() {
		previousTokenPosition = currentToken.position;
		currentToken = lexicalAnalyser.scan();
	}

	// start records the position of the start of a phrase.
	// This is defined to be the position of the first
	// character of the first token of the phrase.

	void start(SourcePosition position) {
		position.start = currentToken.position.start;
	}

	// finish records the position of the end of a phrase.
	// This is defined to be the position of the last
	// character of the last token of the phrase.

	void finish(SourcePosition position) {
		position.finish = previousTokenPosition.finish;
	}

	void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
		SourcePosition pos = currentToken.position;
		errorReporter.reportError(messageTemplate, tokenQuoted, pos);
		throw (new SyntaxError());
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// PROGRAMS
	//
	///////////////////////////////////////////////////////////////////////////////

	public Program parseProgram() {

		Program programAST = null;

		previousTokenPosition.start = 0;
		previousTokenPosition.finish = 0;
		currentToken = lexicalAnalyser.scan();

		try {
			Command cAST = parseCommand();
			programAST = new Program(cAST, previousTokenPosition);
			if (currentToken.kind != Token.EOT) {
				syntacticError("\"%\" not expected after end of program", currentToken.spelling);
			}
		} catch (SyntaxError s) {
			return null;
		}
		return programAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// LITERALS
	//
	///////////////////////////////////////////////////////////////////////////////

	// parseIntegerLiteral parses an integer-literal, and constructs
	// a leaf AST to represent it.

	IntegerLiteral parseIntegerLiteral() throws SyntaxError {
		IntegerLiteral IL = null;

		if (currentToken.kind == Token.INTLITERAL) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			IL = new IntegerLiteral(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			IL = null;
			syntacticError("integer literal expected here", "");
		}
		return IL;
	}

	// parseCharacterLiteral parses a character-literal, and constructs a leaf
	// AST to represent it.

	CharacterLiteral parseCharacterLiteral() throws SyntaxError {
		CharacterLiteral CL = null;

		if (currentToken.kind == Token.CHARLITERAL) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			CL = new CharacterLiteral(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			CL = null;
			syntacticError("character literal expected here", "");
		}
		return CL;
	}

	// parseIdentifier parses an identifier, and constructs a leaf AST to
	// represent it.

	Identifier parseIdentifier() throws SyntaxError {
		Identifier I = null;

		if (currentToken.kind == Token.IDENTIFIER) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			I = new Identifier(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			I = null;
			syntacticError("identifier expected here", "");
		}
		return I;
	}

	// parseOperator parses an operator, and constructs a leaf AST to
	// represent it.

	Operator parseOperator() throws SyntaxError {
		Operator O = null;

		if (currentToken.kind == Token.OPERATOR) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			O = new Operator(spelling, previousTokenPosition);
			currentToken = lexicalAnalyser.scan();
		} else {
			O = null;
			syntacticError("operator expected here", "");
		}
		return O;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// COMMANDS
	//
	///////////////////////////////////////////////////////////////////////////////

	// parseCommand parses the command, and constructs an AST
	// to represent its phrase structure.

	Command parseCommand() throws SyntaxError {
		Command commandAST = null; // in case there's a syntactic error

		SourcePosition commandPos = new SourcePosition();

		start(commandPos);
		commandAST = parseSingleCommand();
		while (currentToken.kind == Token.SEMICOLON) {
			acceptIt();
			Command c2AST = parseSingleCommand();
			finish(commandPos);
			commandAST = new SequentialCommand(commandAST, c2AST, commandPos);
		}
		return commandAST;
	}

	Command parseSingleCommand() throws SyntaxError {
		Command commandAST = null; // in case there's a syntactic error

		SourcePosition commandPos = new SourcePosition();
		start(commandPos);

		switch (currentToken.kind) {
		case Token.IDENTIFIER: {
			Identifier iAST = parseIdentifier();
			if (currentToken.kind == Token.LPAREN) {
				acceptIt();
				ActualParameterSequence apsAST = parseActualParameterSequence();
				accept(Token.RPAREN);
				finish(commandPos);
				commandAST = new CallCommand(iAST, apsAST, commandPos);

			} else {
				Vname vAST = parseRestOfVname(iAST);
				if (currentToken.kind == Token.OPERATOR && currentToken.spelling.equals("--")) {
					acceptIt();

					//Create interliteral for the -1
					IntegerLiteral il = new IntegerLiteral("-1", commandPos);

					//Wraps in an IntegerExpression
					IntegerExpression ie = new IntegerExpression(il, commandPos);

					// Variable name gets wrapped in VnameExpression
					VnameExpression vne = new VnameExpression(vAST, commandPos);

					// Operator will be - (each operator is defined by its spelling)
					Operator op = new Operator("-", commandPos);

					// Assemble expression into a BinaryExpression for a - 1
					Expression eAST = new BinaryExpression(vne, op, ie, commandPos);

					// Sets last line of command for debugging
					finish(commandPos);

					// Create assignment with binary expression on right
					commandAST = new AssignCommand(vAST, eAST, commandPos);

				}
				else {
					accept(Token.BECOMES);
					Expression eAST = parseExpression();
					finish(commandPos);
					commandAST = new AssignCommand(vAST, eAST, commandPos);
				}
			}
		}
			break;

		case Token.BEGIN:
			acceptIt();
			commandAST = parseCommand();
			accept(Token.END);
			break;

		case Token.LET: {
			acceptIt();
			Declaration dAST = parseDeclaration();
			accept(Token.IN);
			Command cAST = parseSingleCommand();
			finish(commandPos);
			commandAST = new LetCommand(dAST, cAST, commandPos);
		}
			break;

		case Token.IF: {
			acceptIt();
			Expression eAST = parseExpression();
			accept(Token.THEN);
			Command c1AST = parseSingleCommand();
			accept(Token.ELSE);
			Command c2AST = parseSingleCommand();
			finish(commandPos);
			commandAST = new IfCommand(eAST, c1AST, c2AST, commandPos);
		}
			break;

		case Token.WHILE: {
			acceptIt();
			Expression eAST = parseExpression();
			accept(Token.DO);
			Command cAST = parseSingleCommand();
			finish(commandPos);
			commandAST = new WhileCommand(eAST, cAST, commandPos);
		}
			break;

			case Token.REPEAT: {
				acceptIt();
				Command cAST = parseSingleCommand();
				accept(Token.UNTIL);
				Expression eAST = parseExpression();
				finish(commandPos);
				commandAST = new RepeatCommand(eAST, cAST, commandPos);
			}
			break;

		case Token.SEMICOLON:
		case Token.END:
		case Token.ELSE:
		case Token.IN:
		case Token.EOT:

			finish(commandPos);
			commandAST = new EmptyCommand(commandPos);
			break;

		default:
			syntacticError("\"%\" cannot start a command", currentToken.spelling);
			break;

		}

		return commandAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// EXPRESSIONS
	//
	///////////////////////////////////////////////////////////////////////////////

	Expression parseExpression() throws SyntaxError {
		Expression expressionAST = null; // in case there's a syntactic error

		SourcePosition expressionPos = new SourcePosition();

		start(expressionPos);

		switch (currentToken.kind) {

		case Token.LET: {
			acceptIt();
			Declaration dAST = parseDeclaration();
			accept(Token.IN);
			Expression eAST = parseExpression();
			finish(expressionPos);
			expressionAST = new LetExpression(dAST, eAST, expressionPos);
		}
			break;

		case Token.IF: {
			acceptIt();
			Expression e1AST = parseExpression();
			accept(Token.THEN);
			Expression e2AST = parseExpression();
			accept(Token.ELSE);
			Expression e3AST = parseExpression();
			finish(expressionPos);
			expressionAST = new IfExpression(e1AST, e2AST, e3AST, expressionPos);
		}
			break;

		default:
			expressionAST = parseSecondaryExpression();
			break;
		}
		return expressionAST;
	}

	Expression parseSecondaryExpression() throws SyntaxError {
		Expression expressionAST = null; // in case there's a syntactic error

		SourcePosition expressionPos = new SourcePosition();
		start(expressionPos);

		expressionAST = parsePrimaryExpression();
		while (currentToken.kind == Token.OPERATOR) {
			Operator opAST = parseOperator();
			Expression e2AST = parsePrimaryExpression();
			expressionAST = new BinaryExpression(expressionAST, opAST, e2AST, expressionPos);
		}
		return expressionAST;
	}

	Expression parsePrimaryExpression() throws SyntaxError {
		Expression expressionAST = null; // in case there's a syntactic error

		SourcePosition expressionPos = new SourcePosition();
		start(expressionPos);

		switch (currentToken.kind) {

		case Token.INTLITERAL: {
			IntegerLiteral ilAST = parseIntegerLiteral();
			finish(expressionPos);
			expressionAST = new IntegerExpression(ilAST, expressionPos);
		}
			break;

		case Token.CHARLITERAL: {
			CharacterLiteral clAST = parseCharacterLiteral();
			finish(expressionPos);
			expressionAST = new CharacterExpression(clAST, expressionPos);
		}
			break;

		case Token.LBRACKET: {
			acceptIt();
			ArrayAggregate aaAST = parseArrayAggregate();
			accept(Token.RBRACKET);
			finish(expressionPos);
			expressionAST = new ArrayExpression(aaAST, expressionPos);
		}
			break;

		case Token.LCURLY: {
			acceptIt();
			RecordAggregate raAST = parseRecordAggregate();
			accept(Token.RCURLY);
			finish(expressionPos);
			expressionAST = new RecordExpression(raAST, expressionPos);
		}
			break;

		case Token.IDENTIFIER: {
			Identifier iAST = parseIdentifier();
			if (currentToken.kind == Token.LPAREN) {
				acceptIt();
				ActualParameterSequence apsAST = parseActualParameterSequence();
				accept(Token.RPAREN);
				finish(expressionPos);
				expressionAST = new CallExpression(iAST, apsAST, expressionPos);

			} else {
				Vname vAST = parseRestOfVname(iAST);
				finish(expressionPos);
				expressionAST = new VnameExpression(vAST, expressionPos);
			}
		}
			break;

		case Token.OPERATOR: {
			Operator opAST = parseOperator();
			Expression eAST = parsePrimaryExpression();
			finish(expressionPos);
			expressionAST = new UnaryExpression(opAST, eAST, expressionPos);
		}
			break;

		case Token.LPAREN:
			acceptIt();
			expressionAST = parseExpression();
			accept(Token.RPAREN);
			break;

		default:
			syntacticError("\"%\" cannot start an expression", currentToken.spelling);
			break;

		}
		return expressionAST;
	}

	RecordAggregate parseRecordAggregate() throws SyntaxError {
		RecordAggregate aggregateAST = null; // in case there's a syntactic error

		SourcePosition aggregatePos = new SourcePosition();
		start(aggregatePos);

		Identifier iAST = parseIdentifier();
		accept(Token.IS);
		Expression eAST = parseExpression();

		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			RecordAggregate aAST = parseRecordAggregate();
			finish(aggregatePos);
			aggregateAST = new MultipleRecordAggregate(iAST, eAST, aAST, aggregatePos);
		} else {
			finish(aggregatePos);
			aggregateAST = new SingleRecordAggregate(iAST, eAST, aggregatePos);
		}
		return aggregateAST;
	}

	ArrayAggregate parseArrayAggregate() throws SyntaxError {
		ArrayAggregate aggregateAST = null; // in case there's a syntactic error

		SourcePosition aggregatePos = new SourcePosition();
		start(aggregatePos);

		Expression eAST = parseExpression();
		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			ArrayAggregate aAST = parseArrayAggregate();
			finish(aggregatePos);
			aggregateAST = new MultipleArrayAggregate(eAST, aAST, aggregatePos);
		} else {
			finish(aggregatePos);
			aggregateAST = new SingleArrayAggregate(eAST, aggregatePos);
		}
		return aggregateAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// VALUE-OR-VARIABLE NAMES
	//
	///////////////////////////////////////////////////////////////////////////////

	Vname parseVname() throws SyntaxError {
		Vname vnameAST = null; // in case there's a syntactic error
		Identifier iAST = parseIdentifier();
		vnameAST = parseRestOfVname(iAST);
		return vnameAST;
	}

	Vname parseRestOfVname(Identifier identifierAST) throws SyntaxError {
		SourcePosition vnamePos = new SourcePosition();
		vnamePos = identifierAST.getPosition();
		Vname vAST = new SimpleVname(identifierAST, vnamePos);

		while (currentToken.kind == Token.DOT || currentToken.kind == Token.LBRACKET) {

			if (currentToken.kind == Token.DOT) {
				acceptIt();
				Identifier iAST = parseIdentifier();
				vAST = new DotVname(vAST, iAST, vnamePos);
			} else {
				acceptIt();
				Expression eAST = parseExpression();
				accept(Token.RBRACKET);
				finish(vnamePos);
				vAST = new SubscriptVname(vAST, eAST, vnamePos);
			}
		}
		return vAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// DECLARATIONS
	//
	///////////////////////////////////////////////////////////////////////////////

	Declaration parseDeclaration() throws SyntaxError {
		Declaration declarationAST = null; // in case there's a syntactic error

		SourcePosition declarationPos = new SourcePosition();
		start(declarationPos);
		declarationAST = parseSingleDeclaration();
		while (currentToken.kind == Token.SEMICOLON) {
			acceptIt();
			Declaration d2AST = parseSingleDeclaration();
			finish(declarationPos);
			declarationAST = new SequentialDeclaration(declarationAST, d2AST, declarationPos);
		}
		return declarationAST;
	}

	Declaration parseSingleDeclaration() throws SyntaxError {
		Declaration declarationAST = null; // in case there's a syntactic error

		SourcePosition declarationPos = new SourcePosition();
		start(declarationPos);

		switch (currentToken.kind) {

		case Token.CONST: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.IS);
			Expression eAST = parseExpression();
			finish(declarationPos);
			declarationAST = new ConstDeclaration(iAST, eAST, declarationPos);
		}
			break;

		case Token.VAR: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.COLON);
			TypeDenoter tAST = parseTypeDenoter();
			finish(declarationPos);
			declarationAST = new VarDeclaration(iAST, tAST, declarationPos);
		}
			break;

		case Token.PROC: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.LPAREN);
			FormalParameterSequence fpsAST = parseFormalParameterSequence();
			accept(Token.RPAREN);
			accept(Token.IS);
			Command cAST = parseSingleCommand();
			finish(declarationPos);
			declarationAST = new ProcDeclaration(iAST, fpsAST, cAST, declarationPos);
		}
			break;

		case Token.FUNC: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.LPAREN);
			FormalParameterSequence fpsAST = parseFormalParameterSequence();
			accept(Token.RPAREN);
			accept(Token.COLON);
			TypeDenoter tAST = parseTypeDenoter();
			accept(Token.IS);
			Expression eAST = parseExpression();
			finish(declarationPos);
			declarationAST = new FuncDeclaration(iAST, fpsAST, tAST, eAST, declarationPos);
		}
			break;

		case Token.TYPE: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.IS);
			TypeDenoter tAST = parseTypeDenoter();
			finish(declarationPos);
			declarationAST = new TypeDeclaration(iAST, tAST, declarationPos);
		}
			break;

		default:
			syntacticError("\"%\" cannot start a declaration", currentToken.spelling);
			break;

		}
		return declarationAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// PARAMETERS
	//
	///////////////////////////////////////////////////////////////////////////////

	FormalParameterSequence parseFormalParameterSequence() throws SyntaxError {
		FormalParameterSequence formalsAST;

		SourcePosition formalsPos = new SourcePosition();

		start(formalsPos);
		if (currentToken.kind == Token.RPAREN) {
			finish(formalsPos);
			formalsAST = new EmptyFormalParameterSequence(formalsPos);

		} else {
			formalsAST = parseProperFormalParameterSequence();
		}
		return formalsAST;
	}

	FormalParameterSequence parseProperFormalParameterSequence() throws SyntaxError {
		FormalParameterSequence formalsAST = null; // in case there's a syntactic error;

		SourcePosition formalsPos = new SourcePosition();
		start(formalsPos);
		FormalParameter fpAST = parseFormalParameter();
		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			FormalParameterSequence fpsAST = parseProperFormalParameterSequence();
			finish(formalsPos);
			formalsAST = new MultipleFormalParameterSequence(fpAST, fpsAST, formalsPos);

		} else {
			finish(formalsPos);
			formalsAST = new SingleFormalParameterSequence(fpAST, formalsPos);
		}
		return formalsAST;
	}

	FormalParameter parseFormalParameter() throws SyntaxError {
		FormalParameter formalAST = null; // in case there's a syntactic error;

		SourcePosition formalPos = new SourcePosition();
		start(formalPos);

		switch (currentToken.kind) {

		case Token.IDENTIFIER: {
			Identifier iAST = parseIdentifier();
			accept(Token.COLON);
			TypeDenoter tAST = parseTypeDenoter();
			finish(formalPos);
			formalAST = new ConstFormalParameter(iAST, tAST, formalPos);
		}
			break;

		case Token.VAR: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.COLON);
			TypeDenoter tAST = parseTypeDenoter();
			finish(formalPos);
			formalAST = new VarFormalParameter(iAST, tAST, formalPos);
		}
			break;

		case Token.PROC: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.LPAREN);
			FormalParameterSequence fpsAST = parseFormalParameterSequence();
			accept(Token.RPAREN);
			finish(formalPos);
			formalAST = new ProcFormalParameter(iAST, fpsAST, formalPos);
		}
			break;

		case Token.FUNC: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			accept(Token.LPAREN);
			FormalParameterSequence fpsAST = parseFormalParameterSequence();
			accept(Token.RPAREN);
			accept(Token.COLON);
			TypeDenoter tAST = parseTypeDenoter();
			finish(formalPos);
			formalAST = new FuncFormalParameter(iAST, fpsAST, tAST, formalPos);
		}
			break;

		default:
			syntacticError("\"%\" cannot start a formal parameter", currentToken.spelling);
			break;

		}
		return formalAST;
	}

	ActualParameterSequence parseActualParameterSequence() throws SyntaxError {
		ActualParameterSequence actualsAST;

		SourcePosition actualsPos = new SourcePosition();

		start(actualsPos);
		if (currentToken.kind == Token.RPAREN) {
			finish(actualsPos);
			actualsAST = new EmptyActualParameterSequence(actualsPos);

		} else {
			actualsAST = parseProperActualParameterSequence();
		}
		return actualsAST;
	}

	ActualParameterSequence parseProperActualParameterSequence() throws SyntaxError {
		ActualParameterSequence actualsAST = null; // in case there's a syntactic error

		SourcePosition actualsPos = new SourcePosition();

		start(actualsPos);
		ActualParameter apAST = parseActualParameter();
		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			ActualParameterSequence apsAST = parseProperActualParameterSequence();
			finish(actualsPos);
			actualsAST = new MultipleActualParameterSequence(apAST, apsAST, actualsPos);
		} else {
			finish(actualsPos);
			actualsAST = new SingleActualParameterSequence(apAST, actualsPos);
		}
		return actualsAST;
	}

	ActualParameter parseActualParameter() throws SyntaxError {
		ActualParameter actualAST = null; // in case there's a syntactic error

		SourcePosition actualPos = new SourcePosition();

		start(actualPos);

		switch (currentToken.kind) {

		case Token.IDENTIFIER:
		case Token.INTLITERAL:
		case Token.CHARLITERAL:
		case Token.OPERATOR:
		case Token.LET:
		case Token.IF:
		case Token.LPAREN:
		case Token.LBRACKET:
		case Token.LCURLY: {
			Expression eAST = parseExpression();
			finish(actualPos);
			actualAST = new ConstActualParameter(eAST, actualPos);
		}
			break;

		case Token.VAR: {
			acceptIt();
			Vname vAST = parseVname();
			finish(actualPos);
			actualAST = new VarActualParameter(vAST, actualPos);
		}
			break;

		case Token.PROC: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			finish(actualPos);
			actualAST = new ProcActualParameter(iAST, actualPos);
		}
			break;

		case Token.FUNC: {
			acceptIt();
			Identifier iAST = parseIdentifier();
			finish(actualPos);
			actualAST = new FuncActualParameter(iAST, actualPos);
		}
			break;

		default:
			syntacticError("\"%\" cannot start an actual parameter", currentToken.spelling);
			break;

		}
		return actualAST;
	}

	///////////////////////////////////////////////////////////////////////////////
	//
	// TYPE-DENOTERS
	//
	///////////////////////////////////////////////////////////////////////////////

	TypeDenoter parseTypeDenoter() throws SyntaxError {
		TypeDenoter typeAST = null; // in case there's a syntactic error
		SourcePosition typePos = new SourcePosition();

		start(typePos);

		switch (currentToken.kind) {

		case Token.IDENTIFIER: {
			Identifier iAST = parseIdentifier();
			finish(typePos);
			typeAST = new SimpleTypeDenoter(iAST, typePos);
		}
			break;

		case Token.ARRAY: {
			acceptIt();
			IntegerLiteral ilAST = parseIntegerLiteral();
			accept(Token.OF);
			TypeDenoter tAST = parseTypeDenoter();
			finish(typePos);
			typeAST = new ArrayTypeDenoter(ilAST, tAST, typePos);
		}
			break;

		case Token.RECORD: {
			acceptIt();
			FieldTypeDenoter fAST = parseFieldTypeDenoter();
			accept(Token.END);
			finish(typePos);
			typeAST = new RecordTypeDenoter(fAST, typePos);
		}
			break;

		default:
			syntacticError("\"%\" cannot start a type denoter", currentToken.spelling);
			break;

		}
		return typeAST;
	}

	FieldTypeDenoter parseFieldTypeDenoter() throws SyntaxError {
		FieldTypeDenoter fieldAST = null; // in case there's a syntactic error

		SourcePosition fieldPos = new SourcePosition();

		start(fieldPos);
		Identifier iAST = parseIdentifier();
		accept(Token.COLON);
		TypeDenoter tAST = parseTypeDenoter();
		if (currentToken.kind == Token.COMMA) {
			acceptIt();
			FieldTypeDenoter fAST = parseFieldTypeDenoter();
			finish(fieldPos);
			fieldAST = new MultipleFieldTypeDenoter(iAST, tAST, fAST, fieldPos);
		} else {
			finish(fieldPos);
			fieldAST = new SingleFieldTypeDenoter(iAST, tAST, fieldPos);
		}
		return fieldAST;
	}
}
