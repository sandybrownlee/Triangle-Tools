/*
 * @(#)Scanner.java                        2.1 2003/10/07
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

public final class Scanner {

	private SourceFile sourceFile;
	private boolean debug;

	private char currentChar;
	private StringBuffer currentSpelling;
	private boolean currentlyScanningToken;

	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	// isOperator returns true iff the given character is an operator character.

	private boolean isOperator(char c) {
		return (c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '<' || c == '>' || c == '\\'
				|| c == '&' || c == '@' || c == '%' || c == '^' || c == '?' || c == '|');
	}

	///////////////////////////////////////////////////////////////////////////////

	public Scanner(SourceFile source) {
		sourceFile = source;
		currentChar = sourceFile.getSource();
		debug = false;
	}

	public void enableDebugging() {
		debug = true;
	}

	//TakeIt() reads the next character from the sourceFile. If we are currently in the middle of scanning a token,
	// the current character is also added to the spelling of the current token.
	private void takeIt() {
		if (currentlyScanningToken)
			currentSpelling.append(currentChar);
		currentChar = sourceFile.getSource();
	}

	// ScanSeparator() looks at the currentChar. If it’s a !, that is, the beginning of a comment,
	// it will then keep calling takeIt() until we reach the end of the line or the end of the file.

	private void scanSeparator() {
		switch (currentChar) {
		
		// comment
		case '!':
			case '#' : {
			takeIt();
			//EOL = End Of Line, EOT = End Of File
			while ((currentChar != SourceFile.EOL) && (currentChar != SourceFile.EOT))
				takeIt();
			if (currentChar == SourceFile.EOL)
				takeIt();
		}
			break;

			case '$' : {
				takeIt();
				while ((currentChar != '$') && (currentChar != SourceFile.EOT))
					takeIt();
				if (currentChar == '$')
					takeIt();
			}
			break;

		// whitespace
		case ' ':
		case '\n':
		case '\r':
		case '\t':
			takeIt();
			break;
		}
	}

	//scanToken() looks at the first character of the token and
	private int scanToken() {

		switch (currentChar) {

		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
			//if it’s a letter, this is deemed to be an identifier it calls takeIt() recursively to get the
			// next letter. This will stop if it hits a non-letter or non-digit.
			takeIt();
			while (isLetter(currentChar) || isDigit(currentChar))
				takeIt();
			return Token.IDENTIFIER;

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			takeIt();
			//If it’s a digit, this is deemed to be an integer literal, so it calls takeIt() to get the rest of
			//the number in question. This will stop if it hits a non-digit.
			while (isDigit(currentChar))
				takeIt();
			return Token.INTLITERAL;

		case '+':
		case '-':
		case '*':
		case '/':
		case '=':
		case '<':
		case '>':
		case '\\':
		case '&':
		case '@':
		case '%':
		case '^':
		case '?':
			case '|':
				//If it’s a +, - etc., it’s deemed to be an operator, so it calls takeIt() to get the rest of the
				//operator in question.
			takeIt();
			while (isOperator(currentChar))
				takeIt();
			return Token.OPERATOR;

		case '\'':
			takeIt();
			takeIt(); // the quoted character
			if (currentChar == '\'') {
				takeIt();
				return Token.CHARLITERAL;
			} else
				return Token.ERROR;

			//Any other characters are deemed to be single characters; if they have a “kind” then that’s
			//selected, otherwise Token.ERROR is returned.
		case '.':
			takeIt();
			return Token.DOT;

		case ':':
			takeIt();
			if (currentChar == '=') {
				takeIt();
				return Token.BECOMES;
			} else
				return Token.COLON;

		case ';':
			takeIt();
			return Token.SEMICOLON;

		case ',':
			takeIt();
			return Token.COMMA;

		case '~':
			takeIt();
			return Token.IS;

		case '(':
			takeIt();
			return Token.LPAREN;

		case ')':
			takeIt();
			return Token.RPAREN;

		case '[':
			takeIt();
			return Token.LBRACKET;

		case ']':
			takeIt();
			return Token.RBRACKET;

		case '{':
			takeIt();
			return Token.LCURLY;

		case '}':
			takeIt();
			return Token.RCURLY;

		case SourceFile.EOT:
			return Token.EOT;

		default:
			takeIt();
			return Token.ERROR;
		}
	}

	//scan() starts the process of reading a token. It looks at the current character,
	// which should be the first one of a new token.
	// If it’s a whitespace or a ! then it calls scanSeparator() to skip forwards until we reach something more interesting.
	// Otherwise, it decides that we’re reading a token, and calls scanToken to decide what to do.
	public Token scan() {
		Token tok;
		SourcePosition pos;
		int kind;

		currentlyScanningToken = false;
		// skip any whitespace or comments
		while (currentChar == '!' || currentChar == ' ' || currentChar == '\n' || currentChar == '\r'
				|| currentChar == '\t' || currentChar == '#' || currentChar == '$')
			scanSeparator();

		currentlyScanningToken = true;
		currentSpelling = new StringBuffer("");
		pos = new SourcePosition();
		pos.start = sourceFile.getCurrentLine();

		kind = scanToken();

		pos.finish = sourceFile.getCurrentLine();
		tok = new Token(kind, currentSpelling.toString(), pos);
		if (debug)
			System.out.println(tok);
		return tok;
	}

}
