package triangle.codeGenerator;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import triangle.Compiler;
import triangle.abstractMachine.Interpreter;;

public class TestEncoder {
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Before
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @Test
    public void testDecrement() {
        compile("C:/Users/milif/Desktop/Triangle-Tools/programs/decrement.tri");
        testOut.reset();
        final String testString = "5";
        provideInput(testString);
        Interpreter.main(new String[0]);
        String[] expected = {"4","3"} ;
        String[] output = getOutput().split("\\n");
        assertEquals(expected[0], output[1].trim());
        assertEquals(expected[1], output[2].trim());
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    private void compile(String filename) {
        // SourceFile source =
        // SourceFile.ofPath(this.getClass().getResource(filename).getFile().toString());
        String[] args = { "-i", filename };
        Compiler.main(args);
    }

    
}