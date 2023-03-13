package triangle.Compiler;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

looppublic class ParserTest {

    @Test
    public void testWhileLoopWithCurlyBrackets() {
        String input = "PROGRAM\n"
                     + "  VAR x : INTEGER;\n"
                     + "BEGIN\n"
                     + "  x := 0;\n"
                     + "  WHILE x < 5 DO {\n"
                     + "    x := x + 1;\n"
                     + "  }\n"
                     + "END\n";
        try {
            Parser parser = new Parser(input);
            parser.parse();
            assertTrue(true);
        } catch (SyntaxError e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIfStatementWithCurlyBrackets() {
        String input = "PROGRAM\n"
                     + "  VAR x : INTEGER;\n"
                     + "BEGIN\n"
                     + "  x := 5;\n"
                     + "  IF x = 5 THEN {\n"
                     + "    x := x + 1;\n"
                     + "  }\n"
                     + "END\n";
        try {
            Parser parser = new Parser(input);
            parser.parse();
            assertTrue(true);
        } catch (SyntaxError e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testNestedCurlyBrackets() {
        String input = "PROGRAM\n"
                     + "  VAR x : INTEGER;\n"
                     + "BEGIN\n"
                     + "  x := 0;\n"
                     + "  WHILE x < 5 DO {\n"
                     + "    x := x + 1;\n"
                     + "    IF x = 2 THEN {\n"
                     + "      x := x * 2;\n"
                     + "    }\n"
                     + "  }\n"
                     + "END\n";
        try {
            Parser parser = new Parser(input);
            parser.parse();
            assertTrue(true);
        } catch (SyntaxError e) {
            fail(e.getMessage());
        }
    }

}